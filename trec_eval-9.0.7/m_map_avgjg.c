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
te_calc_map_avgjg (const EPI *epi, const REL_INFO *rel_info,
		   const RESULTS *results, const TREC_MEAS *tm,TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_map_avgjg =
    {"map_avgjg",
      "    Mean Average Precision over judgment groups \n\
    Precision measured after each relevant doc is retrieved, then averaged\n\
    for the topic, and then averaged over judgement group (user) and then \n\
    averaged over topics (if more than one).\n\
    Same as the workhorse measure 'map' except if there is more than one\n\
    set of relevance judgments for this query (each set indicated by a\n\
    different judgment group), the score will be averaged over the judgment\n\
    groups.\n",
     te_init_meas_s_float,
     te_calc_map_avgjg,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_map_avgjg (const EPI *epi, const REL_INFO *rel_info,
		   const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    RES_RELS_JG rr;
    double sum;
    long rel_so_far;
    long i, jg;

    if (UNDEF == te_form_res_rels_jg (epi, rel_info, results, &rr))
	return (UNDEF);

    for (jg = 0; jg < rr.num_jgs; jg++) {
	rel_so_far = 0;
	sum = 0.0;
	for (i = 0; i < rr.jgs[jg].num_ret; i++) {
	    if (rr.jgs[jg].results_rel_list[i] >= epi->relevance_level) {
		rel_so_far++;
		sum += (double) rel_so_far / (double) (i + 1);
	    }
	}
	/* Average over the rel docs */
	if (rel_so_far) {
	    eval->values[tm->eval_index].value += 
		sum / (double) rr.jgs[jg].num_rel;
	}
    }

    if (rr.num_jgs > 1) {
	eval->values[tm->eval_index].value /= rr.num_jgs;
    }

    return (1);
}
