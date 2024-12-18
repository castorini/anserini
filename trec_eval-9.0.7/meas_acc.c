/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/
#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "functions.h"

/* Procedures for accumulations of results of several default
   formats of measure values.
   Used in acc_meas procedure description of TREC_MEAS in trec_eval.h:
      typedef struct trec_meas {
      ...
          * Merge info for single query into summary info *
          int (* acc_meas) (const EPI *epi, const struct trec_meas *tm,
                      const TREC_EVAL *q_eval, TREC_EVAL *summ_eval);
      ...
      } TREC_MEAS;
   Measures are defined in measures.c.
*/

/* ---------------- Acc measure generic procedures -------------- */

/* Measure does not require accumulation */
int
te_acc_meas_empty (const EPI *epi, const TREC_MEAS *tm,
		   const TREC_EVAL *q_eval, TREC_EVAL *accum_eval)
{
    return (1);
}

/* Measure is a single float/long that should now be summed */
int
te_acc_meas_s (const EPI *epi, const TREC_MEAS *tm,
	       const TREC_EVAL *q_eval, TREC_EVAL *accum_eval)
{
    accum_eval->values[tm->eval_index].value +=
	q_eval->values[tm->eval_index].value;
    return (1);
}

/* Measure is an array with cutoffs */
int
te_acc_meas_a_cut (const EPI *epi, const TREC_MEAS *tm,
			const TREC_EVAL *q_eval, TREC_EVAL *accum_eval)
{
    long i;
    
    for (i = 0; i < tm->meas_params->num_params; i++) {
	accum_eval->values[tm->eval_index + i].value +=
	    q_eval->values[tm->eval_index + i].value;
    }
    return (1);
}
