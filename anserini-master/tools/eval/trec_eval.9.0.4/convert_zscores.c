/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/

#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "functions.h"
#include "trec_format.h"
/*
Convert all values in a trec_eval object for a single query from being
a raw score into being a zscore: score expressed in units of standard
deviation from a mean.  Means and standard deviations from a reference
set of runs are given by all_zscores.

If the mean and stddev values for this measure and query are not found 
in all_zscores, then the value is set to MISSING_ZSCORE_VALUE.
*/

static ZSCORES *find_qid (const ALL_ZSCORES *all_zscores, const char *qid);
static ZSCORE_QID *find_meas (const ZSCORES *zscores, const char *meas);

int 
te_convert_to_zscore (const ALL_ZSCORES *all_zscores, TREC_EVAL *q_eval)
{
    int return_value = 1;
    long i;
    ZSCORES *zscores;
    ZSCORE_QID *zscores_qid;

    /* Do a binary search to find q_eval->qid */
    if (NULL == (zscores = find_qid (all_zscores, q_eval->qid))) {
	/* If q_eval->qid not found, set all values in q_eval to
	   MISSING_ZSCORE_VALUE and return 0 */
	for (i = 0; i < q_eval->num_values; i++)
	    q_eval->values[i].value = MISSING_ZSCORE_VALUE;
	return (0);
    }

    /* For each measure in q_eval, do a binary search for that measure
       within zscores */
    for (i = 0; i < q_eval->num_values; i++) {
	if (NULL == (zscores_qid = find_meas (zscores,
					      q_eval->values[i].name))) {
	    q_eval->values[i].value = MISSING_ZSCORE_VALUE;
	    return_value = 0;
	}
	else {
	    if (zscores_qid->stddev) 
		q_eval->values[i].value =  
		    (q_eval->values[i].value - zscores_qid->mean) / 
		    zscores_qid->stddev;
	    else {
		if (q_eval->values[i].value == zscores_qid->mean)
		    q_eval->values[i].value = 0;
		else {
		    q_eval->values[i].value = MISSING_ZSCORE_VALUE;
		    return_value = 0;
		}
	    }
	}
    }

    return (return_value);
}

static ZSCORES *
find_qid (const ALL_ZSCORES *all_zscores, const char *qid)
{
    ZSCORES *base;
    long start, end, current;
    int result;

    base = all_zscores->q_zscores;
    end = all_zscores->num_q_zscores;
    start = 0;
    while (start <= end) {
	current = (start + end) / 2;
	result = strcmp (qid, base[current].qid);
	if (result < 0)
	    end = current - 1;
	else if (result > 0)
	    start = current + 1;
	else
	    return (&base[current]);
    }
    return (NULL);
}

static ZSCORE_QID *
find_meas (const ZSCORES *zscores, const char *meas)
{
    ZSCORE_QID *base;
    long start, end, current;
    int result;

    base = zscores->zscores;
    end =  zscores->num_zscores;
    start = 0;
    while (start <= end) {
	current = (start + end) / 2;
	result = strcmp (meas, base[current].meas);
	if (result < 0)
	    end = current - 1;
	else if (result > 0)
	    start = current + 1;
	else
	    return (&base[current]);
    }
    return (NULL);
}

