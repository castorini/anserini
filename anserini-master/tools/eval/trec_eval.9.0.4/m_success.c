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
te_calc_success (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
static long success_cutoff_array[] = {1, 5, 10};
static PARAMS default_success_cutoffs = {
    NULL, sizeof (success_cutoff_array) / sizeof (success_cutoff_array[0]),
    &success_cutoff_array[0]};

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_success =
    {"success",
     "    Success at cutoffs\n\
    Success (a relevant doc has been retrieved) measured at various doc level\n\
    cutoffs in the ranking.\n\
    If the cutoff is larger than the number of docs retrieved, then\n\
    it is assumed nonrelevant docs fill in the rest.\n\
    Cutoffs must be positive without duplicates\n\
    Default param: trec_eval -m success.1,5,10\n\
    History: Developed by Stephen Tomlinson.\n",
     te_init_meas_a_float_cut_long,
     te_calc_success,
     te_acc_meas_a_cut,
     te_calc_avg_meas_a_cut,
     te_print_single_meas_a_cut,
     te_print_final_meas_a_cut,
     (void *) &default_success_cutoffs, -1};

static int 
te_calc_success (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    long *cutoffs = (long *) tm->meas_params->param_values;
    long cutoff_index = 0;
    long i;
    RES_RELS res_rels;
    long rel_so_far = 0;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    for (i = 0; i < res_rels.num_ret; i++) {
	if (i == cutoffs[cutoff_index]) {
	    /* Calculate previous cutoff threshold.
	     Note i guaranteed to be positive by init_meas */
	    eval->values[tm->eval_index + cutoff_index].value =
		rel_so_far ? 1.0 : 0.0;
	    if (++cutoff_index == tm->meas_params->num_params)
		break;
	}
	if (res_rels.results_rel_list[i] >= epi->relevance_level)
	    rel_so_far++;
    }
    /* calculate values for those cutoffs not achieved */
    while (cutoff_index < tm->meas_params->num_params) {
	eval->values[tm->eval_index + cutoff_index].value =
	    rel_so_far ? 1.0 : 0.0;
	cutoff_index++;
    }
    return (1);
}
