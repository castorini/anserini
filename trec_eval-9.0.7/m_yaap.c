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

static int
te_calc_yaap (const EPI *epi, const REL_INFO *rel_info,
	     const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_yaap =  {"yaap",
     "    Yet Another Average Precision\n\
    Adaptation of MAP proposed by Stephen Robertson to get a value\n\
    that is more globally averagable than MAP.  Should be monotonic with\n\
    MAP on a single topic, but handles extreme values better.\n\
    log ((1 + sum_probrel)  /  (1 + num_rel - sum_probrel))\n\
    where sum_probrel = sum over all rels of (numrel_before_it / current rank)\n\
    Cite: 'On Smoothing Average Precision', Stephen Robertson.\n\
    ECIR 2012, LNCS 7224, pp.158-169.  2012.\n\
    Edited by R.Baeza-Yates et al. Springer-Verlag Berlin\n",
     te_init_meas_s_float,
     te_calc_yaap,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_yaap (const EPI *epi, const REL_INFO *rel_info, const RESULTS *results,
	     const TREC_MEAS *tm, TREC_EVAL *eval)
{
    RES_RELS res_rels;
    double sum;
    long rel_so_far;
    long i;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    rel_so_far = 0;
    sum = 0.0;
    for (i = 0; i < res_rels.num_ret; i++) {
	if (res_rels.results_rel_list[i] >= epi->relevance_level) {
	    rel_so_far++;
	    sum += (double) rel_so_far / (double) (i + 1);
	}
    }
    eval->values[tm->eval_index].value = 
	log ( (1.0 + sum) /
	      (1.0  + (double) res_rels.num_rel - sum));
    return (1);
}
