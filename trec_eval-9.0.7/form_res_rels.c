/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/
#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "trec_format.h"
#include "functions.h"
/* Takes the top docs and judged docs for a query, and returns a
   rel_rank object giving the ordered relevance values for retrieved
   docs, plus relevance occurrence statatistics.
   Relevance value is
       value in text_qrels if docno is in text_qrels and was judged
           (assumed to be a small non-negative integer)
       RELVALUE_NONPOOL (-1) if docno is not in text_qrels
       RELVALUE_UNJUDGED (-2) if docno is in text_qrels and was not judged.

   This procedure may be called repeatedly for a given topic - returned
   values are cached until the query changes.

   results and rel_info formats must be "trec_results" and "qrels"
   respectively.  

   UNDEF returned if error, 0 if used cache values, 1 if new values.
*/

static int comp_rank_judged (), comp_sim_docno (), comp_docno ();

/* Definitions used for temporary and cached values */
typedef struct {
    char *docno;
    float sim;
    long rank;
    long rel;
} DOCNO_INFO;

/* Current cached query */
static char *current_query = "no query";
static long max_current_query = 0;

/* Space reserved for cached returned values */
static long *rel_levels;
static long max_rel_levels = 0;
static RES_RELS saved_res_rels;
static long *ranked_rel_list;
static long max_ranked_rel_list = 0;

/* Space reserved for intermediate values */
static DOCNO_INFO *docno_info;
static long max_docno_info = 0;


int
te_form_res_rels (const EPI *epi, const REL_INFO *rel_info,
		  const RESULTS *results, RES_RELS *res_rels)
{
    long i;
    long num_results;
    long max_rel;

    TEXT_RESULTS_INFO *text_results_info;
    TEXT_QRELS_INFO *trec_qrels;

    TEXT_QRELS *qrels_ptr, *end_qrels;

    if (0 == strcmp (current_query, results->qid)) {
	/* Have done this query already. Return cached values */
	*res_rels = saved_res_rels;
	return (0);
    }

    /* Check that format type of result info and rel info are correct */
    if (strcmp ("qrels", rel_info->rel_format) ||
	strcmp ("trec_results", results->ret_format)) {
	fprintf (stderr, "trec_eval.form_res_qrels: rel_info format not qrels or results format not trec_results\n");
	return (UNDEF);
    }

    /* Make sure enough space for query and save copy */
    i = strlen(results->qid)+1;
    if (NULL == (current_query =
		 te_chk_and_malloc (current_query, &max_current_query,
				    i, sizeof (char))))
	return (UNDEF);
    (void) strncpy (current_query, results->qid, i);

    text_results_info = (TEXT_RESULTS_INFO *) results->q_results;
    trec_qrels = (TEXT_QRELS_INFO *) rel_info->q_rel_info;

    num_results = text_results_info->num_text_results;

    /* Check and reserve space for output structure */
    /* Reserve space for temp structure copying results */
    if (NULL == (ranked_rel_list =
		 te_chk_and_malloc (ranked_rel_list, &max_ranked_rel_list,
				    num_results, sizeof (long))) ||
        NULL == (docno_info =
		 te_chk_and_malloc (docno_info, &max_docno_info,
				    num_results, sizeof (DOCNO_INFO))))
	return (UNDEF);

    for (i = 0; i < num_results; i++) {
	docno_info[i].docno = text_results_info->text_results[i].docno;
	docno_info[i].sim = text_results_info->text_results[i].sim;
    }

    /* Sort results by sim, breaking ties lexicographically using docno */
    qsort ((char *) docno_info,
	   (int) num_results,
	   sizeof (DOCNO_INFO),
	   comp_sim_docno);

    /* Only look at epi->max_num_docs_per_topic (not normally an issue) */
    if (num_results > epi->max_num_docs_per_topic)
	num_results = epi->max_num_docs_per_topic;

    /* Add ranks to docno_info (starting at 1) */
    for (i = 0; i < num_results; i++) {
        docno_info[i].rank = i+1;
    }

    /* Sort trec_top lexicographically */
    qsort ((char *) docno_info,
           (int) num_results,
           sizeof (DOCNO_INFO),
           comp_docno);

    /* Error checking for duplicates */
    for (i = 1; i < num_results; i++) {
	if (0 == strcmp (docno_info[i].docno,
			 docno_info[i-1].docno)) {
	    fprintf (stderr, "trec_eval.form_res_qrels: duplicate docs %s",
		     docno_info[i].docno);
	    return (UNDEF);
	}
    }

    /* Find max_rel among qid, reserve and zero space for rel_levels */
    /* Check for duplicate docnos. */
    qrels_ptr = trec_qrels->text_qrels;
    end_qrels = &trec_qrels->text_qrels [trec_qrels->num_text_qrels];
    max_rel = qrels_ptr->rel;
    qrels_ptr++;
    while (qrels_ptr < end_qrels) {
	if (max_rel < qrels_ptr->rel)
	    max_rel = qrels_ptr->rel;
	if (0 == strcmp ((qrels_ptr-1)->docno, qrels_ptr->docno)) {
	    fprintf (stderr, "trec_eval.form_res_rels: duplicate docs %s\n",
		     qrels_ptr->docno);
	    return (UNDEF);
	}
	qrels_ptr++;
    }
    if (NULL == (rel_levels =
		 te_chk_and_malloc (rel_levels, &max_rel_levels,
				    (max_rel+1),
				    sizeof (long))))
	return (UNDEF);
    (void) memset (rel_levels, 0, (max_rel+1) * sizeof (long));
    
    /* Construct rank_rel array and rel_levels */
    /* Go through docno_info, trec_qrels in parallel to determine relevance
       for each doc in docno_info.
       Note that trec_qrels already sorted by docno with no duplicates */
    qrels_ptr = trec_qrels->text_qrels;
    end_qrels = &trec_qrels->text_qrels [trec_qrels->num_text_qrels];
    for (i = 0; i < num_results; i++) {
	while (qrels_ptr < end_qrels &&
	       strcmp (qrels_ptr->docno, docno_info[i].docno) < 0) {
	    if (qrels_ptr->rel >= 0)
		rel_levels[qrels_ptr->rel]++;
	    qrels_ptr++;
	}
	if (qrels_ptr >= end_qrels ||
	    strcmp (qrels_ptr->docno, docno_info[i].docno) > 0) {
	    /* Doc is non-judged */
	    docno_info[i].rel = RELVALUE_NONPOOL;
	}
	else {
	    /* Doc is in pool, assign relevance */
	    if (qrels_ptr->rel < 0)
		/* In pool, but unjudged (eg, infAP uses a sample of pool)*/
		docno_info[i].rel = RELVALUE_UNJUDGED;
	    else {
		docno_info[i].rel = qrels_ptr->rel;
	    }
	    if (qrels_ptr->rel >= 0)
		rel_levels[qrels_ptr->rel]++;
	    qrels_ptr++;
	}
    }
    /* Finish counting rels */
    while (qrels_ptr < end_qrels) {
	if (qrels_ptr->rel >= 0)
	    rel_levels[qrels_ptr->rel]++;
	qrels_ptr++;
    }

    /* Construct ranked_rel_list and associated counts */
    saved_res_rels.num_rel_ret = 0;
    saved_res_rels.num_nonpool = 0;
    saved_res_rels.num_unjudged_in_pool = 0;
    saved_res_rels.results_rel_list = ranked_rel_list;
    saved_res_rels.rel_levels = rel_levels;
    if (epi->judged_docs_only_flag) {
	/* If judged_docs_only_flag, then must fix up ranks to
	   reflect unjudged docs being thrown out. Note: done this way
	   to preserve original tie-breaking based on text docno */
	long rrl;
	/* Sort tuples by increasing rank among judged docs*/
	qsort ((char *) docno_info,
	       (int) num_results,
	       sizeof (DOCNO_INFO),
	       comp_rank_judged);
	rrl = 0; i = 0;
	while (i < num_results && docno_info[i].rel >= 0) {
	    if (docno_info[i].rel >= epi->relevance_level)
		saved_res_rels.num_rel_ret++;
	    saved_res_rels.results_rel_list[rrl++] = docno_info[i++].rel;
	}
	saved_res_rels.num_ret = rrl;
    }
    else {
	/* Normal path.  Assign rel value to appropriate rank */
	for (i = 0; i < num_results; i++) {
	    saved_res_rels.results_rel_list[docno_info[i].rank - 1] =
		docno_info[i].rel;
	    if (RELVALUE_NONPOOL == docno_info[i].rel)
		saved_res_rels.num_nonpool++;
	    else if (RELVALUE_UNJUDGED == docno_info[i].rel)
		saved_res_rels.num_unjudged_in_pool++;
	    else {
		if (docno_info[i].rel >= epi->relevance_level)
		    saved_res_rels.num_rel_ret++;
	    }
	}
	saved_res_rels.num_ret = num_results;
    }
    saved_res_rels.num_rel = 0;
    for (i = 0; i <= max_rel; i++) {
	if (saved_res_rels.rel_levels[i]) {
	    saved_res_rels.num_rel_levels = i + 1;
	    if (i >= epi->relevance_level)
		saved_res_rels.num_rel += saved_res_rels.rel_levels[i];
	}
    }
    
    *res_rels = saved_res_rels;

    return (1);
}

static int 
comp_rank_judged (ptr1, ptr2)
DOCNO_INFO *ptr1;
DOCNO_INFO *ptr2;
{
    if (ptr1->rel >= 0 && ptr2->rel >= 0) {
	if (ptr1->rank < ptr2->rank)
	    return (-1);
	if (ptr1->rank > ptr2->rank)
	    return (1);
	return (0);
    }
    if (ptr1->rel >= 0)
	return (-1);
    if (ptr2->rel >= 0)
	return (1);
    return(0);
}

static int 
comp_sim_docno (ptr1, ptr2)
DOCNO_INFO *ptr1;
DOCNO_INFO *ptr2;
{
    if (ptr1->sim > ptr2->sim)
        return (-1);
    if (ptr1->sim < ptr2->sim)
        return (1);
    return (strcmp (ptr2->docno, ptr1->docno));
}

static int 
comp_docno (ptr1, ptr2)
DOCNO_INFO *ptr1;
DOCNO_INFO *ptr2;
{
    return (strcmp (ptr1->docno, ptr2->docno));
}


int 
te_form_res_rels_cleanup ()
{
    if (max_current_query > 0) {
	Free (current_query);
	max_current_query = 0;
	current_query = "no_query";
    }
    if (max_rel_levels > 0) {
	Free (rel_levels);
	max_rel_levels = 0;
    }
    if (max_ranked_rel_list > 0) {
	Free (ranked_rel_list);
	max_ranked_rel_list = 0;
    }
    if (max_docno_info > 0) {
	Free (docno_info);
	max_docno_info = 0;
    }
    return (1);
}
