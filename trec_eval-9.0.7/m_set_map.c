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
te_calc_set_map (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_set_map =
    {"set_map",
     "    Set map: num_relevant_retrieved**2 / (num_retrieved*num_rel)\n\
    Unranked set map, where the precision due to all relevant retrieved docs\n\
    is the set precision, and the precision due to all relevant not-retrieved\n\
    docs is set to 0.\n\
    Was known as exact_unranked_avg_prec in earlier versions of trec_eval.\n\
    Another way of loooking at this is  Recall * Precision on the set of\n\
    docs retrieved for a topic.\n",
     te_init_meas_s_float,
     te_calc_set_map,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_set_map (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    RES_RELS res_rels;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    if (res_rels.num_ret && res_rels.num_rel)
	eval->values[tm->eval_index].value =
	    ((double) res_rels.num_rel_ret *
	     (double) res_rels.num_rel_ret)      /
	    ((double) res_rels.num_ret *
	     (double) res_rels.num_rel);

    return (1);
}
