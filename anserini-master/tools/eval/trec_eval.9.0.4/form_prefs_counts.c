/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/
/* Copyright 2008 Chris Buckley */

#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "trec_format.h"
#include "functions.h"

/* Takes the top docs and judged prefs for a query, and returns a
results_prefs_info object giving the preferences from judged_prefs that
are observed in the retrieved docs.

Three part process here: 
1. Add a docid_rank (0..num_judged) to the judgment prefs that for
   every docno in any judgment pref gives the relative rank at which
   it occurs in the results (a number 0 to num_judged_ret-1).  If the
   docno does not occur in the results, it is given a (consistent)
   value from num_judged_ret to num_judged-1.  These docid_ranks are
   used to represent the docs within preferences.
   
2. Go through the judgements again and represent all preferences per judgment
   group (JG).  Two options for representing the preferences within a JG:
  A. If there is only 1 judgment sub-group (JSG), then the preferences are
   given by a set of equivalence classes (ECs) where all docs with the
   same rel_level in the input are in the same equivalence class.  Thus all
   docs in a higher EC.rel_level are preferred to the docs in a lower
   EC.rel_level.  Since there is only 1 JSG, there are complete preferences
   between any pair of docs not in the same EC.
  B. If there are multiple JSGs, then the JG preference relation is assumed to
   be partial.  Preferences are given by a preference array, where array[i][j]
   is 1 iff doc with docid_rank i is preferred to doc with docid_rank j
   in this JG.  Transitive preferences that aren't explicitly in the
   judgments are added - this happens when (doc A > doc B in JSG jsg1) and
   (doc B > doc C in JSG jsg2).  Note that a JG represents a single information
   need set of judgments, and is required to be consistent (inconsistent
   preferences are represented in different JGs).  Preference array is of
   size num_judged * num_judged.
3. Go through the preference in each JG, and count num_fulfilled and 
   num_possible preferences in categories "retrieved", "implied" and
   "not_retrieved" where
      retrieved means both A and B were retrieved in a pref A > B
      implied means exactly one of A or B was retrieved.
      not_retrieved means neither was retrieved.
   Different evaluation measures may want to do different things with these
   categories.  Counting preferences is accomplished in the two preference
   representations by:
  A. When comparing EC1 and EC2 with EC1.rel_level > EC2.rel_level, compare
   each (docid) rank1 in EC1->ranks with rank2 in EC2->ranks.  
   If rank1 < num_judged_ret then it was retrieved, similarly for rank2.
   If both retrieved, then if rank1 < rank2 the preference is fulfilled
   otherwise it wasn't.
   If rank1 retrieved and rank2 was not, then implied pref fulfilled.
   If rank1 not retrieved and rank2 was, then preference was not fulfilled.
   If both not retrieved, then that count is incremented.
  B. Given preference array PA there are five areas of importance, divided
   by lines i == NJR (where NJR is num_judged_ret), j == NJR, and i == j
                       NJR
       \................|...
       .\...............|...
       ..\.......A1.....|.A3
       ...\.............|...
       ....\............|...
               ...
       ...A2..........\.|...
       ................\|...
  NJR  -----------------|---
       .................|...
       ..........A4.....|.A5
       .................|...

    Area A1 is preferences fulfilled, both retrieved.
    Area A2 is preferences not fulfilled, both retrieved.
    Area A3 is preference implied fulfilled (i retrieved, j not)
    Area A4 is preference implied not fulfilled (i not retrieved, j retrieved)
    Area A5 is both i and j not retrieved.
    Simply count the marks (PA[i][j] == 1) in each appropriate area.

    As well as storing counts within each JG, a counts array for the
    entire pref_results is constructed. Counts_array CA is exactly the same 
    format and size as the preference arrays, except CA[i][j] is the sum
    of the conceptual PA[i][j] over all JGs.  This allows counts of
    confirmations (CA[i][j] > 1) and conflicts (CA[i][j] and CA[j][i] both
    non-zero).

    Not mentioned in steps 2 and 3 above since it adds even more confusion,
    is that the relevant (rel_level > 0.0) and nonrelevant docs are also
    tracked.  Different measures may deal with those preferences differently.


   This procedure may be called repeatedly for a given topic - returned
   values are cached until the qid changes.

   results and prefs_info formats must be "trec_results" and "prefs"
   respectively.  

   UNDEF returned if error, 0 if used cache values, 1 if new values.
*/

typedef struct {
    char *docno;
    float sim;
    long rank;
} DOCNO_RESULTS;

typedef struct {
    char *jg;
    char *jsg;
    float rel_level;
    char *docno;
    long rank;
} PREFS_AND_RANKS;


static int form_jg_ec (const PREFS_AND_RANKS *prefs, const long num_prefs,
		       long *rank_pool_ptr, JG *jg,
		       RESULTS_PREFS *results_prefs);
static int add_ec_pref_to_jg (JG *jg, RESULTS_PREFS *results_prefs);

static int form_jg_pa (const PREFS_AND_RANKS *prefs, const long num_prefs,
		       JG *jg, RESULTS_PREFS *results_prefs);
static int add_transitives (PREFS_ARRAY *pa);
static int add_pa_pref_to_jg (JG *jg, RESULTS_PREFS *results_prefs);
static int mult_and_check_change (const PREFS_ARRAY *a1, const PREFS_ARRAY *a2,
				  PREFS_ARRAY *res);

static int form_prefs_and_ranks (const EPI*epi,
				 const TEXT_RESULTS_INFO *text_results_info,
				 const TEXT_PREFS_INFO *trec_prefs,
				 PREFS_AND_RANKS *prefs_and_ranks,
				 long *num_judged, long *num_judged_ret);

static void init_prefs_array (PREFS_ARRAY *pa);
static void init_counts_array (COUNTS_ARRAY *ca);
static int comp_prefs_and_ranks_jg_rel_level ();
static int comp_prefs_and_ranks_docno();
static int comp_sim_docno (), comp_docno (), comp_results_inc_rank ();
static void debug_print_ec (EC *ec),debug_print_prefs_array (PREFS_ARRAY *pa),
    debug_print_counts_array (COUNTS_ARRAY *ca), debug_print_jg (JG *jg),
    debug_print_results_prefs (RESULTS_PREFS *rp);
static void debug_print_docno_results (DOCNO_RESULTS *dr, long num_results,
				       char *location);
static void debug_print_prefs_and_ranks (PREFS_AND_RANKS *par, long num_prefs,
					 char *location);


/* Intermediate Temp storage. Not malloc'd and freed every query just
   for memory management efficiency (avoids fragmentations and
   thus effects on caching) */
 /* Temp Structure for mapping results docno to results rank */

/* Current cached query */
static char *current_query = "no query";
static long max_current_query = 0;

/* Space reserved for cached returned values */
static long num_judged_ret;
static long num_judged;
static long num_jgs;
static JG *jgs;
static long max_num_jgs = 0;
static long *rank_pool;
static long max_rank_pool = 0;
static EC *ec_pool;
static long max_ec_pool = 0;
static unsigned short *ca_pool;      
static long max_ca_pool = 0;
static unsigned short **ca_ptr_pool; 
static long max_ca_ptr_pool = 0;
static unsigned char *pa_pool;
static long max_pa_pool = 0;
static unsigned char **pa_ptr_pool;
static long max_pa_ptr_pool = 0;
static float *rel_pool;
static long max_rel_pool = 0;
/* Space reserved for intermediate values */
static PREFS_AND_RANKS *prefs_and_ranks;
static long max_prefs_and_ranks = 0;
static DOCNO_RESULTS *docno_results;
static long max_docno_results = 0;
static unsigned char *temp_pa_pool;
static long max_temp_pa_pool;
static unsigned char **temp_pa_ptr_pool;
static long max_temp_pa_ptr_pool;

static long saved_num_judged = 0;



int
form_prefs_counts (const EPI *epi, const REL_INFO *rel_info,
		   const RESULTS *results, RESULTS_PREFS *results_prefs)
{
    long i;
    char *jgid, *jsgid;
    long jg_ind;
    long num_jgs_with_subgroups;
    float rel_level;

    EC * ec_pool_ptr;
    float *rel_pool_ptr;
    long *rank_pool_ptr;
    unsigned char *pa_pool_ptr;
    unsigned char **pa_ptr_pool_ptr;

    long start_jg;
    long num_rel_level;
    long num_sub_group;
    
    TEXT_RESULTS_INFO *text_results_info;
    TEXT_PREFS_INFO *trec_prefs;

    if (epi->debug_level >= 3)
	printf ("Debug: Form_prefs starting query '%s'\n", results->qid);

    if (0 == strcmp (current_query, results->qid)) {
	/* Have done this query already. Return cached values */
	results_prefs->num_jgs = num_jgs;
	results_prefs->jgs = jgs;
	results_prefs->num_judged = num_judged;
	results_prefs->num_judged_ret = num_judged_ret;
	results_prefs->pref_counts = (COUNTS_ARRAY) {ca_pool,
						     ca_ptr_pool,
						     num_judged};
	if (epi->debug_level >= 3)
	    printf ("Returned Cached Form_prefs\n");
	return (0);
    }

    /* Check that format type of result info and rel info are correct */
    if ((strcmp ("prefs", rel_info->rel_format) &&
	 strcmp ("qrels_prefs", rel_info->rel_format)) ||
	strcmp ("trec_results", results->ret_format)) {
	fprintf (stderr, "trec_eval.form_prefs_info: prefs_info format not (prefs or qrels_prefs) or results format not trec_results\n");
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
    trec_prefs = (TEXT_PREFS_INFO *) rel_info->q_rel_info;


    /* Reserve space for returned and intermediate values, if needed */
    if (NULL == (prefs_and_ranks =
		 te_chk_and_malloc (prefs_and_ranks, &max_prefs_and_ranks,
				 trec_prefs->num_text_prefs,
				 sizeof (PREFS_AND_RANKS))) ||
	NULL == (ec_pool =
		 te_chk_and_malloc (ec_pool, &max_ec_pool,
				 trec_prefs->num_text_prefs, sizeof (EC))) ||
	NULL == (rank_pool =
		 te_chk_and_malloc (rank_pool, &max_rank_pool,
				 trec_prefs->num_text_prefs, sizeof (long))))
	return (UNDEF);

    /* get prefs_and_ranks from results and prefs. Will be sorted by
       jg, jsg, rel_level, rank. Set num_judged, num_judged_ret */
    if (UNDEF == form_prefs_and_ranks (epi, text_results_info, trec_prefs,
				       prefs_and_ranks, &num_judged,
				       &num_judged_ret))
	return (UNDEF);

    /* Go through prefs_and ranks, count and reserve space for judgment groups.
       Also count number of JGs that have subgroups and will need preference
       arrays. */
    jgid = "";  jsgid = "";
    num_jgs = 0;  num_jgs_with_subgroups = 0;
    for (i = 0; i < trec_prefs->num_text_prefs; i++) {
	if (strcmp (jgid, prefs_and_ranks[i].jg)) {
	    /* New JG */
	    jgid = prefs_and_ranks[i].jg;
	    jsgid = prefs_and_ranks[i].jsg;
	    num_jgs++;
	}
	else if (strcmp (jsgid, prefs_and_ranks[i].jsg)) {
	    jsgid = prefs_and_ranks[i].jsg;
	    num_jgs_with_subgroups++;
	}
    }

    /* Reserve pool space for JGs, and final pref_counts */
    if (NULL == (jgs =
		 te_chk_and_malloc (jgs, &max_num_jgs, num_jgs, sizeof (JG))) ||
	NULL == (ca_pool =
		 te_chk_and_malloc (ca_pool, &max_ca_pool,
				 num_judged * num_judged,
				 sizeof (unsigned short))) ||
	NULL == (ca_ptr_pool =
		 te_chk_and_malloc (ca_ptr_pool, &max_ca_ptr_pool, num_judged, 
				 sizeof (unsigned short *))))
	return (UNDEF);

    if (num_jgs_with_subgroups) {
	/* Reserve pool space for preference arrays, and rel_level arrays */
	if (NULL == (rel_pool =
		     te_chk_and_malloc (rel_pool, &max_rel_pool,
				     num_judged * num_jgs_with_subgroups,
				     sizeof (float))) ||
	    NULL == (pa_pool =
		     te_chk_and_malloc (pa_pool, &max_pa_pool,
			       num_judged * num_judged * num_jgs_with_subgroups,
				     sizeof (unsigned char))) ||
	    NULL == (pa_ptr_pool =
		     te_chk_and_malloc (pa_ptr_pool, &max_pa_ptr_pool,
				     num_judged * num_jgs_with_subgroups,
				     sizeof (unsigned char *))))
	    return (UNDEF);
    }

    ec_pool_ptr = ec_pool;
    rel_pool_ptr = rel_pool;
    rank_pool_ptr = rank_pool;
    pa_pool_ptr = pa_pool;
    pa_ptr_pool_ptr = pa_ptr_pool;

    /* setup returned results_prefs so its summary values can be filled in */
    results_prefs->num_jgs = num_jgs;
    results_prefs->jgs = jgs;
    results_prefs->num_judged = num_judged;
    results_prefs->num_judged_ret = num_judged_ret;
    results_prefs->pref_counts = (COUNTS_ARRAY) {ca_pool, ca_ptr_pool,
						 num_judged};
    init_counts_array (&results_prefs->pref_counts);

    /* Go through prefs_and_ranks, determine and construct appropriate JG
       preference format.  Preferences are counted and add to summary values
       as each JG is handled. */
    jg_ind = 0;
    start_jg = 0;
    num_rel_level = 0;
    num_sub_group = 0;
    rel_level = -3.0;         /* Illegal rel_level */
    jgid = prefs_and_ranks[0].jg;
    jsgid = "";
    for (i = 0; i < trec_prefs->num_text_prefs; i++) {
	if (strcmp (jgid, prefs_and_ranks[i].jg)) {
	    /* New judgment group. Form previous JG and initialize coounts
	       for new JG */
	    if (num_sub_group > 1) {
		/* Preference array JG */
		jgs[jg_ind].num_ecs = 0;  /* Indicator thet prefs_array used */
		jgs[jg_ind].prefs_array = (PREFS_ARRAY) {pa_pool_ptr,
						       pa_ptr_pool_ptr,
						       num_judged};
		pa_pool_ptr += num_judged * num_judged;
		pa_ptr_pool_ptr += num_judged;
		jgs[jg_ind].rel_array = rel_pool_ptr;
		rel_pool_ptr += num_judged;
		if (UNDEF == form_jg_pa (&prefs_and_ranks[start_jg],
					 i - start_jg,
					 &jgs[jg_ind],
					 results_prefs))
		    return (UNDEF);
	    }
	    else {
		/* EC JG */
		jgs[jg_ind].ecs = ec_pool_ptr;
		ec_pool_ptr += num_rel_level;
		jgs[jg_ind].num_ecs = num_rel_level;
		if (UNDEF == form_jg_ec (&prefs_and_ranks[start_jg],
					 i - start_jg,
					 rank_pool_ptr,
					 &jgs[jg_ind],
					 results_prefs))
		    return (UNDEF);
		rank_pool_ptr += i - start_jg;
	    }
	    jgid = prefs_and_ranks[i].jg;
	    jg_ind++;
	    jsgid = "";
	    start_jg = i;
	    num_sub_group = 0;
	    num_rel_level = 0;
	    rel_level = -3.0; /* Illegal rel_level */
	}
	if (strcmp (jsgid, prefs_and_ranks[i].jsg)) {
	    num_sub_group++;
	    jsgid = prefs_and_ranks[i].jsg;
	}
	if (rel_level != prefs_and_ranks[i].rel_level) {
	    num_rel_level++;
	    rel_level = prefs_and_ranks[i].rel_level;
	}
    }
    /*  Form last JG  */
    if (num_sub_group > 1) {
	/* Preference array JG */
	jgs[jg_ind].num_ecs = 0;  /* Indicator thet prefs_array used */
	jgs[jg_ind].prefs_array = (PREFS_ARRAY) {pa_pool_ptr,
					       pa_ptr_pool_ptr,
					       num_judged};
	pa_pool_ptr += num_judged * num_judged;
	pa_ptr_pool_ptr += num_judged;
	jgs[jg_ind].rel_array = rel_pool_ptr;
	rel_pool_ptr += num_judged;
	if (UNDEF == form_jg_pa (&prefs_and_ranks[start_jg],
				 i - start_jg,
				 &jgs[jg_ind],
				 results_prefs))
	    return (UNDEF);
    }
    else {
	/* EC JG */
	jgs[jg_ind].ecs = ec_pool_ptr;
	ec_pool_ptr += num_rel_level;
	jgs[jg_ind].num_ecs = num_rel_level;
	if (UNDEF == form_jg_ec (&prefs_and_ranks[start_jg],
				 i - start_jg,
				 rank_pool_ptr,
				 &jgs[jg_ind],
				 results_prefs))
	    return (UNDEF);
    }
    if (epi->debug_level >= 3)
	debug_print_results_prefs (results_prefs);

    return (1);
}

static int
form_jg_ec (const PREFS_AND_RANKS *prefs, const long num_prefs,
	    long *rank_pool_ptr, JG *jg, RESULTS_PREFS *results_prefs) 
{
    EC *ec_ptr =jg->ecs;
    long *rank_ptr = rank_pool_ptr;
    long i;
    float rel_level;

    /* Fill in prefs array with all known info from prefs */
    /* prefs is sorted by jsg, then rel_level, then rank */
    rel_level = prefs[0].rel_level;
    *ec_ptr = (EC) {rel_level, 0, rank_ptr};
    for (i = 0; i < num_prefs; i++) {
	if (prefs[i].rel_level != rel_level) {
	    /* new equivalence class */
	    rel_level = prefs[i].rel_level;
	    rank_ptr += ec_ptr->num_in_ec;
	    ec_ptr++;
	    *ec_ptr = (EC) {prefs[i].rel_level, 0, rank_ptr};
	}
	ec_ptr->docid_ranks[ec_ptr->num_in_ec++] = prefs[i].rank;
    }

    /* Add counts of preference fulfilled and possible to jg and 
       summary counts */
    if (UNDEF == add_ec_pref_to_jg (jg, results_prefs))
	return (UNDEF);

    return (1);
}

/* Add counts of preference fulfilled and possible to jg for EC pref info */
static int
add_ec_pref_to_jg (JG *jg, RESULTS_PREFS *results_prefs)
{
    long num_judged_ret = results_prefs->num_judged_ret;
    unsigned short **pc = results_prefs->pref_counts.array;
    long *ptr1, *ptr2;
    long ec1, ec2;

    jg->num_prefs_fulfilled_ret = 0; 
    jg->num_prefs_possible_ret = 0;  
    jg->num_prefs_fulfilled_imp = 0;
    jg->num_prefs_possible_imp = 0;
    jg->num_prefs_possible_notoccur = 0;
    jg->num_nonrel = 0;
    jg->num_nonrel_ret = 0;
    jg->num_rel = 0;       
    jg->num_rel_ret = 0;   

    /* Go through all ecs counting preferences, and setting up 
       prefs_count */
    for (ec1 = 0; ec1 < jg->num_ecs; ec1++) {
	/* Count num rel and ret */
	for (ptr1 = jg->ecs[ec1].docid_ranks;
	     ptr1 < &jg->ecs[ec1].docid_ranks[jg->ecs[ec1].num_in_ec];
	     ptr1++) {
	    if (*ptr1 >= num_judged_ret)
		break;
	}
	if (jg->ecs[ec1].rel_level > 0.0) {
	    jg->num_rel_ret += ptr1 - jg->ecs[ec1].docid_ranks;
	    jg->num_rel += jg->ecs[ec1].num_in_ec;
	}
	else {
	    jg->num_nonrel_ret += ptr1 - jg->ecs[ec1].docid_ranks;
	    jg->num_nonrel += jg->ecs[ec1].num_in_ec;
	}

	/* Count prefs */
	for (ec2 = ec1 + 1; ec2 < jg->num_ecs; ec2++) {
	    for (ptr1 = jg->ecs[ec1].docid_ranks;
		 ptr1 < &jg->ecs[ec1].docid_ranks[jg->ecs[ec1].num_in_ec];
		 ptr1++) {
		for (ptr2 = jg->ecs[ec2].docid_ranks;
		     ptr2 < &jg->ecs[ec2].docid_ranks[jg->ecs[ec2].num_in_ec];
		     ptr2++) {
		    /* Add pref to summary info */
		    pc[*ptr1][*ptr2]++;
		    /* check for inconsistency: same doc in multiple ec */
		    if (*ptr1 == *ptr2) {
			fprintf (stderr,
				 "trec_eval.form_prefs_counts: Internal docid %ld occurs with different rel_level in same jsg\n", *ptr1);
			return (UNDEF);
			/* need to check this in pa also? */
		    }
		    /* Add count to appropriate jg_num* */
		    if (*ptr1 < *ptr2) {
			/* judgment fulfilled */
			if (*ptr2 < num_judged_ret)
			    jg->num_prefs_fulfilled_ret++;
			else if (*ptr1 < num_judged_ret)
			    jg->num_prefs_fulfilled_imp++;
			else
			    jg->num_prefs_possible_notoccur++;
		    }
		    else {
			if (*ptr1 < num_judged_ret)
			    jg->num_prefs_possible_ret++;
			else if (*ptr2 < num_judged_ret)
			    jg->num_prefs_possible_imp++;
			else
			    jg->num_prefs_possible_notoccur++;
		    }
		}
	    }
	}
    }

    jg->num_prefs_possible_ret += jg->num_prefs_fulfilled_ret;
    jg->num_prefs_possible_imp += jg->num_prefs_fulfilled_imp;
    return (1);
}

static int
form_jg_pa (const PREFS_AND_RANKS *prefs, const long num_prefs,
	    JG *jg, RESULTS_PREFS *results_prefs) 
{
    long i,j;

    PREFS_ARRAY *pa = &jg->prefs_array;

    /* Initialize and zero prefs_array */
    init_prefs_array(pa);

    /* Initialize and set rel_level to -1.  Will check for inconsistencies
       (rel_level for some docid 0.0 and some > 0.0) as prefs handled */
    for (i = 0; i < pa->num_judged; i++) {
	jg->rel_array[i] = -1.0;
    }

    /* Fill in prefs array with all known info from prefs */
    /* prefs is sorted by jsg, then rel_level, then rank */
    for (i = 0; i < num_prefs; i++) {
	/* check for consistency and add rel_level info */
	if ((jg->rel_array[prefs[i].rank] > 0.0 &&
	     prefs[i].rel_level == 0.0) ||
	    (jg->rel_array[prefs[i].rank] == 0.0 &&
	     prefs[i].rel_level > 0.0)) {
	    fprintf (stderr,
		     "trec_eval.form_prefs_counts: doc '%s' has both 0 and non-0 rel_level assigned\n", 
		     prefs[i].docno);
	    return (UNDEF);
	}
	jg->rel_array[prefs[i].rank] = prefs[i].rel_level;

	/* Add preference for i to all other entries in this JSG with lower
	 rel_levels */
	j = i+1;
	/* Just skip over all docs at same rel_level */
	while (j < num_prefs &&
	       0 == strcmp (prefs[i].jsg, prefs[j].jsg) &&
	       prefs[i].rel_level == prefs[j].rel_level)
	    j++;
	/* Rest of JSG has lower rel_level */
	while (j < num_prefs &&
	       0 == strcmp (prefs[i].jsg, prefs[j].jsg)) {
	    pa->array[prefs[i].rank][prefs[j].rank] = 1;
	    j++;
	}
    }

    /* Add transitive preferences to pa */
    if (UNDEF == add_transitives (pa))
	return (UNDEF);

    /* Add counts of preference fulfilled and possible to jg and 
       summary counts */
    if (UNDEF == add_pa_pref_to_jg (jg, results_prefs))
	return (UNDEF);

    return (1);
}

static int
add_transitives(PREFS_ARRAY *pa)
{
    PREFS_ARRAY m1;
    PREFS_ARRAY m2;

    long i, j;
    PREFS_ARRAY *array_in, *array_out, *temp;

    /* Need two temporary arrays of same size as pa. Reserve space and
       zero out arrays */
    if (NULL == (temp_pa_pool =
		 te_chk_and_malloc (temp_pa_pool, &max_temp_pa_pool,
				 2 * pa->num_judged * pa->num_judged,
				 sizeof (unsigned char))) ||
	NULL == (temp_pa_ptr_pool =
		 te_chk_and_malloc (temp_pa_ptr_pool, &max_temp_pa_ptr_pool,
				 2 * pa->num_judged,
				 sizeof (unsigned char *))))
	return (UNDEF);

    m1 = (PREFS_ARRAY) {temp_pa_pool, temp_pa_ptr_pool, pa->num_judged};
    m2 = (PREFS_ARRAY) {temp_pa_pool + pa->num_judged * pa->num_judged,
		       temp_pa_ptr_pool + pa->num_judged,
		       pa->num_judged};
    if (pa->num_judged != saved_num_judged) {
	/* if new size array, must reset row pointers */
	saved_num_judged = pa->num_judged;
	for (i = 0; i < pa->num_judged; i++) {
	    m1.array[i] = m1.full_array + i * pa->num_judged;
	    m2.array[i] = m2.full_array + i * pa->num_judged;
	}
    }

    /* Add diagonal all ones in pa */
    for (i = 0; i < pa->num_judged; i++) {
	pa->array[i][i] = 1;
    }

    /* Multiply prefs_array by itself until there are no changes */
    array_in = pa;
    array_out = &m1;
    if (mult_and_check_change (pa, array_in, array_out)) {
	array_in = array_out;
	array_out = &m2;
	while (mult_and_check_change (pa, array_in, array_out)) {
	    temp = array_in;
	    array_in = array_out;
	    array_out = temp;
	}
	/* There were changes,  now set pa values to those of array_out */
	for (i = 0; i < pa->num_judged; i++) {
	    for (j = 0; j <pa-> num_judged; j++) {
		pa->array[i][j] = array_out->array[i][j];
	    }
	}
    }

    /* Reset all diagonals to 0 in pa */
    for (i = 0; i < pa->num_judged; i++) {
	pa->array[i][i] = 0;
    }

    /* Check for inconsistencies */
    for (i = 0; i < pa->num_judged; i++) {
	for (j = 0; j <pa-> num_judged; j++) {
	    if (i != j && pa->array[i][j] && pa->array[j][i]) {
		fprintf (stderr, "trec_eval.form_prefs_counts: Pref inconsistency found\n      internal rank %ld and internal rank %ld are conflicted\n", i, j);
		abort();
		return (-1);
	    }
	}
    }

    return (1);
}


/* Add counts of preference fulfilled and possible to jg */
static int
add_pa_pref_to_jg (JG *jg, RESULTS_PREFS *results_prefs)
{
    unsigned char **a = jg->prefs_array.array;
    unsigned short **c = results_prefs->pref_counts.array;
    long num_judged = results_prefs->num_judged;
    long num_judged_ret = results_prefs->num_judged_ret;
    long i,j;

    jg->num_prefs_fulfilled_ret = 0; 
    jg->num_prefs_possible_ret = 0;  
    jg->num_prefs_fulfilled_imp = 0;
    jg->num_prefs_possible_imp = 0;
    jg->num_prefs_possible_notoccur = 0;
    jg->num_nonrel = 0;
    jg->num_nonrel_ret = 0;
    jg->num_rel = 0;       
    jg->num_rel_ret = 0;   

    for (i = 0; i < num_judged_ret; i++) {
	if (jg->rel_array[i] > 0.0)
	    jg->num_rel_ret++;
	else if (jg->rel_array[i] == 0.0)
	    jg->num_nonrel_ret++;
    }
    jg->num_rel = jg->num_rel_ret;
    jg->num_nonrel = jg->num_nonrel_ret;
    for (i = num_judged_ret; i < num_judged; i++) {
	if (jg->rel_array[i] > 0.0)
	    jg->num_rel++;
	else if  (jg->rel_array[i] == 0.0)
	    jg->num_nonrel++;
    }

    for (i = 0; i < num_judged_ret; i++) {
	for (j = 0; j < i; j++) {
	    if (a[i][j]) {
		/* Pref not fulfilled.  Area A2 (see comment at top) */
		c[i][j]++;
		jg->num_prefs_possible_ret++;
	    }
	}
	for (j = i+1; j < num_judged_ret; j++) {
	    if (a[i][j]) {
		/* Pref fulfilled.  Area A1 (see comment at top) */
		c[i][j]++;
		jg->num_prefs_fulfilled_ret++;
	    }
	}
	for (j = num_judged_ret; j < num_judged; j++) {
	    if (a[i][j]) {
		/* Pref fulfilled implied.  Area A3 (see comment at top) */
		c[i][j]++;
		jg->num_prefs_fulfilled_imp++;
	    }
	}
    }
    for (i = num_judged_ret; i < num_judged; i++) {
	for (j = 0; j < num_judged_ret; j++) {
	    if (a[i][j]) {
		/* Pref not fulfilled implied.  Area A4 (see comment at top) */
		c[i][j]++;
		jg->num_prefs_possible_imp++;
	    }
	}
	for (j = num_judged_ret; j < num_judged; j++) {
	    if (a[i][j]) {
		/* Pref not occur at all.  Area A5 (see comment at top) */
		c[i][j]++;
		jg->num_prefs_possible_notoccur++;
	    }
	}
    }

    jg->num_prefs_possible_ret += jg->num_prefs_fulfilled_ret;
    jg->num_prefs_possible_imp += jg->num_prefs_fulfilled_imp;
    return (1);
}

/* Multiply array a1 * array a2 and put result in array res.
   Return (1) if array res is changed from array a2 after multiplication
   else return 0 */
static int
mult_and_check_change (const PREFS_ARRAY *a1, const PREFS_ARRAY *a2, PREFS_ARRAY *res)
{
    int change = 0;
    long i, j, k;
    for (i = 0; i < a1->num_judged; i++) {
	for (j = 0; j < a1->num_judged; j++) {
	    res->array[i][j] = 0;
	    for (k = 0; k < a1->num_judged; k++) {
		if (a1->array[i][k]  && a2->array[k][j]) {
		    res->array[i][j] = 1;
		    break;
		}
	    }
	    
	    if (a2->array[i][j] != res->array[i][j]) change = 1;
	}
    }
    return (change);
}

static int form_prefs_and_ranks (const EPI*epi,
				 const TEXT_RESULTS_INFO *text_results_info,
				 const TEXT_PREFS_INFO *trec_prefs,
				 PREFS_AND_RANKS *prefs_and_ranks,
				 long *num_judged, long *num_judged_ret)
{
    long lnum_judged_ret; /* local num_judged_ret */
    long next_unretrieved_rank;
    long i;
    long num_results;     /* Current number of results (changes as docs
			     thrown away from docno_results) */
    long num_prefs = trec_prefs->num_text_prefs;
    PREFS_AND_RANKS *ranks_ptr, *end_ranks, *start_ptr;

    /* Copy docno results and add ranks */
    num_results = text_results_info->num_text_results;
    if (NULL == (docno_results = 
		 te_chk_and_malloc (docno_results, &max_docno_results,
				 num_results, sizeof (DOCNO_RESULTS))))
	return (UNDEF);

    for (i = 0; i < num_results; i++) {
	docno_results[i].docno = text_results_info->text_results[i].docno;
	docno_results[i].sim = text_results_info->text_results[i].sim;
    }
    /* Sort results by sim, breaking ties lexicographically using docno */
    qsort ((char *) docno_results,
	   (int) num_results,
	   sizeof (DOCNO_RESULTS),
	   comp_sim_docno);

    if (epi->debug_level >= 5)
	debug_print_docno_results (docno_results, num_results,
				   "After input, before ranks");

    /* Only look at epi->max_num_docs_per_topic (not normally an issue) */
    if (num_results > epi->max_num_docs_per_topic)
	num_results = epi->max_num_docs_per_topic;
    /* Add ranks to docno_results (starting at 1) */
    for (i = 0; i < num_results; i++) {
        docno_results[i].rank = i+1;
    }
    /* Sort docno_results by increasing docno */
    qsort ((char *) docno_results,
           (int) num_results,
           sizeof (DOCNO_RESULTS),
           comp_docno);
    /* Error checking for duplicates */
    for (i = 1; i < num_results; i++) {
	if (0 == strcmp (docno_results[i].docno,
			 docno_results[i-1].docno)) {
	    fprintf (stderr, "trec_eval.form_prefs_counts: duplicate docs %s",
		     docno_results[i].docno);
	    return (UNDEF);
	}
    }

    if (epi->debug_level >= 5)
	debug_print_docno_results (docno_results, num_results,
				   "After -M, ranks");

    /* Copy trec_prefs - sort by docno.  Space already reserved */
    for (i = 0; i < num_prefs; i++) {
	prefs_and_ranks[i].jg = trec_prefs->text_prefs[i].jg;
	prefs_and_ranks[i].jsg = trec_prefs->text_prefs[i].jsg;
	prefs_and_ranks[i].rel_level = trec_prefs->text_prefs[i].rel_level;
	prefs_and_ranks[i].docno = trec_prefs->text_prefs[i].docno;
    }
    qsort ((char *) prefs_and_ranks,
	   (int) num_prefs,
	   sizeof (PREFS_AND_RANKS),
	   comp_prefs_and_ranks_docno);

    if (epi->debug_level >= 5)
	debug_print_prefs_and_ranks (prefs_and_ranks, num_prefs,
				   "Input, before ranks");

    /* Go through docno_results and prefs_and_ranks in parallel (both sorted
       by docno) and assign ranks of -1 to those docs in docno_results that
       are not in prefs_and_ranks */
    ranks_ptr = prefs_and_ranks;
    end_ranks = &prefs_and_ranks[num_prefs];
    for (i = 0; i < num_results && ranks_ptr < end_ranks; i++) {
        while (ranks_ptr < end_ranks &&
               strcmp (ranks_ptr->docno, docno_results[i].docno) < 0)
            ranks_ptr++;
        if (ranks_ptr < end_ranks &&
	    strcmp (ranks_ptr->docno, docno_results[i].docno) == 0) {
	    do {
		ranks_ptr++;
	    } while (ranks_ptr < end_ranks &&
		     strcmp (ranks_ptr->docno, docno_results[i].docno) == 0);
	}
	else
	    /* dpcno_results[i] not judged */
	    docno_results[i].rank = -1;
    }
    /* sort docno_results[0..i] by increasing rank */
    num_results = i;
    qsort ((char *) docno_results,
	   (int) num_results,
	   sizeof (DOCNO_RESULTS),
	   comp_results_inc_rank);

    if (epi->debug_level >= 5)
	debug_print_docno_results (docno_results, num_results,
				   "After marking not judged");

    /* Assign new docid ranks starting at 0 for only judged docs */
    lnum_judged_ret = 0;
    for (i = 0; i < num_results; i ++) {
	if (docno_results[i].rank > -1) {
	    docno_results[lnum_judged_ret].docno = docno_results[i].docno;
	    docno_results[lnum_judged_ret].rank = lnum_judged_ret;
	    lnum_judged_ret++;
	}
    }
    num_results = lnum_judged_ret;
    /* Sort docno_results by increasing docno */
    qsort ((char *) docno_results,
           (int) num_results,
           sizeof (DOCNO_RESULTS),
           comp_docno);

    if (epi->debug_level >= 5)
	debug_print_docno_results (docno_results, num_results,
				   "After assigning docid_ranks");
    /* Go through reduced docno_results and prefs_and_ranks in parallel and
       assign ranks in prefs_and_ranks from docno_results. Note all docnos
       in docno_results now guaranteed to be in prefs_and_ranks */
    ranks_ptr = prefs_and_ranks;
    end_ranks = &prefs_and_ranks[num_prefs];
    next_unretrieved_rank = num_results;
    for (i = 0; i < num_results; i++) {
	while (strcmp (ranks_ptr->docno, docno_results[i].docno) < 0) {
	    ranks_ptr->rank = next_unretrieved_rank++;
	    start_ptr = ranks_ptr++;
	    while (strcmp (ranks_ptr->docno, start_ptr->docno) == 0) {
		ranks_ptr->rank = start_ptr->rank;
		ranks_ptr++;
	    }
	}
	ranks_ptr->rank = docno_results[i].rank;
	start_ptr = ranks_ptr++;
	while (ranks_ptr < end_ranks &&
	       strcmp (ranks_ptr->docno, start_ptr->docno) == 0) {
	    ranks_ptr->rank = start_ptr->rank;
	    ranks_ptr++;
	}
    }
    while (ranks_ptr < end_ranks) {
	ranks_ptr->rank = next_unretrieved_rank++;
	start_ptr = ranks_ptr++;
	while (ranks_ptr < end_ranks &&
	       strcmp (ranks_ptr->docno, start_ptr->docno) == 0) {
	    ranks_ptr->rank = start_ptr->rank;
	    ranks_ptr++;
	}
    }

    /* Now sort prefs_and_ranks by jg, jsg, rel_level, docid_rank */
    qsort ((void *) prefs_and_ranks,
	   num_prefs,
	   sizeof (PREFS_AND_RANKS),
	   comp_prefs_and_ranks_jg_rel_level);

    if (epi->debug_level >= 4) {
	printf ("Form_prefs: num_judged %ld, num_judged_ret %ld\n",
		next_unretrieved_rank, num_results);
	debug_print_prefs_and_ranks (prefs_and_ranks, num_prefs,
				   "Final prefs");
    }

    *num_judged = next_unretrieved_rank;
    *num_judged_ret = num_results;
    return (1);
}

static void
init_prefs_array (PREFS_ARRAY *pa)
{
    unsigned char *row_ptr;
    long i;
    (void) memset ((void *) pa->full_array, 0,
		   pa->num_judged * pa->num_judged *
		   sizeof (unsigned char));
    row_ptr = pa->full_array;
    for (i = 0; i < pa->num_judged; i++) {
	pa->array[i] = row_ptr;
	row_ptr += pa->num_judged;
    }
}

static void
init_counts_array (COUNTS_ARRAY *ca)
{
    unsigned short *row_ptr;
    long i;
    (void) memset ((void *) ca->full_array, 0,
		   ca->num_judged * ca->num_judged *
		   sizeof (unsigned short));
    row_ptr = ca->full_array;
    for (i = 0; i < ca->num_judged; i++) {
	ca->array[i] = row_ptr;
	row_ptr += ca->num_judged;
    }
}


static int 
comp_prefs_and_ranks_docno (PREFS_AND_RANKS *ptr1, PREFS_AND_RANKS *ptr2)
{
    return (strcmp (ptr1->docno, ptr2->docno));
}

static int 
comp_prefs_and_ranks_jg_rel_level (PREFS_AND_RANKS *ptr1, PREFS_AND_RANKS *ptr2)
{
    int jg_comp = strcmp (ptr1->jg, ptr2->jg);
    if (jg_comp != 0) return (jg_comp);
    jg_comp = strcmp (ptr1->jsg, ptr2->jsg);
    if (jg_comp != 0) return (jg_comp);
    if (ptr1->rel_level > ptr2->rel_level) return (-1);
    if (ptr1->rel_level < ptr2->rel_level) return (1);
    return (ptr1->rank - ptr2->rank);
}

static int 
comp_sim_docno (ptr1, ptr2)
DOCNO_RESULTS *ptr1;
DOCNO_RESULTS *ptr2;
{
    if (ptr1->sim > ptr2->sim)
        return (-1);
    if (ptr1->sim < ptr2->sim)
        return (1);
    return (strcmp (ptr1->docno, ptr2->docno));
}

static int 
comp_docno (DOCNO_RESULTS *ptr1, DOCNO_RESULTS *ptr2)
{
    return (strcmp (ptr1->docno, ptr2->docno));
}

static int 
comp_results_inc_rank (DOCNO_RESULTS *ptr1, DOCNO_RESULTS *ptr2)
{
    return (ptr1->rank - ptr2->rank);
}


static void
debug_print_prefs_and_ranks (PREFS_AND_RANKS *par, long num_prefs,
			     char *location)
{
    long i;
    printf ("Prefs_and_ranks Dump.  num_pref_lines %ld,  %s\n",
	    num_prefs, location);
    for (i = 0; i < num_prefs; i++) {
	printf ("  %s\t%s\t%4.2f\t%s\t%3ld\n",
		par[i].jg, par[i].jsg, par[i].rel_level, par[i].docno,
		par[i].rank);
    }
    fflush (stdout);
}

static void
debug_print_docno_results (DOCNO_RESULTS *dr, long num_results,
			     char *location)
{
    long i;
    printf ("Docno_results Dump.  num_results %ld, %s\n",
	    num_results, location);
    for (i = 0; i < num_results; i++) {
	printf ("  %s\t%4.2f\t%3ld\n",
		dr[i].docno, dr[i].sim, dr[i].rank);
    }
    fflush (stdout);
}

static void
debug_print_ec (EC *ec) {
    long i;
    printf ("    EC Dump. Rel_level %4.2f. Num_docid_ranks %ld",
	    ec->rel_level, ec->num_in_ec);
    for (i = 0; i < ec->num_in_ec; i++) {
	if (0 == (i % 10))
	    printf ("\n      ");
	printf ("%3ld ", ec->docid_ranks[i]);
    }
    putchar ('\n');
    fflush (stdout);
}

static void
debug_print_prefs_array (PREFS_ARRAY *pa) {
    long i,j;
    printf ("    Prefs_Array Dump. Num_judged %ld", pa->num_judged);
    for (i = 0; i < pa->num_judged; i++) {
	printf ("\n      Row %3ld\n      ", i);
	for (j = 0; j < pa->num_judged; j++) {
	    if (j && 0 == (j % 20))
		printf ("    (%ld)\n     ", j);
	    printf ("%2hhd", pa->array[i][j]);
	}
    }
    putchar ('\n');
    fflush (stdout);
}

static void
debug_print_counts_array (COUNTS_ARRAY *ca) {
    long i,j;
    printf ("  Counts_Array Dump. Num_judged %ld", ca->num_judged);
    for (i = 0; i < ca->num_judged; i++) {
	printf ("\n    Row %3ld\n    ", i);
	for (j = 0; j < ca->num_judged; j++) {
	    if (j && 0 == (j % 20))
		printf ("    (%ld)\n   ", j);
	    printf ("%2hd ", ca->array[i][j]);
	}
    }
    putchar ('\n');
    fflush (stdout);
}


static void
debug_print_jg (JG *jg) {
    long i;
    printf ("  JG Dump.  Type %s\n", jg->num_ecs > 0 ? "EC":"Prefs_array");
    printf ("    num_prefs_fulfilled_ret %ld\n", jg->num_prefs_fulfilled_ret);
    printf ("    num_prefs_possible_ret %ld\n",  jg->num_prefs_possible_ret);
    printf ("    num_prefs_fulfilled_imp %ld\n", jg->num_prefs_fulfilled_imp);
    printf ("    num_prefs_possible_imp %ld\n",  jg->num_prefs_possible_imp);
    printf ("    num_prefs_possible_notoccur %ld\n", jg->num_prefs_possible_notoccur);
    printf ("    num_nonrel %ld\n",  jg->num_nonrel);
    printf ("    num_nonrel_ret %ld\n",  jg->num_nonrel_ret);
    printf ("    num_rel %ld\n",  jg->num_rel);
    printf ("    num_rel_ret %ld\n", jg->num_rel_ret);
    if (0 == jg->num_ecs && jg->rel_array) {
	debug_print_prefs_array (&jg->prefs_array);
	printf ("    Rel_array Dump. %ld values",
		jg->prefs_array.num_judged);
	for (i = 0; i < jg->prefs_array.num_judged; i++) {
	    if (0 == (i % 10))
		printf ("\n      ");
	    printf ("%4.2f ", jg->rel_array[i]);
	}
	putchar ('\n');
    }
    else if (0 == jg->num_ecs)
	printf ("    JG is not initialized (0 ECs and no rel_array\n");
    else {
	printf ("    Dump of %ld ECs within JG\n", jg->num_ecs);
	for (i = 0; i < jg->num_ecs; i++)
	    debug_print_ec (&jg->ecs[i]);
    }
    fflush (stdout);
}

static void
debug_print_results_prefs (RESULTS_PREFS *rp) {
    long i;
    printf ("Results_prefs Dump.  %ld Judgment Groups\n", rp->num_jgs);
    printf ("  num_judged_ret %ld,  num_judged %ld\n",
	    rp->num_judged_ret, rp->num_judged);
    for (i = 0; i < rp->num_jgs; i++)
	debug_print_jg (&rp->jgs[i]);
    debug_print_counts_array (&rp->pref_counts);
}

int 
te_form_pref_counts_cleanup ()
{
    if (max_current_query > 0) {
	Free (current_query);
	max_current_query = 0;
	current_query = "no_query";
    }
    if (max_num_jgs > 0) {
	Free (jgs);
	max_num_jgs = 0;
    }
    if (max_rank_pool > 0) {
	Free (rank_pool);
	max_rank_pool = 0;
    }
    if (max_ec_pool > 0) {
	Free (ec_pool);
	max_ec_pool = 0;
    }
    if (max_ca_pool > 0) {
	Free (ca_pool);
	max_ca_pool = 0;
    }
    if (max_ca_ptr_pool > 0) {
	Free (ca_ptr_pool);
	max_ca_ptr_pool = 0;
    }
    if (max_pa_pool > 0) {
	Free (pa_pool);
	max_pa_pool = 0;
    }
    if (max_pa_ptr_pool > 0) {
	Free (pa_ptr_pool);
	max_pa_ptr_pool = 0;
    }
    if (max_rel_pool > 0) {
	Free (rel_pool);
	max_rel_pool = 0;
    }
    if (max_prefs_and_ranks > 0) {
	Free (prefs_and_ranks);
	max_prefs_and_ranks = 0;
    }
    if (max_docno_results > 0) {
	Free (docno_results);
	max_docno_results = 0;
    }
    if (max_temp_pa_pool > 0) {
	Free (temp_pa_pool);
	max_temp_pa_pool = 0;
    }
    if (max_temp_pa_ptr_pool > 0) {
	Free (temp_pa_ptr_pool);
	max_temp_pa_ptr_pool = 0;
    }
    return (1);
}
