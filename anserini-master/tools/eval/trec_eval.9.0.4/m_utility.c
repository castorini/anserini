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
te_calc_utility (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
static double utility_param_array[] = {1.0, -1.0, 0.0, 0.0};
static PARAMS default_utility_params = {
    NULL, sizeof (utility_param_array) / sizeof (utility_param_array[0]),
    &utility_param_array[0]};

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_utility =
   {"utility",
     "    Set utility measure\n\
    Set evaluation based on contingency table:\n\
                        relevant  nonrelevant\n\
       retrieved            a          b\n\
       nonretrieved         c          d\n\
    where  utility = p1 * a + p2 * b + p3 * c + p4 * d\n\
    and p1-4 are parameters (given on command line in that order).\n\
    Conceptually, each retrieved relevant doc is worth something positive to\n\
    a user, each retrieved nonrelevant doc has a negative worth, each \n\
    relevant doc not retrieved may have a negative worth, and each\n\
    nonrelevant doc not retrieved may have a (small) positive worth.\n\
    The overall measure is simply a weighted sum of these values.\n\
    If p4 is non-zero, then '-N num_docs_in_coll' may also be needed - the\n\
    standard results and rel_info files do not contain that information.\n\
    Default usage: -m utility.1.0,-1.0,0.0,0.0 ...\n\
    Warning: Current version summary evaluation averages over all topics;\n\
    it could be argued that simply summing is more useful (but not backward\n\
    compatible)\n",
     te_init_meas_s_float_p_float,
     te_calc_utility,
     te_acc_meas_s,
     te_calc_avg_meas_s, 
     te_print_single_meas_s_float,
     te_print_final_meas_s_float_p,
    (void *) &default_utility_params, -1};

static int 
te_calc_utility (const EPI *epi, const REL_INFO *rel_info,
		 const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    double *params = (double *) tm->meas_params->param_values;
    RES_RELS rr;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &rr))
	return (UNDEF);

    if (tm->meas_params->num_params != 4) {
	fprintf (stderr,
		 "trec_eval.calc_utility: improper number of coefficients\n");
	return (UNDEF);
    }

    eval->values[tm->eval_index].value =
	params[0] * rr.num_rel_ret +
	params[1] * (rr.num_ret - rr.num_rel_ret) +
	params[2] * (rr.num_rel - rr.num_rel_ret) +
	params[3] * (epi->num_docs_in_coll + rr.num_rel_ret - rr.num_ret -
		     rr.num_rel);
    return (1);
}
