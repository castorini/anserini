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
double log2(double x);

static int
te_calc_binG (const EPI *epi, const REL_INFO *rel_info,
	      const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_binG =  {"binG",
     "    Binary G\n\
    Experimental measure. (4/10/2008)\n\
    G is a gain related measure that combines qualities of MAP and NDCG.\n\
    G(doc) == rel_level_gain (doc) / log2 (2+num_nonrel retrieved before doc)\n\
    G is the average of G(doc) over all docs, normalized by\n\
    sum (rel_level_gain).\n\
    BinG restricts the gain to either 0 or 1 (nonrel or rel), and thus is the\n\
    average over all rel docs of (1 / log2 (2+num_nonrel before doc))\n",
     te_init_meas_s_float,
     te_calc_binG,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_binG (const EPI *epi, const REL_INFO *rel_info, const RESULTS *results,
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
	    sum += (double) 1.0 / (double) log2 ((double) (3 + i - rel_so_far));
	    if (epi->debug_level > 0) 
		printf("binG: %ld %ld %6.4f\n",
		       i, rel_so_far, sum);
	}
    }
    /* Average over the rel docs */
    if (rel_so_far) {
	eval->values[tm->eval_index].value = 
	    sum / (double) res_rels.num_rel;
    }
    return (1);
}
