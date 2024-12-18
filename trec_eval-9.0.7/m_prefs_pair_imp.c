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
te_calc_prefs_pair_imp (const EPI *epi, const REL_INFO *rel_info,
			const RESULTS *results, const TREC_MEAS *tm,
			TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_prefs_pair_imp =
    {"prefs_pair_imp",
     "   Average over doc pairs of preference ratio for that pair.\n\
    If a doc pair satisfies 3 preferences but fails 2 preferences (preferences\n\
    from 5 different users),  then the score for doc pair is 3/5.\n\
    Same as prefs_simp if there are no doc_pairs in multiple judgment groups.\n\
    For doc pref A>B, this includes implied preferences (only one of A or B\n\
    retrieved), but ignores pair if neither A nor B retrieved.\n\
    pref_*_imp measures don't have any preferred applications that I know of,\n\
    but some people like them.\n\
    Assumes '-R prefs' or '-R qrels_prefs'\n",
     te_init_meas_s_float,
     te_calc_prefs_pair_imp,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float,
     NULL, -1};

static int 
te_calc_prefs_pair_imp (const EPI *epi, const REL_INFO *rel_info,
			const RESULTS *results, const TREC_MEAS *tm,
			TREC_EVAL *eval)
{
    RESULTS_PREFS rp;
    long i, j;
    double sum = 0;
    long num_pairs = 0;

    if (UNDEF == form_prefs_counts (epi, rel_info, results, &rp))
	return (UNDEF);

    for (i = 0; i < rp.num_judged_ret; i++) {
	for (j = i+1; j < rp.num_judged_ret; j++) {
	    if (rp.pref_counts.array[i][j] ||
		rp.pref_counts.array[j][i]) {
		num_pairs++;
		sum += (double) rp.pref_counts.array[i][j] /
		    (double) (rp.pref_counts.array[i][j] +
			     rp.pref_counts.array[j][i]);
	    }
	}
    }

    for (i = 0; i < rp.num_judged_ret; i++) {
	for (j = rp.num_judged_ret; j < rp.num_judged; j++) {
	    if (rp.pref_counts.array[i][j] ||
		rp.pref_counts.array[j][i]) {
		num_pairs++;
		sum += (double) rp.pref_counts.array[i][j] /
		    (double) (rp.pref_counts.array[i][j] +
			     rp.pref_counts.array[j][i]);
	    }
	}
    }

    if (num_pairs) {
        eval->values[tm->eval_index].value =
	    sum / (double) num_pairs;
    }
    return (1);
}
