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
te_calc_rel_P (const EPI *epi, const REL_INFO *rel_info, const RESULTS *results,
	       const TREC_MEAS *tm, TREC_EVAL *eval);
static long long_cutoff_array[] = {5, 10, 15, 20, 30, 100, 200, 500, 1000};
static PARAMS default_relative_P_cutoffs = {
    NULL, sizeof (long_cutoff_array) / sizeof (long_cutoff_array[0]),
    &long_cutoff_array[0]};

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_relative_P =
    {"relative_P",
     "    Relative Precision at cutoffs\n\
    Precision at cutoff relative to the maximum possible precision at that\n\
    cutoff.  Equivalent to Precision up until R, and then recall after R\n\
    Cutoffs must be positive without duplicates\n\
    Default params: -m relative_P.5,10,15,20,30,100,200,500,1000\n",
     te_init_meas_a_float_cut_long,
     te_calc_rel_P,
     te_acc_meas_a_cut,
     te_calc_avg_meas_a_cut,
     te_print_single_meas_a_cut,
     te_print_final_meas_a_cut,
     (void *) &default_relative_P_cutoffs, -1};

static int 
te_calc_rel_P (const EPI *epi, const REL_INFO *rel_info, const RESULTS *results,
	       const TREC_MEAS *tm, TREC_EVAL *eval)
{
    long *cutoffs = (long *) tm->meas_params->param_values;
    long cutoff_index = 0;
    long i;
    RES_RELS rr;
    long rel_so_far = 0;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &rr))
	return (UNDEF);

    if (rr.num_rel == 0)
	return (0);

    for (i = 0; i < rr.num_ret; i++) {
	if (i == cutoffs[cutoff_index]) {
	    /* Calculate previous cutoff threshold.
	       Note cutoffs guaranteed to be positive by init_meas */
	    eval->values[tm->eval_index + cutoff_index].value =
		(double) rel_so_far / (double)((i > rr.num_rel) ? rr.num_rel:i);
	    if (++cutoff_index == tm->meas_params->num_params)
		break;
	}
	if (rr.results_rel_list[i] >= epi->relevance_level)
	    rel_so_far++;
    }
    /* calculate values for those cutoffs not achieved */
    while (cutoff_index < tm->meas_params->num_params) {
	i = cutoffs[cutoff_index];
	eval->values[tm->eval_index + cutoff_index].value =
	    (double) rel_so_far / (double) ((i > rr.num_rel) ? rr.num_rel : i);
	cutoff_index++;
    }
    return (1);
}
