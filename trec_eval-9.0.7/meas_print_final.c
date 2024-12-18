/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/

#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "functions.h"

/* Procedures for printing of summary and cleaning up several default formats
   of measure values.
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

/* ---------------- Print final measure generic procedures -------------- */

/* Measure does not require printing or storage */
int 
te_print_final_meas_empty (const EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    return (1);
}


/* Measure is a single float measure with no parameters */
int 
te_print_final_meas_s_float (const EPI *epi, TREC_MEAS *tm,
			     TREC_EVAL *eval)
{
    if (epi->summary_flag)
	printf ("%s%-22s\t%s\t%6.4f\n",
		epi->zscore_flag ? "Z": "",
		eval->values[tm->eval_index].name,
		eval->qid,
		eval->values[tm->eval_index].value);
    return (1);
}

/* Measure is a single long measure with no parameters. */
int 
te_print_final_meas_s_long (const EPI *epi, TREC_MEAS *tm,
			    TREC_EVAL *eval)
{
    if (epi->summary_flag) {
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
    }
    return (1);
}


/* Measure is a float array with cutoffs */
int 
te_print_final_meas_a_cut (const EPI *epi, TREC_MEAS *tm,
			   TREC_EVAL *eval)
{
    long i;

    for (i = 0; i < tm->meas_params->num_params; i++) {
	if (epi->summary_flag)
	    printf ("%s%-22s\t%s\t%6.4f\n",
		    epi->zscore_flag ? "Z": "",
		    eval->values[tm->eval_index + i].name,
		    eval->qid,
		    eval->values[tm->eval_index + i].value);
	Free (eval->values[tm->eval_index + i].name);
    }
    if (tm->meas_params->printable_params) {
	Free (tm->meas_params->param_values);
	Free (tm->meas_params->printable_params);
    }

    return (1);
}

/* Measure is a single float with float params */
int 
te_print_final_meas_s_float_p (const EPI *epi, TREC_MEAS *tm,
			       TREC_EVAL *eval)
{
    if (epi->summary_flag)
	printf ("%s%-22s\t%s\t%6.4f\n",
		epi->zscore_flag ? "Z": "",
		eval->values[tm->eval_index].name,
		eval->qid,
		eval->values[tm->eval_index].value);
    if (tm->meas_params->printable_params) {
	Free (eval->values[tm->eval_index].name);
	Free (tm->meas_params->printable_params);
	Free (tm->meas_params->param_values);
    }
    return (1);
}
