/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/
#ifndef TRECFORMATH
#define TRECFORMATH

/* ---------------------------------------------------------------------- */
/* Format specific definitions for file input for results and rel_info */
/* trec_results - pointed to by results->q_results */
typedef struct {                    /* For each retrieved document result */
    char *docno;                       /* document id */
    float sim;                         /* score */
} TEXT_RESULTS;

typedef struct {                    /* For each query in retrieved results */
    long num_text_results;           /* number results for query*/
    long max_num_text_results;       /* number results space reserved for */
    TEXT_RESULTS *text_results;     /* Array of TEXT_RESULTS results */
} TEXT_RESULTS_INFO;

/* qrels pointed to by rel_info->q_rel_info */
typedef struct {                    /* For each relevance judgement */
    char *docno;                       /* document id */
    long rel;                          /* document judgement */
} TEXT_QRELS;

typedef struct {                    /* For each query in rel judgments */
    long num_text_qrels;               /* number of judged documents */
    long max_num_text_qrels;           /* Num docs space reserved for 
					  Private, unused */
    TEXT_QRELS *text_qrels;            /* Array of judged TEXT_QRELS.
					  Kept sorted by docno */
} TEXT_QRELS_INFO;

typedef struct {                    /* For each jg in query */
    long num_text_qrels;               /* number of judged documents */
    TEXT_QRELS *text_qrels;            /* Array of judged TEXT_QRELS.
					  Kept sorted by docno */
} TEXT_QRELS_JG;

typedef struct {                    /* For each query in rel judgments */
    long num_text_qrels_jg;            /* number of judgment groups */
    TEXT_QRELS_JG *text_qrels_jg;      /* Array of judged TEXT_QRELS_JG */
} TEXT_QRELS_JG_INFO;

/* prefs pointed to by rel_info->q_rel_info */
typedef struct {                 /* For each line in rel prefs judgments */
    char *jg;                       /* Judgment group id */          
    char *jsg;                      /* Judgment subgroup id */          
    float rel_level;                /* Relevance level of this docno */
    char *docno;                    /* docno */
} TEXT_PREFS;

typedef struct {                    /* For each query in rel judgements */
    long num_text_prefs;               /* number of preference lines */
    long max_num_text_prefs;           /* Num pref lines space reserved for */
    TEXT_PREFS *text_prefs;            /* Array of judged TEXT_PREFS */
} TEXT_PREFS_INFO;


/* ----------------------------------------------------------------------*/
/* Intermediate form of merged results plus rel_info that can be more 
   directly used by several measures */

/* Original trec_results and qrels  */
typedef struct {
    /* Counts among retrieved docs (possibly different after -M)  */
    long num_rel_ret;      /* Relevant retrieved docs - 
			      Number of retrieved docs in results_rel_list
			      with value >= epi->relevance_level */
    long num_ret;          /* Retrieved docs -
			      Number of docs in results_rel_list */
    long num_nonpool;      /* Number of docs in results_rel_list not in pool -
			      Number with value = RELVALUE_NONPOOL */
    long num_unjudged_in_pool; /* Number of docs in results_rel_list in pool
			      but not judged  -
			      Number with value = RELVALUE_UNJUDGED */

    /* Counts among total relevance judgments (independent of retrieval) */
    long num_rel;
    long num_rel_levels;   /* Number of judged rel_levels */
    long *rel_levels;      /* Number of docs in each judged rel_level 
			      (0 through num_rel_levels-1), whether
			      ranked or not */

    long *results_rel_list; /* Ordered list of relevance judgements
			      Eg, results_rel_list[2] gives relevance of the
			      third retrieved doc in rank order.
			      length of list is rank_rel->num_ret */
} RES_RELS;

/* If Judgments group info is included (qrels_jg), then return multiple jgs */
typedef struct {
    char *qid;
    long num_jgs;
    RES_RELS *jgs;
} RES_RELS_JG;

/* Merged trec_results and prefs info */

/* Two different approaches for representing preferences: EC and PREF_ARRAY */

/* For every different equivalence class (an ec is defined by rel_level) within
   a JSG, keep track of the ranks of retrieved docs from that class.
   Order list of docnos in prefs by increasing rank value in the retrieved 
   docs,if retrieved, and then by docno if not retrieved. 
   Docid_rank for a docno is the index of docno within that ordered list */
typedef struct {
    float rel_level;                /* rel_level of this EC in this JG */
    long num_in_ec;                 /* number in rel_info file in this ec
				       (indpendent of results) */
    long *docid_ranks;             /* List of docids in ec.
				       After construction, sorted by increasing
				       docid_rank numbers. */
} EC;

/* Preference array.  A square array, num_judgments * num_judgments, where
   pref_array[i][j] is 1 iff doc with docid_rank i is preferred to doc with 
   docid_rank j.
   could use bitstring to save more space, but at present not worth the
   added complexity.
   Given preference array PA there are five areas of importance, divided
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
  NJR  _________________|___
       .................|...
       ..........A4.....|.A5
       .................|...
 
    Area A1 is preferences fulfilled, both retrieved.
    Area A2 is preferences not fulfilled, both retrieved.
    Area A3 is preference implied fulfilled (i retrieved, j not)
    Area A4 is preference implied not fulfilled (i not retrieved, j retrieved)
    Area A5 is both i and j not retrieved.
*/
typedef struct {
    unsigned char *full_array;
    unsigned char **array;
    unsigned long num_judged;
} PREFS_ARRAY;
/* Counts array.  A summary square array, num_judgments * num_judgments, where
   counts_array[i][j] gives the number of JGs preferring i to j as above.
   Note number of JGs countaining the same docno must be bounded by
   MAX_SHORT */
typedef struct {
    unsigned short *full_array;
    unsigned short **array;
    unsigned long num_judged;
} COUNTS_ARRAY;

/* For every judgment group (JG) within a topic, give the preferences for
   the JG in one of two forms: either an ordered list of ECs, or a 
   preference array. See the head comment in form_prefs_counts.c for further
   details and implications of the representations */
typedef struct {
    EC *ecs;                /* Equivalence classes, ordered by decreasing
			       rel_level of class.  Thus JG.ecs[num_ecs-1]
			       is the only possible EC of nonrelevant docnos
			       (those with rel_level = 0.0). */
    long num_ecs;           /* num_ecs == 0 means prefs_array being used for
			       preference info rather than EC */

    PREFS_ARRAY prefs_array;/* prefs_array[i][j] is 1 iff doc with docid_rank i
			       is preferred to doc with docid_rank j.
			       Size num_judged * num_judged where
                               num_judged is the same for all JGs and is the
			       total number of distinct docnos involved
			       with preferences for this topic */
    float *rel_array;       /* Size num_judged. A rel_level value for each
			       doc in the judgements.  There may be multiple
                               different values for a given docno, only the
			       last encountered is used.  The restriction
			       that values for a docno must be either all
			       non-zero, or all zero, is enforced */

    /* Prefs between doc A and doc B, fulfilled by results and possible */
    long num_prefs_fulfilled_ret;    /* Num prefs in this jg satisfied by
					results where A and B both in results */
    long num_prefs_possible_ret;     /* Num possible prefs in this jg satisfied
					by results, A and B in results */
    long num_prefs_fulfilled_imp;    /* Implied num prefs in this jg satisfied
					by results where exactly one of A and B 
					in results */
    long num_prefs_possible_imp;     /* Implied num possible prefs in this jg 
					satisfied by results where exactly one
                                        of A and B in results */
    long num_prefs_possible_notoccur;/* Implied num possible prefs in this jg 
					not satisfied by results where neither
                                        A nor B is in results */

    long num_nonrel;                 /* Number of docs in judgments with
					rel_level of 0.0 in this JG */
    long num_nonrel_ret;             /* Number of docs retrieved with rel_level
					of 0.0 in this JG */
    long num_rel;                    /* Number of docs in judgments with
					rel_level greater than 0.0 in this JG */
    long num_rel_ret;                /* Number of docs retrieved with rel_level
					greater than 0.0 in this JG */
} JG;

/* RESULTS_PREFS contains two structures representing prefs - most measures
   will use one or the other.  
   The first gives a count by judgment group of prefs fulfilled and
   possible.  It includes the equivalences class info and rel_levels
   if measures need to go down that far.  This structure does not
   directly give information about conflicts or multiple representations
   of a preference (as when docno_i and docno_j both appear in multiple JGs).

   The second is an array of counts, size num_judged ** 2.
   pref_counts[i,j] gives the number of times docno_i is preferred to docno_j in
   judged preferences.  If i < j, then it is a preference fulfilled,
   if i > j, then a preference was not fulfilled.  This structure gives
   direct info for handling conflicts, but has no summary info for an
   individual JG.
*/
typedef struct {
    long num_jgs;
    JG *jgs;
    long num_judged;                    /* Num docs mentioned in trec_prefs */
    long num_judged_ret;                /* number of those docs retrieved */
    COUNTS_ARRAY pref_counts;           /* Array size num_judged**2  where
					   counts_array[i][j] gives num of
					   JG with doc i preferred to doc j
					   (i and j are internal ids, sorted
					   by retrieval rank then docno) */
} RESULTS_PREFS;


/* ----------------------------------------------------------------------*/
/* Procedure prototypes for going from input format REL_INFO and 
   input format RESULTS to an intermediate form appropriate to the two
   input formats that can be more directly used by several measures */

/* trec_results and qrels to RES_RELS */
int te_form_res_rels (const EPI *epi, const REL_INFO *rel_info,
                      const RESULTS *results, RES_RELS *res_rels);

/* trec_results and qrels to RES_RELS */
int te_form_res_rels_jg (const EPI *epi, const REL_INFO *rel_info,
			 const RESULTS *results, RES_RELS_JG *res_rels);

/* trec_results and prefs (or qrels_prefs) to RESULT_PREFS */
int form_prefs_counts (const EPI *epi, const REL_INFO *rel_info,
                       const RESULTS *results, RESULTS_PREFS *results_prefs);



#endif /* TRECFORMATH */
