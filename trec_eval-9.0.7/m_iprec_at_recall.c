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
te_calc_iprec_at_recall (const EPI *epi, const REL_INFO *rel_info,
			 const RESULTS *results, const TREC_MEAS *tm,
			 TREC_EVAL *eval);
static double float_cutoff_array[] = {
    0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
static PARAMS default_iprec_at_recall_cutoffs = {
    NULL, sizeof (float_cutoff_array) / sizeof (float_cutoff_array[0]),
    &float_cutoff_array[0]};

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_iprec_at_recall =
    {"iprec_at_recall",
     "    Interpolated Precision at recall cutoffs.\n\
    This is the data shown in the standard Recall-Precision graph.\n\
    The standard cutoffs and interpolation are needed to average data over\n\
    multiple topics; otherwise, how is a topic with 5 relevant docs averaged\n\
    with a topic with 3 relevant docs for graphing purposes?  The Precision \n\
    interpolation used here is\n\
      Int_Prec (rankX) == MAX (Prec (rankY)) for all Y >= X.\n\
    Default usage: -m iprec_at_recall.0,.1,.2,.3,.4,.5,.6,.7,.8,.9,1 ...\n",
     te_init_meas_a_float_cut_float,
     te_calc_iprec_at_recall,
     te_acc_meas_a_cut, 
     te_calc_avg_meas_a_cut,
     te_print_single_meas_a_cut,
     te_print_final_meas_a_cut,
     (void *) &default_iprec_at_recall_cutoffs, -1};

static int 
te_calc_iprec_at_recall (const EPI *epi, const REL_INFO *rel_info,
			 const RESULTS *results, const TREC_MEAS *tm,
			 TREC_EVAL *eval)
{
    double *cutoff_percents = (double *) tm->meas_params->param_values;
    long *cutoffs;    /* cutoffs expressed in num rel docs instead of percents*/
    long current_cut; /* current index into cutoffs */
    RES_RELS rr;
    long rel_so_far;
    long i;
    double precis, int_precis;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &rr))
	return (UNDEF);

    /* translate percentage of rels as given in the measure params, to
       an actual cutoff number of docs.  Note addition of 0.9 
       means the default 11 percentages should have same cutoffs as
       historical MAP implementations (eg, old trec_eval) */
    if (NULL == (cutoffs = Malloc (tm->meas_params->num_params, long)))
	return (UNDEF);
    for (i = 0; i < tm->meas_params->num_params; i++)
	cutoffs[i] = (long) (cutoff_percents[i] * rr.num_rel+0.9);

    current_cut = tm->meas_params->num_params - 1;
    while (current_cut >= 0 && cutoffs[current_cut] > rr.num_rel_ret)
	current_cut--;

    /* Loop over all retrieved docs in reverse order.  Needs to be
       reverse order since are calcualting interpolated precision.
       Int_Prec (X) defined to be MAX (Prec (Y)) for all Y >= X. */
    precis = (double) rr.num_rel_ret / (double) rr.num_ret;
    int_precis = precis;
    rel_so_far = rr.num_rel_ret;
    for (i = rr.num_ret; i > 0 && rel_so_far > 0; i--) {
	precis = (double) rel_so_far / (double) i;
	if (int_precis < precis)
	    int_precis = precis;
	if (rr.results_rel_list[i-1] >= epi->relevance_level) {
            while (current_cut >= 0 && rel_so_far == cutoffs[current_cut]) {
		eval->values[tm->eval_index+current_cut].value = int_precis;
                current_cut--;
            }
            rel_so_far--;
	}
    }

    while (current_cut >= 0) {
	eval->values[tm->eval_index+current_cut].value = int_precis;
	current_cut--;
    }

    (void) Free (cutoffs);

    return (1);
}
