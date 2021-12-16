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
te_calc_P_avgjg (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
static long long_cutoff_array[] = {5, 10, 15, 20, 30, 100, 200, 500, 1000};
static PARAMS default_P_avgjg_cutoffs = {
    NULL, sizeof (long_cutoff_array) / sizeof (long_cutoff_array[0]),
    &long_cutoff_array[0]};

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_P_avgjg =
    {"P_avgjg",
     "    Precision at cutoffs, averaged over judgment groups (users)\n\
    Precision measured at various doc level cutoffs in the ranking.\n\
    If the cutoff is larger than the number of docs retrieved, then\n\
    it is assumed nonrelevant docs fill in the rest.  Eg, if a method\n\
    retrieves 15 docs of which 4 are relevant, then P20 is 0.2 (4/20).\n\
    If there are multiple relevance judgment sets for this query, Precision\n\
    is averaged over the judgment groups.\n\
    Cutoffs must be positive without duplicates\n\
    Default param: trec_eval -m P.5,10,15,20,30,100,200,500,1000\n",
     te_init_meas_a_float_cut_long,
     te_calc_P_avgjg,
     te_acc_meas_a_cut,
     te_calc_avg_meas_a_cut,
     te_print_single_meas_a_cut,
     te_print_final_meas_a_cut,
     (void *) &default_P_avgjg_cutoffs, -1};

static int 
te_calc_P_avgjg (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    long *cutoffs = (long *) tm->meas_params->param_values;
    long cutoff_index;
    long i,jg;
    RES_RELS_JG rr;
    long rel_so_far;

    if (UNDEF == te_form_res_rels_jg (epi, rel_info, results, &rr))
	return (UNDEF);

   for (jg = 0; jg < rr.num_jgs; jg++) {
       cutoff_index = 0;
       rel_so_far = 0;
       for (i = 0; i < rr.jgs[jg].num_ret; i++) {
	   if (i == cutoffs[cutoff_index]) {
	       /* Calculate previous cutoff threshold.
		  Note all guaranteed to be positive by init_meas */
	       eval->values[tm->eval_index+cutoff_index].value +=
		   (double) rel_so_far / (double) i;
	       if (++cutoff_index == tm->meas_params->num_params)
		   break;
	   }
	   if (rr.jgs[jg].results_rel_list[i] >= epi->relevance_level)
	       rel_so_far++;
       }
       /* calculate values for those cutoffs not achieved */
       while (cutoff_index < tm->meas_params->num_params) {
	   eval->values[tm->eval_index+cutoff_index].value +=
	       (double) rel_so_far/(double) cutoffs[cutoff_index];
	   cutoff_index++;
       }
   }

    if (rr.num_jgs > 1) {
	for (cutoff_index = 0; cutoff_index < tm->meas_params->num_params;
	     cutoff_index++)
	    eval->values[tm->eval_index + cutoff_index].value /= rr.num_jgs;
    }

    return (1);
}
