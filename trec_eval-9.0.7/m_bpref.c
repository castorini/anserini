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
te_calc_bpref (const EPI *epi, const REL_INFO *rel_info, const RESULTS *results,
	       const TREC_MEAS *tm, TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_bpref =
     {"bpref",
     "    Main binary preference measure.\n\
    Fraction of the top R nonrelevant docs that are retrieved after each\n\
    relevant doc. Put another way: when looking at the R relevant docs, and\n\
    the top R nonrelevant docs, if all relevant docs are to be preferred to\n\
    nonrelevant docs, bpref is the fraction of the preferences that the\n\
    ranking preserves.\n\
    Cite: 'Retrieval Evaluation with Incomplete Information', Chris Buckley\n\
    and Ellen Voorhees. In Proceedings of 27th SIGIR, 2004.\n",
     te_init_meas_s_float,
     te_calc_bpref,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
      NULL, -1};

static int 
te_calc_bpref (const EPI *epi, const REL_INFO *rel_info, const RESULTS *results,
	       const TREC_MEAS *tm, TREC_EVAL *eval)
{
    RES_RELS res_rels;
    long j;
    long nonrel_so_far, rel_so_far, pool_unjudged_so_far;
    long num_nonrel = 0;
    double bpref = 0.0;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    for (j = 0; j < epi->relevance_level; j++)
	num_nonrel += res_rels.rel_levels[j];

    /* Calculate judgement based measures (dependent on only
       judged docs; no assumption of non-relevance if not judged) */
    /* Binary Preference measures; here expressed as all docs with a higher 
       value of rel are to be preferred.  Optimize by keeping track of nonrel
       seen so far */
    nonrel_so_far = 0;
    rel_so_far = 0;
    pool_unjudged_so_far = 0;
    for (j = 0; j < res_rels.num_ret; j++) {
	if (res_rels.results_rel_list[j] == RELVALUE_NONPOOL)
	    /* document not in pool. Skip */
	    continue;
	if (res_rels.results_rel_list[j] == RELVALUE_UNJUDGED) {
	    /* document in pool but unjudged. */
	    pool_unjudged_so_far++;
	    continue;
	}

	if (res_rels.results_rel_list[j] >= 0 &&
	    res_rels.results_rel_list[j] < epi->relevance_level)
	    nonrel_so_far++;
	else {
	    /* Judged Rel doc */
	    rel_so_far++;
	    /* Add fraction of correct preferences. */
	    /* Special case nonrel_so_far == 0 to avoid division by 0 */
	    if (nonrel_so_far > 0) {
		bpref += 1.0 - 
		    (((double) MIN (nonrel_so_far, res_rels.num_rel)) /
		     (double) MIN (num_nonrel, res_rels.num_rel));
	    }
	    else
		bpref += 1.0;
	}
    }
    if (res_rels.num_rel)
	bpref /= res_rels.num_rel;

    eval->values[tm->eval_index].value = bpref;
    return (1);
}
