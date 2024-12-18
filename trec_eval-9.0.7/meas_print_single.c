/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/

#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "functions.h"

/* Procedures for printing single query default formats of measure values.
   Used in init_meas procedure description of TREC_MEAS in trec_eval.h:
      typedef struct trec_meas {
         ...
        * Store parameters for measure in meas_params. Reserve space in
	TREC_EVAL.values for results of measure. Store individual measure
	names (possibly altered by parameters) in TREC_EVAL.values.
	Set tm->eval_index to start of reserved space *
         ...
      } TREC_MEAS;
   Measures are defined in measures.c.
*/


/* ---------------- print single measure generic procedures -------------- */

/* Measure does not require printing */
int 
te_print_single_meas_empty (const EPI *epi, const TREC_MEAS *tm,
			    const TREC_EVAL *eval)
{
    return (1);
}


/* Measure is a single float measure with no parameters, */
int 
te_print_single_meas_s_float (const EPI *epi, const TREC_MEAS *tm,
			      const TREC_EVAL *eval)
{
    if (epi->zscore_flag)
	printf ("Z%-22s\t%s\t%6.4f\n",
		eval->values[tm->eval_index].name,
		eval->qid,
		eval->values[tm->eval_index].value);
    else 
	printf ("%-22s\t%s\t%6.4f\n",
		eval->values[tm->eval_index].name,
		eval->qid,
		eval->values[tm->eval_index].value);
    return (1);
}

/* Measure is a single long measure with no parameters. */
int 
te_print_single_meas_s_long (const EPI *epi, const TREC_MEAS *tm,
			     const TREC_EVAL *eval)
{
    if (epi->zscore_flag)
	printf ("Z%-22s\t%s\t%6.4f\n",
		eval->values[tm->eval_index].name,
		eval->qid,
		eval->values[tm->eval_index].value);
    else 
	printf ("%-22s\t%s\t%ld\n",
		eval->values[tm->eval_index].name,
		eval->qid,
		(long) eval->values[tm->eval_index].value);
    return (1);
}

/* Measure is a float array with cutoffs */
int 
te_print_single_meas_a_cut (const EPI *epi, const TREC_MEAS *tm,
			    const TREC_EVAL *eval)
{
    long i;
    for (i = 0; i < tm->meas_params->num_params; i++) {
	if (epi->zscore_flag)
	    printf ("Z%-22s\t%s\t%6.4f\n",
		    eval->values[tm->eval_index+i].name,
		    eval->qid,
		    eval->values[tm->eval_index+i].value);
	else 
	    printf ("%-22s\t%s\t%6.4f\n",
		    eval->values[tm->eval_index+i].name,
		    eval->qid,
		    eval->values[tm->eval_index+i].value);
    }
    return (1);
}
