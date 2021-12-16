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
te_calc_Rprec (const EPI *epi, const REL_INFO *rel_info, const RESULTS *results,
	       const TREC_MEAS *tm, TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_Rprec =
    {"Rprec",
    "    Precision after R documents have been retrieved.\n\
    R is the total number of relevant docs for the topic.  \n\
    This is a good single point measure for an entire retrieval\n\
    ranking that averages well since each topic is being averaged\n\
    at an equivalent point in its result ranking.\n\
    Note that this is the point that Precision = Recall.\n\
    History: Originally developed for IR rankings by Chris Buckley\n\
    after TREC 1, but analogs were used in other disciplines previously.\n\
    (the point where P = R is an important one!)\n\
    Cite: 'Retrieval System Evaluation', Chris Buckley and Ellen Voorhees.\n\
    Chapter 3 in TREC: Experiment and Evaluation in Information Retrieval\n\
    edited by Ellen Voorhees and Donna Harman.  MIT Press 2005\n",
     te_init_meas_s_float,
     te_calc_Rprec,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_Rprec (const EPI *epi, const REL_INFO *rel_info, const RESULTS *results,
	       const TREC_MEAS *tm, TREC_EVAL *eval)
{
    RES_RELS res_rels;
    long num_to_look_at;
    long rel_so_far;
    long i;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    rel_so_far = 0;
    num_to_look_at = MIN (res_rels.num_ret, res_rels.num_rel);
    if (0 == num_to_look_at)
	return (0);

    for (i = 0; i < num_to_look_at; i++) {
	if (res_rels.results_rel_list[i] >= epi->relevance_level)
	    rel_so_far++;
    }
    eval->values[tm->eval_index].value =
	(double) rel_so_far / (double) res_rels.num_rel;
    return (1);
}
