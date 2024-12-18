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
te_calc_num_q (const EPI *epi, const REL_INFO *rel_info,
	       const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
static int 
te_calc_avg_num_q (const EPI *epi, const TREC_MEAS *tm,
		   const ALL_REL_INFO *all_rel_info, TREC_EVAL *accum_eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_num_q = 
    {"num_q",
     "    Number of topics results averaged over.  May be different from\n\
    number of topics in the results file if -c was used on the command line \n\
    in which case number of topics in the rel_info file is used.\n",
     te_init_meas_s_long,
     te_calc_num_q,
     te_acc_meas_s,
     te_calc_avg_num_q,
     te_print_single_meas_empty,
     te_print_final_meas_s_long,
     NULL, -1};

static int 
te_calc_num_q (const EPI *epi, const REL_INFO *rel_info,
		   const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    eval->values[tm->eval_index].value = 1;
    return (1);
}

/* Need custom calc_avg because after -c flag, calculated value may not
   agree with eval->num_queries. (Actually other ways to better do it now) */
static int 
te_calc_avg_num_q (const EPI *epi, const TREC_MEAS *tm,
		                  const ALL_REL_INFO *all_rel_info, TREC_EVAL *accum_eval)
{
    long num_queries = accum_eval->num_queries;
    if (epi->average_complete_flag)
	num_queries = all_rel_info->num_q_rels;

    accum_eval->values[tm->eval_index].value = num_queries;
    return (1);
}    
