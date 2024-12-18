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
te_calc_runid (const EPI *epi, const REL_INFO *rel_info,
	       const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval);
static int
te_print_runid (const EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval);

/* See trec_eval.h for definition of TREC_MEAS */
TREC_MEAS te_meas_runid = 
    {"runid",
     "    Runid given by results input file.\n",
     te_init_meas_empty,
     te_calc_runid,
     te_acc_meas_empty,
     te_calc_avg_meas_empty,
     te_print_single_meas_empty,
     te_print_runid,
     NULL, -1};

static char *runid;
static int 
te_calc_runid (const EPI *epi, const REL_INFO *rel_info,
	       const RESULTS *results, const TREC_MEAS *tm, TREC_EVAL *eval)
{
    runid = results->run_id;
    return (1);
}

static int
te_print_runid (const EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    if (epi->summary_flag)
	printf ("%-22s\t%s\t%s\n", "runid", eval->qid, runid);
    return (1);
}
