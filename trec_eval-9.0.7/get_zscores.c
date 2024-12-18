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


/* Read a Zscore file containing means and stddev from a reference set of runs
for each measure and qid to be calculated.
Format of Zscore file is 
     qid  measure_name  mean stddev
where measure_name and qid are strings, and mean and stddev are doubles.

Fields are separated by whitespace, string fields can contain no whitespace.
File may contain no NULL characters.

All values are stored in per query arrays within Zscores.
Each list of query judgments is sorted lexicographically by docno.
*/

/* Declarations in trec_eval.h (generic q info) for the input format */
/*
  typedef struct {
    char *meas;
    double mean;
    double stddev;
  } ZSCORE_QID;
  typedef struct {
    char *qid;
    long num_zscores;
    ZSCORE_QID *zscores;
  } ZSCORES;
  typedef struct {
    long num_q_zscores;
    ZSCORES *q_zscores;
  } ALL_ZSCORES;
*/

/* Temp structure for values in input line */
typedef struct {
    char *qid;
    char *meas;
    char *mean;
    char *stddev;
} LINES;

static int parse_zscore_line (char **start_ptr, char **qid_ptr, char **meas_ptr,
			      char **mean_ptr, char **stddev_ptr);

static int comp_lines_qid_meas ();


/* static pools of memory, allocated here and never changed.  */
static char *trec_zscores_buf = NULL;
static ZSCORE_QID *text_zscores_pool = NULL;
static ZSCORES *zscores_pool = NULL;

int
te_get_zscores (const EPI *epi, const char *zscores_file,
		ALL_ZSCORES *all_zscores)
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
    ZSCORE_QID *text_zscores_ptr;
    ZSCORES *zscores_ptr;

    /* Read entire file into memory */
    if (-1 == (fd = open (zscores_file, 0)) ||
        0 >= (size = lseek (fd, 0L, 2)) ||
        NULL == (trec_zscores_buf = malloc ((unsigned) size+2)) ||
        -1 == lseek (fd, 0L, 0) ||
        size != read (fd, trec_zscores_buf, size) ||
	-1 == close (fd)) {
        fprintf (stderr,
		 "trec_eval.get_zscores: Cannot read zscores file '%s'\n",
		 zscores_file);
        return (UNDEF);
    }

    /* Append ending newline if not present, Append NULL terminator */
    if (trec_zscores_buf[size-1] != '\n') {
	trec_zscores_buf[size] = '\n';
	size++;
    }
    trec_zscores_buf[size] = '\0';

    /* Count number of lines in file */
    num_lines = 0;
    for (ptr = trec_zscores_buf; *ptr; ptr = index(ptr,'\n')+1)
	num_lines++;

    /* Get all lines */
    if (NULL == (lines = Malloc (num_lines, LINES)))
	return (UNDEF);
    line_ptr = lines;
    ptr = trec_zscores_buf;
    while (*ptr) {
	if (UNDEF == parse_zscore_line (&ptr, &line_ptr->qid, &line_ptr->meas,
					&line_ptr->mean, &line_ptr->stddev)) {
	    fprintf (stderr, "trec_eval.get_zscores: Malformed line %ld\n",
		     (long) (line_ptr - lines + 1));
	    return (UNDEF);
	}
	line_ptr++;
    }
    num_lines = line_ptr-lines;

    /* Sort all lines by qid, then meas */
    qsort ((char *) lines,
	   (int) num_lines,
	   sizeof (LINES),
	   comp_lines_qid_meas);

    /* Go through lines and count number of qid */
    num_qid = 1;
    for (i = 1; i < num_lines; i++) {
	if (strcmp (lines[i-1].qid, lines[i].qid))
	    /* New query */
	    num_qid++;
    }

    /* Allocate space for queries */
    if (NULL == (zscores_pool = Malloc (num_qid, ZSCORES)) ||
	NULL == (text_zscores_pool = Malloc (num_lines, ZSCORE_QID)))
	return (UNDEF);

    zscores_ptr = zscores_pool;
    text_zscores_ptr = text_zscores_pool;
    
    /* Go through lines and store all info */
    current_qid = "";
    for (i = 0; i < num_lines; i++) {
	if (strcmp (current_qid, lines[i].qid)) {
	    /* New query.  End old query and start new one */
	    if (i != 0) {
		zscores_ptr->num_zscores =
		    text_zscores_ptr - zscores_ptr->zscores;
		zscores_ptr++;
	    }
	    current_qid = lines[i].qid;
	    zscores_ptr->qid = current_qid;
	    zscores_ptr->zscores = text_zscores_ptr;
	}
	text_zscores_ptr->meas = lines[i].meas;
	text_zscores_ptr->mean = atof (lines[i].mean);
	text_zscores_ptr->stddev = atof (lines[i].stddev);
	text_zscores_ptr++;
    }
    /* End last qid */
    zscores_ptr->num_zscores = text_zscores_ptr - zscores_ptr->zscores;

    all_zscores->num_q_zscores = num_qid;
    all_zscores->q_zscores = zscores_pool;

    Free (lines);
    return (1);
}

static int comp_lines_qid_meas (LINES *ptr1, LINES *ptr2)
{
    int cmp = strcmp (ptr1->qid, ptr2->qid);
    if (cmp) return (cmp);
    return (strcmp (ptr1->meas, ptr2->meas));
}

static int
parse_zscore_line (char **start_ptr, char **qid_ptr, char **meas_ptr,
		   char **mean_ptr, char **stddev_ptr)
{
    char *ptr = *start_ptr;

    /* Get qid */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    *qid_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n')  return (UNDEF);
    *ptr++ = '\0';
    /* Get meas */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    *meas_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n') return (UNDEF);
    *ptr++ = '\0';
    /* Get mean */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    *mean_ptr = ptr;
    while (! isspace (*ptr)) ptr++;
    if (*ptr == '\n') return (UNDEF);
    *ptr++ = '\0';
    /* Get stddev */
    while (*ptr != '\n' && isspace (*ptr)) ptr++;
    if (*ptr == '\n') return (UNDEF);
    *stddev_ptr = ptr;
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
te_get_zscores_cleanup ()
{
    if (trec_zscores_buf != NULL) {
	Free (trec_zscores_buf);
	trec_zscores_buf = NULL;
    }
    if (text_zscores_pool != NULL) {
	Free (text_zscores_pool);
	text_zscores_pool = NULL;
    }
    if (zscores_pool != NULL) {
	Free (zscores_pool);
	zscores_pool = NULL;
    }
    return (1);
}
