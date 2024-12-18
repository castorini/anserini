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
double log2(double x);

static int 
te_calc_G (const EPI *epi, const REL_INFO *rel_info,
	      const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
static PARAMS default_G_gains = { NULL, 0, NULL};

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_G =
    {"G",
     "    Normalized Gain\n\
    Experimental measure 4/10/2008\n\
    G is a gain related measure that combines qualities of MAP and NDCG.\n\
    Contribution of doc doc retrieved at rank i is \n\
    G(doc) == gain (doc) / log2 (2+ideal_gain(i)-results_gain(i))\n\
    where results_gain(i) is sum gain(doc) for all docs before i\n\
    and ideal_gain is the maximum possible results_gain(i)\n\
    G is the sum of G(doc) over all docs, normalized by max ideal_gain.\n\
    Gain values are set to the appropriate relevance level by default.  \n\
    The default gain can be overridden on the command line by having \n\
    comma separated parameters 'rel_level=gain'.\n\
    Eg, 'trec_eval -m G.1=3.5,2=9.0,4=7.0 ...'\n\
    will give gains 3.5, 9.0, 3.0, 7.0 for relevance levels 1,2,3,4\n\
    respectively (level 3 remains at the default).\n\
    Gains are allowed to be 0 or negative, and relevance level 0\n\
    can be given a gain.\n\
    The idea behind G is that the contribution of a doc retrieved at i\n\
    should not be independent of the docs before. If most docs before have\n\
    higher gain, then the retrieval of this doc at i is nearly as good as \n\
    possible, and should be rewarded appropriately\n",
     te_init_meas_s_float_p_pair,
     te_calc_G,
     te_acc_meas_s,
     te_calc_avg_meas_s,
     te_print_single_meas_s_float,
     te_print_final_meas_s_float_p,
     &default_G_gains, -1};

/* Keep track of valid rel_levels and associated gains */
/* Initialized in setup_gains */
typedef struct {
    long rel_level;
    long num_at_level;
    double gain;
} REL_GAIN;

typedef struct {
    REL_GAIN *rel_gains;
    long num_gains;
    long total_num_at_levels;
} GAINS;

static int setup_gains (const TREC_MEAS *tm, const RES_RELS *res_rels,
			GAINS *gains);
static double get_gain (const long rel_level, const GAINS *gains);
static int comp_rel_gain ();

static int 
te_calc_G (const EPI *epi, const REL_INFO *rel_info,
	      const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    RES_RELS res_rels;
    double results_gain, sum_results;
    double ideal_gain, sum_ideal;
    double sum_cost, min_cost;
    double results_g;
    long cur_level, num_at_level;
    long i;
    GAINS gains;
   
    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
	return (UNDEF);

    if (UNDEF == setup_gains (tm, &res_rels, &gains))
	return (UNDEF);

    results_g = 0.0;
    sum_results = 0.0;
    sum_ideal = 0.0;
    sum_cost = 0.0;
    cur_level = gains.num_gains - 1;
    ideal_gain = (cur_level >= 0) ? gains.rel_gains[cur_level].gain : 0.0;
    num_at_level = 0;
    min_cost = 1.0;

    for (i = 0; i < res_rels.num_ret && ideal_gain > 0.0; i++) {
	/* Calculate change in actual results */
	results_gain = get_gain (res_rels.results_rel_list[i], &gains);
	sum_results += results_gain;
	/* Calculate change in ideal results */
	num_at_level++;
	while (cur_level >= 0 &&
	       num_at_level > gains.rel_gains[cur_level].num_at_level) {
	    num_at_level = 1;
	    cur_level--;
	    ideal_gain = (cur_level >= 0) ? gains.rel_gains[cur_level].gain:0.0;
	}
	if (ideal_gain > 0.0)
	    sum_ideal += ideal_gain;
	if (ideal_gain >= min_cost)
	    sum_cost += ideal_gain;
	else
	    sum_cost += min_cost;

	/* Calculate G */
	if (results_gain != 0)
	    results_g += results_gain /
		log2((double) (2 + sum_cost - sum_results));

	if (epi->debug_level > 0) 
	    printf("G: %ld %ld %3.1f %6.4f %3.1f %6.4f %6.4f %6.4f\n",
		   i, cur_level, results_gain, sum_results,
		   ideal_gain, sum_ideal, sum_cost, results_g);
    }
    while (i < res_rels.num_ret) {
	/* Calculate change in results gain */
	results_gain = get_gain (res_rels.results_rel_list[i], &gains);
	sum_results += results_gain;
	sum_cost += min_cost;
	if (results_gain != 0)
	    results_g += results_gain /
		log2((double) (2 + sum_cost - sum_results));
	if (epi->debug_level > 0) 
	    printf("G: %ld %ld %3.1f %6.4f %3.1f %6.4f %6.4f\n",
		   i, cur_level, results_gain, sum_results, 0.0,
		   sum_ideal, results_g);
	i++;
    }
    while (ideal_gain > 0.0) {
	/* Calculate change in ideal results gain*/
	num_at_level++;
	while (cur_level >= 0 &&
	       num_at_level > gains.rel_gains[cur_level].num_at_level) {
	    num_at_level = 1;
	    cur_level--;
	    ideal_gain = (cur_level >= 0) ? gains.rel_gains[cur_level].gain:0.0;
	}
	if (ideal_gain > 0.0)
	    sum_ideal += ideal_gain;
	if (epi->debug_level > 0) 
	    printf("G: %ld %ld %3.1f %6.4f %3.1f %6.4f\n",
		   i, cur_level, 0.0, sum_results,
		   ideal_gain, sum_ideal);
	i++;
    }

    /* Compare sum to ideal G */
    if (sum_ideal > 0.0) {
        eval->values[tm->eval_index].value = results_g / sum_ideal;
    }

    Free (gains.rel_gains);
    return (1);
}

static int
setup_gains (const TREC_MEAS *tm, const RES_RELS *res_rels, GAINS *gains)
{
    FLOAT_PARAM_PAIR *pairs = NULL;
    long num_pairs = 0;
    long i,j;
    long num_gains;

    if (tm->meas_params) {
	pairs = (FLOAT_PARAM_PAIR *) tm->meas_params->param_values;
	num_pairs = tm->meas_params->num_params;
    }

    if (NULL == (gains->rel_gains = Malloc(res_rels->num_rel_levels + num_pairs,
					   REL_GAIN)))
	return (UNDEF);
    num_gains = 0;
    for (i = 0; i < num_pairs; i++) {
	gains->rel_gains[num_gains].rel_level = atol (pairs[i].name);
	gains->rel_gains[num_gains].gain = (double) pairs[i].value;
	gains->rel_gains[num_gains].num_at_level = 0;
	num_gains++;
    }

    for (i = 0; i < res_rels->num_rel_levels; i++) {
	for (j = 0; j < num_gains && gains->rel_gains[j].rel_level != i; j++)
	    ;
	if (j < num_gains)
	    /* Was included in list of parameters. Update occurrence info */
	    gains->rel_gains[j].num_at_level = res_rels->rel_levels[i];
	else {
	    /* Not included in list of parameters. New gain level */
	    gains->rel_gains[num_gains].rel_level = i;
	    gains->rel_gains[num_gains].gain = (double) i;
	    gains->rel_gains[num_gains].num_at_level = res_rels->rel_levels[i];
	    num_gains++;
	}
    }

    /* Sort gains by increasing gain value */
    qsort ((char *) gains->rel_gains,
           (int) num_gains,
           sizeof (REL_GAIN),
           comp_rel_gain);

    gains->total_num_at_levels = 0;
    for (i = 0; i < num_gains; i++)
	gains->total_num_at_levels += gains->rel_gains[i].num_at_level;	

    gains->num_gains = num_gains;
    return (1);
}

static int comp_rel_gain (REL_GAIN *ptr1, REL_GAIN *ptr2)
{
    return (ptr1->gain - ptr2->gain);
}

static double
get_gain (const long rel_level, const GAINS *gains)
{
    long i;
    for (i = 0; i < gains->num_gains; i++)
	if (rel_level == gains->rel_gains[i].rel_level)
	    return (gains->rel_gains[i].gain);
    return (0.0);   /* Print Error ?? */
}

