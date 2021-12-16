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
te_calc_gm_map (const EPI *epi, const REL_INFO *rel_info,
		const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_gm_map =
    {"gm_map",
     "    Geometric Mean Average Precision\n\
    This is the same measure as 'map' (see description of 'map') on an\n\
    individual topic, but the geometric mean is calculated when averaging\n\
    over topics.  This rewards methods that are more consistent over topics\n\
    as opposed to methods which do very well for some topics but very poorly\n\
    for others.\n\
    gm_ap is reported only in the summary over all topics, not for individual\n\
    topics.\n",
     te_init_meas_s_float,
     te_calc_gm_map,
     te_acc_meas_s,
     te_calc_avg_meas_s_gm,
     te_print_single_meas_empty,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_gm_map (const EPI *epi, const REL_INFO *rel_info,
		const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
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

    if (rel_so_far) {
	sum = sum / (double) res_rels.num_rel;
    }
    eval->values[tm->eval_index].value =
	(double) log ((double)(MAX (sum, MIN_GEO_MEAN)));
    return (1);
}
