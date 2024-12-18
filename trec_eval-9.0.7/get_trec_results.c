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
#include <sys/types.h>

/* Read all retrieved results information from trec_results_file.
Read text tuples from trec_results_file of the form
     030  Q0  ZF08-175-870  0   4238   prise1
     qid iter   docno      rank  sim   run_id
giving TREC document numbers (a string) retrieved by query qid 
(a string) with similarity sim (a float).  The other fields are ignored,
with the exception that the run_id field of the last line is kept and
output.  In particular, note that the rank field is ignored here;
internally ranks will be assigned by sorting by the sim field with ties 
broken determinstically (using docno).
Sim is assumed to be higher for the docs to be retrieved first.
File may contain no NULL characters.
Any field following run_id is ignored.
The runid is stored, but only from the last content line of the file (no
check is made that it is consistent throughout).
*/

/* Temp structure for values in input line */
typedef struct {
    char *qid;
    char *docno;
    char *sim;
} LINES;

static int parse_results_line (char **start_ptr, char **qid_ptr,
			       char **docno_ptr, char **sim_ptr,
			       char **run_id_ptr);

static int comp_lines_qid_docno ();


/* static pools of memory, allocated here and never changed.  
   Declared static so one day I can write a cleanup procedure to free them */
static char *trec_results_buf = NULL;
static TEXT_RESULTS_INFO *text_info_pool = NULL;
static TEXT_RESULTS *text_results_pool = NULL;
static RESULTS *q_results_pool = NULL;

int
te_get_trec_results (EPI *epi, char *text_results_file,
		     ALL_RESULTS *all_results)
{
    int fd;
    char *orig_buf;
    size_t size = 0;
    char *ptr;
    char *current_qid;
    long i;
    LINES *lines;
    LINES *line_ptr;
    size_t num_lines;
    long num_qid;
    char *run_id_ptr = NULL;
    /* current pointers into static pools above */
    RESULTS *q_results_ptr;
    TEXT_RESULTS_INFO *text_info_ptr;
    TEXT_RESULTS *text_results_ptr;
    
    /* mmap entire file into memory and copy it into writable memory */
    if (-1 == (fd = open (text_results_file, 0)) ||
        0 >= (size = lseek (fd, 0L, 2)) ||
        (char *) -1 == (orig_buf = (char *) mmap ((caddr_t) 0,
						  (size_t) size,
						  PROT_READ,
						  MAP_SHARED,
						  fd,
						  (off_t) 0))) {
	fprintf (stderr,
		 "trec_eval.get_results: Cannot read results file '%s'\n",
		 text_results_file);
	return (UNDEF);
    }
    if (NULL == (trec_results_buf = malloc ((size_t) size+2))) {
	fprintf (stderr,
		 "trec_eval.get_results: Cannot copy results file '%s'\n",
		 text_results_file);
	return (UNDEF);
    }
    (void) memcpy (trec_results_buf, orig_buf, size);
    if (-1 == munmap (orig_buf, size) ||
	-1 == close (fd)) {
	fprintf (stderr,
		 "trec_eval.get_results: Cannot close results file '%s'\n",
		 text_results_file);
	return (UNDEF);
    }

    /* Append ending newline if not present, Append NULL terminator */
    if (trec_results_buf[size-1] != '\n') {
	trec_results_buf[size] = '\n';
	size++;
    }
    trec_results_buf[size] = '\0';

    /* Count number of lines in file */
    num_lines = 0;
    for (ptr = trec_results_buf; *ptr; ptr = index(ptr,'\n')+1)
	num_lines++;

    /* Get all lines */
    if (NULL == (lines = Malloc (num_lines, LINES)))
	return (UNDEF);
    line_ptr = lines;
    ptr = trec_results_buf;
    while (*ptr) {
	/* Get current line */
	/* Ignore blank lines (people seem to insist on them!) */
	while (*ptr && *ptr != '\n' && isspace (*ptr)) ptr++;
	if (*ptr == '\n') {
	    ptr++;
	    continue;
	}
	if (UNDEF == parse_results_line (&ptr, &line_ptr->qid,&line_ptr->docno,
					 &line_ptr->sim, &run_id_ptr)) {
	    fprintf (stderr, "trec_eval.get_results: Malformed line %ld\n",
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
    if (NULL == (q_results_pool = Malloc (num_qid, RESULTS)) ||
	NULL == (text_info_pool = Malloc (num_qid, TEXT_RESULTS_INFO)) ||
	NULL == (text_results_pool = Malloc (num_lines, TEXT_RESULTS)))
	return (UNDEF);

    q_results_ptr = q_results_pool;
    text_info_ptr = text_info_pool;
    text_results_ptr = text_results_pool;
    
    /* Go through lines and store all info */
    current_qid = "";
    for (i = 0; i < num_lines; i++) {
	if (strcmp (current_qid, lines[i].qid)) {
	    /* New query.  End old query and start new one */
	    if (i != 0) {
		text_info_ptr->num_text_results =
		    text_results_ptr - text_info_ptr->text_results;
		text_info_ptr++;
		q_results_ptr++;
	    }
	    current_qid = lines[i].qid;
	    text_info_ptr->text_results = text_results_ptr;
	    *q_results_ptr =
		(RESULTS) {current_qid, run_id_ptr, "trec_results",
			   text_info_ptr};
	}
	text_results_ptr->docno = lines[i].docno;
	text_results_ptr->sim = atof (lines[i].sim);
	text_results_ptr++;
    }
    /* End last qid */
    text_info_ptr->num_text_results =
	text_results_ptr - text_info_ptr->text_results;

    all_results->num_q_results = num_qid;
    all_results->results = q_results_pool;

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
parse_results_line (char **start_ptr, char **qid_ptr, char **docno_ptr,
		    char **sim_ptr, char **run_id_ptr)
{
    char *ptr = *start_ptr;

    /* Get qid */
    *qid_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n')  return (UNDEF);
    *ptr++ = '\0';
    /* Skip iter */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    while (! isspace (*ptr)) ptr++;
    if (*ptr++ == '\n') return (UNDEF);
    /* Get docno */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    *docno_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n') return (UNDEF);
    *ptr++ = '\0';
    /* Skip rank */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    while (! isspace (*ptr)) ptr++;
    if (*ptr++ == '\n') return (UNDEF);
    /* Get sim */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    *sim_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n')return (UNDEF);
    *ptr++ = '\0';
    /* Get run_id */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    if (*ptr == '\n') return (UNDEF);
    *run_id_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr != '\n') {
	/* Skip over rest of line */
	*ptr++ = '\0';
	while (*ptr != '\n') ptr++;
    }
    *ptr++ = '\0';
    *start_ptr = ptr;
    return (0);
}

int 
te_get_trec_results_cleanup ()
{
    if (trec_results_buf != NULL) {
	Free (trec_results_buf);
	trec_results_buf = NULL;
    }
    if (text_info_pool != NULL) {
	Free (text_info_pool);
	text_info_pool = NULL;
    }
    if (text_results_pool != NULL) {
	Free (text_results_pool);
	text_results_pool = NULL;
    }
    if (q_results_pool != NULL) {
	Free (q_results_pool);
	q_results_pool = NULL;
    }
    return (1);
}

