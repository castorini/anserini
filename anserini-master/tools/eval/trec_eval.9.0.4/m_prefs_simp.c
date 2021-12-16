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
te_calc_prefs_simp (const EPI *epi, const REL_INFO *rel_info,
		    const RESULTS *results, const TREC_MEAS *tm,
		    TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_prefs_simp =
    {"prefs_simp",
    "    Simple ratio of preferences fulfilled to preferences possible.\n\
    If a doc pair satisfies two preferences, both are counted.\n\
    If preferences are conflicted for a doc pair, all are counted\n\
    (and thus max possible score may be less than 1.0 for topic).\n\
    For doc pref A>B, this includes implied preferences (only one of A or B\n\
    retrieved), and counts as failure if neither A nor B retrieved.\n\
    Assumes '-R prefs' or '-R qrels_prefs'\n",
     te_init_meas_s_float,
     te_calc_prefs_simp,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_prefs_simp (const EPI *epi, const REL_INFO *rel_info,
		    const RESULTS *results, const TREC_MEAS *tm,
		    TREC_EVAL *eval)
{
    RESULTS_PREFS results_prefs;
    long i;
    long poss, ful;

    if (UNDEF == form_prefs_counts (epi, rel_info, results, &results_prefs))
	return (UNDEF);
    
    ful = poss = 0;
    for (i = 0; i < results_prefs.num_jgs; i++) {
	ful += results_prefs.jgs[i].num_prefs_fulfilled_ret;
	ful += results_prefs.jgs[i].num_prefs_fulfilled_imp;
	poss += results_prefs.jgs[i].num_prefs_possible_ret;
	poss += results_prefs.jgs[i].num_prefs_possible_imp;
	poss += results_prefs.jgs[i].num_prefs_possible_notoccur;
    }
    /* Simple ratio of preferences fulfilled to preferences possible */
    if (poss) {
        eval->values[tm->eval_index].value =
	    (double) ful / (double) poss;
    }
    return (1);
}
