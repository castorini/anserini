/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/

#include "common.h"
#include "sysfunc.h"
#include "functions.h"
#include "trec_eval.h"

/* Actual measures.  Definition of TREC_MEAS below is from "trec_eval.h".
   Function prototypes for each function are defined in "functions.h".
   Code implementing each measure is normally found in "m_<meas_name>.c".
   Code implementing standard init, acc, average, print routines are found in
   "meas_{init,acc,calc_avg,print_single,print_final}.c".
   Measures are calculated and printed out in the order they occur here;
   the first measures tend to be the more important. */

/* Measure definition:
    typedef struct trec_meas {
        * Nmae of measure (or root name of set of measures) *
        char *name;
        * Full explanation of measure, printed upon help request *
        char *explanation;
        * Store parameters for measure in meas_params. Reserve space in
           TREC_EVAL.values for results of measure. Store individual measure
           names (possibly altered by parameters) in TREC_EVAL.values and
           initialize value to 0.0.
           Set tm->eval_index to start of reserved space *
        int (* init_meas) (EPI *epi, struct trec_meas *tm, TREC_EVAL *eval);
        * Calculate actual measure for single query *
        int (* calc_meas) (const EPI *epi, const REL_INFO *rel,
    		       const RESULTS *results,  const struct trec_meas *tm,
    		       TREC_EVAL *eval);
        * Merge info for single query into summary info *
        int (* acc_meas) (const EPI *epi, const struct trec_meas *tm,
    		      const TREC_EVAL *q_eval, TREC_EVAL *summ_eval);
        * Calculate final averages (if needed)  from summary info *
        int (* calc_avg) (const EPI *epi, const struct trec_meas *tm,
    		      const TREC_EVAL *eval);
        * Print single query value *
        int (* print_single_meas) (const EPI *epi, const struct trec_meas *tm,
       		       const TREC_EVAL *eval);
        * Print final summary value, and cleanup measure malloc's *
        int (* print_final_and_cleanup_meas) (const EPI *epi,
					  const struct trec_meas *tm,
					  TREC_EVAL *eval);    
        * Measure dependent parameters, defaults given here can normally be
           overridden from command line by init_meas procedure *
        void *meas_params;    
         * Index within TREC_EVAL.values for values for measure.
    	-1 indicates measure not to be calculated (default).
    	-2 indicates measure to be calculated, but has not yet been initialized.
            Set in init_meas *
        long eval_index;
    } TREC_MEAS;
*/

extern TREC_MEAS te_meas_runid;
extern TREC_MEAS te_meas_num_q;
extern TREC_MEAS te_meas_num_ret;
extern TREC_MEAS te_meas_num_rel;
extern TREC_MEAS te_meas_num_rel_ret;
extern TREC_MEAS te_meas_map;
extern TREC_MEAS te_meas_gm_map;
extern TREC_MEAS te_meas_Rprec;
extern TREC_MEAS te_meas_bpref;
extern TREC_MEAS te_meas_recip_rank;
extern TREC_MEAS te_meas_iprec_at_recall;
extern TREC_MEAS te_meas_P;
extern TREC_MEAS te_meas_relstring;
extern TREC_MEAS te_meas_recall;
extern TREC_MEAS te_meas_infAP;
extern TREC_MEAS te_meas_gm_bpref;
extern TREC_MEAS te_meas_Rprec_mult;
extern TREC_MEAS te_meas_utility;
extern TREC_MEAS te_meas_11pt_avg;
extern TREC_MEAS te_meas_binG;
extern TREC_MEAS te_meas_G;
extern TREC_MEAS te_meas_ndcg;
extern TREC_MEAS te_meas_ndcg_rel;
extern TREC_MEAS te_meas_Rndcg;
extern TREC_MEAS te_meas_ndcg_cut;
extern TREC_MEAS te_meas_map_cut;
extern TREC_MEAS te_meas_relative_P;
extern TREC_MEAS te_meas_success;
extern TREC_MEAS te_meas_set_P;
extern TREC_MEAS te_meas_set_relative_P;
extern TREC_MEAS te_meas_set_recall;
extern TREC_MEAS te_meas_set_map;
extern TREC_MEAS te_meas_set_F;
extern TREC_MEAS te_meas_num_nonrel_judged_ret;
extern TREC_MEAS te_meas_prefs_num_prefs_poss;
extern TREC_MEAS te_meas_prefs_num_prefs_ful;
extern TREC_MEAS te_meas_prefs_num_prefs_ful_ret;
extern TREC_MEAS te_meas_prefs_simp;
extern TREC_MEAS te_meas_prefs_pair;
extern TREC_MEAS te_meas_prefs_avgjg;
extern TREC_MEAS te_meas_prefs_avgjg_Rnonrel;
extern TREC_MEAS te_meas_prefs_simp_ret;
extern TREC_MEAS te_meas_prefs_pair_ret;
extern TREC_MEAS te_meas_prefs_avgjg_ret;
extern TREC_MEAS te_meas_prefs_avgjg_Rnonrel_ret;
extern TREC_MEAS te_meas_prefs_simp_imp;
extern TREC_MEAS te_meas_prefs_pair_imp;
extern TREC_MEAS te_meas_prefs_avgjg_imp;
extern TREC_MEAS te_meas_map_avgjg;
extern TREC_MEAS te_meas_P_avgjg;
extern TREC_MEAS te_meas_Rprec_mult_avgjg;
extern TREC_MEAS te_meas_yaap;

TREC_MEAS *te_trec_measures[] = {
    &te_meas_runid,
    &te_meas_num_q,
    &te_meas_num_ret,
    &te_meas_num_rel,
    &te_meas_num_rel_ret,
    &te_meas_map,
    &te_meas_gm_map,
    &te_meas_Rprec,
    &te_meas_bpref,
    &te_meas_recip_rank,
    &te_meas_iprec_at_recall,
    &te_meas_P,
    &te_meas_relstring,
    &te_meas_recall,
    &te_meas_infAP,
    &te_meas_gm_bpref,
    &te_meas_Rprec_mult,
    &te_meas_utility,
    &te_meas_11pt_avg,
    &te_meas_binG,
    &te_meas_G,
    &te_meas_ndcg,
    &te_meas_ndcg_rel,
    &te_meas_Rndcg,
    &te_meas_ndcg_cut,
    &te_meas_map_cut,
    &te_meas_relative_P,
    &te_meas_success,
    &te_meas_set_P,
    &te_meas_set_relative_P,
    &te_meas_set_recall,
    &te_meas_set_map,
    &te_meas_set_F,
    &te_meas_num_nonrel_judged_ret,
    &te_meas_prefs_num_prefs_poss,
    &te_meas_prefs_num_prefs_ful,
    &te_meas_prefs_num_prefs_ful_ret,
    &te_meas_prefs_simp,
    &te_meas_prefs_pair,
    &te_meas_prefs_avgjg,
    &te_meas_prefs_avgjg_Rnonrel,
    &te_meas_prefs_simp_ret,
    &te_meas_prefs_pair_ret,
    &te_meas_prefs_avgjg_ret,
    &te_meas_prefs_avgjg_Rnonrel_ret,
    &te_meas_prefs_simp_imp,
    &te_meas_prefs_pair_imp,
    &te_meas_prefs_avgjg_imp,
    &te_meas_map_avgjg,
    &te_meas_P_avgjg,
    &te_meas_Rprec_mult_avgjg,
    &te_meas_yaap,
};
int te_num_trec_measures = sizeof (te_trec_measures) / sizeof (te_trec_measures[0]);

static char *off_names[] =  {
    "runid", "num_q", "num_ret", "num_rel", "num_rel_ret", "map", "gm_map", 
    "Rprec", "bpref", "recip_rank", "iprec_at_recall", "P", NULL};
static char *trec_names[] =  {
    "runid", "num_q", "num_ret", "num_rel", "num_rel_ret", "map", "gm_map",
    "Rprec", "bpref", "recip_rank", "iprec_at_recall", "P", "relstring",
    "recall", "infAP","gm_bpref",
    "utility", "11pt_avg", "ndcg", "relative_P", "Rprec_mult", "success",
    "map_cut", "ndcg_cut", "ndcg_rel", "Rndcg", "binG", "G",
    "set_P", "set_recall", "set_relative_P", "set_map", "set_F",
    "num_nonrel_judged_ret",
    NULL};
static char *set_names[] =  {
    "runid", "num_q", "num_ret", "num_rel", "num_rel_ret", "utility", "set_P",
    "set_recall", "set_relative_P", "set_map", "set_F",
    NULL};
static char *prefs_names[] =  {
    "runid", "num_q","prefs_num_prefs_poss", "prefs_num_prefs_ful",
    "prefs_num_prefs_ful_ret",
    "prefs_simp", "prefs_pair", "prefs_avgjg", "prefs_avgjg_Rnonrel",
    "prefs_simp_ret", "prefs_pair_ret", "prefs_avgjg_ret",
    "prefs_avgjg_Rnonrel_ret",
    "prefs_simp_imp", "prefs_pair_imp", "prefs_avgjg_imp",
    NULL};
static char *prefs_off_name[] =  {
    "runid", "num_q",
    "prefs_num_prefs_poss", "prefs_num_prefs_ful", "prefs_num_prefs_ful_ret",
    "prefs_simp", "prefs_pair", "prefs_avgjg",
    NULL};
static char *qrels_jg_names[] = {
    "runid", "num_q",
    "map_avgjg", "P_avgjg", "Rprec_mult_avgjg",
    NULL};

TREC_MEASURE_NICKNAMES te_trec_measure_nicknames[] = {
    {"official", off_names},
    {"set", set_names},
    {"all_trec", trec_names},
    {"all_prefs", prefs_names},
    {"prefs", prefs_off_name},
    {"qrels_jg", qrels_jg_names},
};
int te_num_trec_measure_nicknames =
    sizeof (te_trec_measure_nicknames) / sizeof (te_trec_measure_nicknames[0]);
