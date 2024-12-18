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
te_calc_num_rel (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
static int 
te_calc_avg_num_rel (const EPI *epi, const TREC_MEAS *tm,
		     const ALL_REL_INFO *all_rel_info, TREC_EVAL *accum_eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_num_rel =
    {"num_rel",
     "    Number of relevant documents for topic. \n\
    May be affected by Judged_docs_only and Max_retrieved_per_topic command\n\
    line parameters (as are most measures).\n\
    Summary figure is sum of individual topics, not average.\n",
     te_init_meas_s_long,
     te_calc_num_rel,
     te_acc_meas_s,
     te_calc_avg_num_rel,
     te_print_single_meas_s_long,
     te_print_final_meas_s_long,
     NULL, -1};

static int 
te_calc_num_rel (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    RES_RELS res_rels;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    eval->values[tm->eval_index].value = (double) res_rels.num_rel;
    return (1);
}

/* Need custom calc_avg because of -c flag (epi->average_comple_flag).
    Have to go back to original qrels info to get num_qrels in all topics (not
    just those with retrieved docs) */
static int 
te_calc_avg_num_rel (const EPI *epi, const TREC_MEAS *tm,
		                  const ALL_REL_INFO *all_rel_info, TREC_EVAL *accum_eval)
{
    long i,j,k;
    long num_rel;

    if (! epi->average_complete_flag)
	return (1);

    num_rel = 0;
    for (i = 0; i < all_rel_info->num_q_rels; i++) {
	if (0 == strcmp ("qrels", all_rel_info->rel_info[i].rel_format)) {
	    TEXT_QRELS_INFO *trec_qrels;
	    trec_qrels =  (TEXT_QRELS_INFO *)  all_rel_info->rel_info[i].q_rel_info;
	    for (j = 0; j < trec_qrels->num_text_qrels; j++) {
		if (trec_qrels->text_qrels[j].rel > 0)
		    num_rel++;
	    }
	}
	else if (0 == strcmp ("qrels_jg", all_rel_info->rel_info[i].rel_format)) {
	    TEXT_QRELS_JG_INFO *trec_qrels;
	    trec_qrels =  (TEXT_QRELS_JG_INFO *)  all_rel_info->rel_info[i].q_rel_info;
	    for (j = 0; j < trec_qrels->num_text_qrels_jg; j++) {
		for (k = 0; k < trec_qrels->text_qrels_jg[j].num_text_qrels; k++) {
		    if (trec_qrels->text_qrels_jg[j].text_qrels[k].rel > 0)
			num_rel++;
		}
	    }
	}
	else {
	    fprintf (stderr, "trec_eval: m_num_rel: rel_info format not qrels or qrels_jg\n");
	    return (UNDEF);
	}
    }

    accum_eval->values[tm->eval_index].value = num_rel;
    return (1);
}
