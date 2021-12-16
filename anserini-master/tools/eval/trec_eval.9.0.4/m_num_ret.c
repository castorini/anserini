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
te_calc_num_ret (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_num_ret =
    {"num_ret",
     "    Number of documents retrieved for topic. \n\
    May be affected by Judged_docs_only and Max_retrieved_per_topic command\n\
    line parameters (as are most measures).\n\
    Summary figure is sum of individual topics, not average.\n",
     te_init_meas_s_long,
     te_calc_num_ret,
     te_acc_meas_s,
     te_calc_avg_meas_empty,
     te_print_single_meas_s_long,
     te_print_final_meas_s_long,
     NULL, -1};

static int 
te_calc_num_ret (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    RES_RELS res_rels;

    /* Can't just use results, since epi->only_judged_docs may be set */
    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    eval->values[tm->eval_index].value = (double) res_rels.num_ret;
    return (1);
}
