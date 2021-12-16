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
te_calc_prefs_avgjg_imp (const EPI *epi, const REL_INFO *rel_info,
			 const RESULTS *results, const TREC_MEAS *tm,
			 TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_prefs_avgjg_imp =
    {"prefs_avgjg_imp",
     "    Simple ratio of preferences fulfilled to preferences possible\n\
    within a judgment group, averaged over jgs.  I.e., rather than considering\n\
    all preferences equal (prefs_simp), consider all judgment groups equal.\n\
    prefs_avgjg = AVERAGE_OVER_JG (fulfilled_jg / possible_jg);\n\
    May be useful in applications where user satisfaction is represented\n\
    by a jg per user, and it is not desirable for many preferences expressed\n\
    by user1 to swamp a few preferences by user2.\n\
    For doc pref A>B, this includes implied preferences (only one of A or B\n\
    retrieved), but ignores pair if neither A nor B retrieved.\n\
    pref_*_imp measures don't have any preferred applications that I know of,\n\
    but some people like them.\n\
    Assumes '-R prefs' or '-R qrels_prefs'\n",
     te_init_meas_s_float,
     te_calc_prefs_avgjg_imp,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_prefs_avgjg_imp (const EPI *epi, const REL_INFO *rel_info,
			 const RESULTS *results, const TREC_MEAS *tm,
			 TREC_EVAL *eval)
{
    RESULTS_PREFS results_prefs;
    long i;
    long ful, poss;
    double sum;

    if (UNDEF == form_prefs_counts (epi, rel_info, results, &results_prefs))
	return (UNDEF);
    
    sum = 0.0;

    for (i = 0; i < results_prefs.num_jgs; i++) {
        ful = results_prefs.jgs[i].num_prefs_fulfilled_ret;
        ful += results_prefs.jgs[i].num_prefs_fulfilled_imp;
        poss = results_prefs.jgs[i].num_prefs_possible_ret;
        poss += results_prefs.jgs[i].num_prefs_possible_imp;

	if (poss)
	    sum += (double) ful / (double) poss;
    }
    /* Simple ratio of preferences fulfilled to preferences possible in
     each jg, averaged over jgs */
    if (sum > 0.0) {
        eval->values[tm->eval_index].value =
	    sum / (double) results_prefs.num_jgs;
    }
    return (1);
}
