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
te_calc_set_relative_P (const EPI *epi, const REL_INFO *rel_info,
			const RESULTS *results, const TREC_MEAS *tm,
			TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_set_relative_P =
   {"set_relative_P",
     "    Relative Set Precision:  P / (Max possible P for this size set) \n\
    Relative precision over all docs retrieved for a topic.\n\
    Was known as exact_relative_prec in earlier versions of trec_eval\n\
    Note:   trec_eval -m relative_P.50 ...\n\
    is different from \n\
            trec_eval -M 50 -m set_relative_P ...\n",
     te_init_meas_s_float,
     te_calc_set_relative_P,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
    NULL, -1};

static int 
te_calc_set_relative_P (const EPI *epi, const REL_INFO *rel_info,
			const RESULTS *results, const TREC_MEAS *tm,
			TREC_EVAL *eval)
{
    RES_RELS res_rels;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    if (res_rels.num_ret && res_rels.num_rel) 
	eval->values[tm->eval_index].value =
	    (double) res_rels.num_rel_ret /
	    (double) MIN (res_rels.num_ret, res_rels.num_rel);
    
    return (1);
}
