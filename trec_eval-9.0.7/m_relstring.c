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


static double relstring_len[] = {10.0};
static PARAMS default_relstring_params = {NULL, 1, &relstring_len[0]};

static char *current_string;
static long string_len = 0;

static int te_calc_relstring(const EPI *epi, const REL_INFO *rel_info,
			     const RESULTS *results, const TREC_MEAS *tm,
			     TREC_EVAL *eval);
static int te_print_q_relstring (const EPI *epi, const TREC_MEAS *tm,
				 const TREC_EVAL *eval);
static int te_print_relstring (const EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_relstring = 
    {"relstring",
     "    The relevance values for the first N (default 10) retrieved docs\n\
    are printed as a string, one character per relevance value for a doc.\n\
    If the relevance value is between 0 and 9, it is printed.\n\
    If the value is > 9,  '>' is printed.\n\
    If the document was not in the pool to be judged, '-' is printed.\n\
    if the document was in the pool, but unjudged (eg, infAP),  '.' is printed\n\
    if the document has some other relevance value, '<' is printed.\n\
    Measure is only printed for individual queries.\n\
    Default usage:  -m relstring.10 \n",
     te_init_meas_s_float_p_float,
     te_calc_relstring,
     te_acc_meas_empty,
     te_calc_avg_meas_empty,
     te_print_q_relstring,
     te_print_relstring,
     (void *) &default_relstring_params, -1};

static int 
te_calc_relstring (const EPI *epi, const REL_INFO *rel_info,
		   const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    double *params = (double *) tm->meas_params->param_values;
    RES_RELS res_rels;
    long i, len;

    if (UNDEF == te_form_res_rels (epi, rel_info, results, &res_rels))
        return (UNDEF);

    len = MIN ((long) params[0], res_rels.num_ret);
    if (len < 0) len = 0;

    if (NULL == (current_string =
		 te_chk_and_malloc (current_string, &string_len, len+1, 1)))
	return (UNDEF);

    for (i = 0; i < len; i++) {
	char c;
	long rel = res_rels.results_rel_list[i];
	if (rel > 9) c = '>';
	else if (rel >= 0) c = '0' + rel;
	else if (rel == RELVALUE_NONPOOL) c = '-';
	else if (rel == RELVALUE_UNJUDGED) c = '.';
	else c = '<';
	current_string[i] = c;
    }
    current_string[i] = '\0';

    return (1);
}

static int
te_print_q_relstring (const EPI *epi, const TREC_MEAS *tm,const TREC_EVAL *eval)
{
    printf ("%-22s\t%s\t'%s'\n",
            eval->values[tm->eval_index].name,
            eval->qid,
	    current_string);
    return (1);
}


static int
te_print_relstring (const EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    if (string_len > 0) {
	string_len = 0;
	Free (current_string);
    }
    if (tm->meas_params->printable_params) {
        Free (eval->values[tm->eval_index].name);
        Free (tm->meas_params->printable_params);
        Free (tm->meas_params->param_values);
    }
    return (1);
}
