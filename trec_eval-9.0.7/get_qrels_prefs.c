/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/

#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "trec_format.h"
#include <ctype.h>


/* Exactly the same as get_prefs.c except it reads a standard
   TREC qrels file as if it were a prefs file, associating
   JG with the iter field, and assigning JSG a constant '0'
   for all lines.  This normally will yield a single JG and
   a single JSG per query, with two levels of relevance if
   it is a binary relevance file, and more if a multiple
   relevance file.

Read all relevance preference information from text_qrels_prefs_file.
Preferences of user(s) for docs for a given qid is determined from
text_prefs_file, which consists of text tuples of the form
   qid  ujg   docno  rel_level
giving TREC document numbers (docno, a string) and their relevance
level (rel_level,a non-negative float) to query qid (a string) for a 
 user judgment group (ujg, a string).
Fields are separated by whitespace, string fields can contain no whitespace.
File may contain no NULL characters.

Preferences are indicated indirectly by comparing rel_level of
different docnos within the same user judgment group(JG).  A
judgment group establishes preferences between all docnos with
non-tied rel_levels within the group. Except possibly for 0.0, the
actual values of rel_level are ignored by default; they only serve to
establish a ranking within the JSG.

If a user only expresses a preference between two docs, then that user JSG
will have 2 lines in text_prefs_file:
      qid1  ujg1   docno1  3.0
      qid1  ujg1   docno2  2.0

If a user completely ranks some small number N (5-10) of docs, then N lines 
are used.
For example:
      qid1  ujg1    docno1  3.0
      qid1  ujg1    docno2  2.0
      qid1  ujg1    docno3  0.0
      qid1  ujg1    docno4  6.0
      qid1  ujg1    docno5  0.0
      qid1  ujg1    docno6  2.0
establishes a total of 13 preferences (5 with docno4 preferred, 4 with docno1 
preferred, 2 each with docno2 and docno6 preferred).

A Judgment Group (JG) conceptually represents preferences for a single
information need of a user at a single time.  Within a single JG, it
is an error if there are inconsistencies (doc A > doc B in one JSG,
but B > A or B == A in another).

Different JGs may contain contradictory preferences, These
disagreements are realistic and desirable: users (or even the same
user at different times) often do not agree with each other's
preferences.  Individual preference evaluation measures will handle
these contradictions (or confirmations) in different ways.

A rel_level of 0.0 by convention means that doc is non-relevant to the
topic (in that user's opinion).  Some preference evaluation measures
may handle 0.0 differently.  Thus when converting a preference file in
some other format into text_prefs format, do not assign a rel_level of
0.0 to a docno unless it is known that docno was considered
nonrelevant.

Handling of rel_level 0.0 separately addresses the general problem
that the number of nonrelevant docs judged for a topic can be critical
to fair evaluation - adding a couple of hundred preferences involving
nonrelevant docs (out of the possibly millions or billions in a
collection) can both change the importance of the topic when averaging
and even change whether system A scores better than system B on a
topic (even given identical retrieval on the added nonrel docs).  How
to handle this correctly for preference evaluation will be an
important future research problem.

*/

static int parse_qrels_prefs_line (char **start_ptr, char **qid_ptr,
				   char **jg_ptr, char **docno_ptr,
				   char **rel_ptr);

static int comp_lines_qid_docno ();


/* static pools of memory, allocated here and never changed.  
   Declared static so one day I can write a cleanup procedure to free them */
static char *trec_prefs_buf = NULL;
static TEXT_PREFS_INFO *text_info_pool = NULL;
static TEXT_PREFS *text_prefs_pool = NULL;
static REL_INFO *rel_info_pool = NULL;

/* Temp structure for values in input line */
typedef struct {
    char *qid;
    char *jg;
    char *docno;
    char *rel;
} LINES;

int
te_get_qrels_prefs (EPI *epi, char *text_prefs_file, ALL_REL_INFO *all_rel_info)
{
    int fd;
    int size = 0;
    char *ptr;
    char *current_qid;
    long i;
    LINES *lines;
    LINES *line_ptr;
    long num_lines;
    long num_qid;
    /* current pointers into static pools above */
    REL_INFO *rel_info_ptr;
    TEXT_PREFS_INFO *text_info_ptr;
    TEXT_PREFS *text_prefs_ptr;
    
    /* Read entire file into memory */
    if (-1 == (fd = open (text_prefs_file, 0)) ||
        0 >= (size = lseek (fd, 0L, 2)) ||
        NULL == (trec_prefs_buf = malloc ((unsigned) size+2)) ||
        -1 == lseek (fd, 0L, 0) ||
        size != read (fd, trec_prefs_buf, size) ||
	-1 == close (fd)) {
        fprintf (stderr,
		 "trec_eval.get_prefs: Cannot read prefs file '%s'\n",
		 text_prefs_file);
        return (UNDEF);
    }

    /* Append ending newline if not present, Append NULL terminator */
    if (trec_prefs_buf[size-1] != '\n') {
	trec_prefs_buf[size] = '\n';
	size++;
    }
    trec_prefs_buf[size] = '\0';

    /* Count number of lines in file */
    num_lines = 0;
    for (ptr = trec_prefs_buf; *ptr; ptr = index(ptr,'\n')+1)
	num_lines++;

    /* Get all lines */
    if (NULL == (lines = Malloc (num_lines, LINES)))
	return (UNDEF);
    line_ptr = lines;
    ptr = trec_prefs_buf;
    while (*ptr) {
	if (UNDEF == parse_qrels_prefs_line (&ptr, &line_ptr->qid,&line_ptr->jg,
					     &line_ptr->docno, &line_ptr->rel)){
	    fprintf (stderr, "trec_eval.get_qrels_prefs: Malformed line %ld\n",
		     (long) (line_ptr - lines + 1));
	    return (UNDEF);
	}
	line_ptr++;
    }
    num_lines = line_ptr-lines;

    /* Sort all lines by qid, then docno */
    qsort ((char *) lines,
	   (int) num_lines,
	   sizeof (LINES),
	   comp_lines_qid_docno);

    /* Go through lines and count number of qid */
    num_qid = 1;
    for (i = 1; i < num_lines; i++) {
	if (strcmp (lines[i-1].qid, lines[i].qid))
	    /* New query */
	    num_qid++;
    }

    /* Allocate space for queries */
    if (NULL == (rel_info_pool = Malloc (num_qid, REL_INFO)) ||
	NULL == (text_info_pool = Malloc (num_qid, TEXT_PREFS_INFO)) ||
	NULL == (text_prefs_pool = Malloc (num_lines, TEXT_PREFS)))
	return (UNDEF);

    rel_info_ptr = rel_info_pool;
    text_info_ptr = text_info_pool;
    text_prefs_ptr = text_prefs_pool;
    
    /* Go through lines and store all info */
    current_qid = "";
    for (i = 0; i < num_lines; i++) {
	if (strcmp (current_qid, lines[i].qid)) {
	    /* New query.  End old query and start new one */
	    if (i != 0) {
		text_info_ptr->num_text_prefs =
		    text_prefs_ptr - text_info_ptr->text_prefs;
		text_info_ptr++;
		rel_info_ptr++;
	    }
	    current_qid = lines[i].qid;
	    text_info_ptr->text_prefs = text_prefs_ptr;
	    *rel_info_ptr =
		(REL_INFO) {current_qid, "prefs", text_info_ptr};
	}
	text_prefs_ptr->jg = lines[i].jg;
	text_prefs_ptr->jsg = "0";
	text_prefs_ptr->rel_level = atof (lines[i].rel);
	text_prefs_ptr->docno = lines[i].docno;
	text_prefs_ptr++;
    }
    /* End last qid */
    text_info_ptr->num_text_prefs = text_prefs_ptr - text_info_ptr->text_prefs;

    all_rel_info->num_q_rels = num_qid;
    all_rel_info->rel_info = rel_info_pool;

    Free (lines);
    return (1);
}

static int comp_lines_qid_docno (LINES *ptr1, LINES *ptr2)
{
    int cmp = strcmp (ptr1->qid, ptr2->qid);
    if (cmp) return (cmp);
    return (strcmp (ptr1->docno, ptr2->docno));
}

static int
parse_qrels_prefs_line (char **start_ptr, char **qid_ptr, char**jg_ptr,
			char **docno_ptr, char **rel_ptr)
{
    char *ptr = *start_ptr;

    /* Get qid */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    *qid_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n')  return (UNDEF);
    *ptr++ = '\0';
    /* Get Judgment Group */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    *jg_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n') return (UNDEF);
    *ptr++ = '\0';
    /* Get docno */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    *docno_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n') return (UNDEF);
    *ptr++ = '\0';
    /* Get relevance */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    if (*ptr == '\n') return (UNDEF);
    *rel_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr != '\n') {
	*ptr++ = '\0';
	while (*ptr != '\n' && isspace (*ptr)) ptr++;
	if (*ptr != '\n') return (UNDEF);
    }
    *ptr++ = '\0';
    *start_ptr = ptr;
    return (0);
}

int 
te_get_qrels_prefs_cleanup ()
{
    if (trec_prefs_buf != NULL) {
	Free (trec_prefs_buf);
	trec_prefs_buf = NULL;
    }
    if (text_info_pool != NULL) {
	Free (text_info_pool);
	text_info_pool = NULL;
    }
    if (text_prefs_pool != NULL) {
	Free (text_prefs_pool);
	text_prefs_pool = NULL;
    }
    if (rel_info_pool != NULL) {
	Free (rel_info_pool);
	rel_info_pool = NULL;
    }
    return (1);
}
