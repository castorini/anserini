trec_eval is the standard tool used by the TREC community for
evaluating an ad hoc retrieval run, given the results file and a
standard set of judged results.  

------------------------------------------------------------------------------
Installation: Should be as easy as typing "make" in the source directory.
If you wish the trec_eval binary to be placed in a standard location, alter
the first line of Makefile appropriately.

------------------------------------------------------------------------------
Testing: sample input and output files are included in the directory test.
"make quicktest" will perform some sample simple evaluations and compare
the results.

------------------------------------------------------------------------------
Usage:  Most options can be ignored.  The only one most folks will need
is the "-q" flag, to indicate whether to output official results for individual 
queries as well as the averages over all queries.  Official TREC usage
might be something like 
	trec_eval -q -c -M1000 official_qrels submitted_results 
to ensure correct evaluation if submitted_results doesn't have results
for all queries, or returns more than 1000 documents per query.
If you wish to output only one particular measure:
        trec_eval -m measure[.params] rel_info_file results_file


------------------------------------------------------------------------------
Change Log  (only recent)
------------------------------------------------------------------------------
12/31/08
       comments and documentation of Zscore file format corrected.
       trec_eval.c get_zscores.c
2/25/08 Version 9.0alpha.  
       Complete rewrite of entire trec_eval (needed for a long time!).  
       Complete separation of individual measure calculations -
       computers are now fast enough so can afford recalculation of lots
       of intermediate values.  
       Should be much easier to add measures to, and much easier to add
       new input file formats with associated measures.
       Parameters for measures (eg, cutoffs for P) can be specified on the
       command line.  
       Choice of measures can be specifed on the command line.
       An initial set of preference evaluation measures (with their own
       input rel_info format) have been added.
       Help now gives targeted measure and format descriptions.  Try
          trec_eval -h -m all_prefs -R prefs
       to get info on preference measures and formats, for instance.
       All internal calculations are in double rather than float. Yields
       minor variations in output at rare times; mostly when going from a
       double percentage to a corresponding doc cutoff (eg, in iprec_at_recall).
       All globally known procedure names or variables now begin with 'te_'
       to allow incorporation of procedures in other programs.
       
       Measures added:
       	        ndcg, ndcg_cut, set_F, success, map_avgjg, P_avgjg,
		various preference evaluation measures.
       Measures renamed:
                Rprec-mult_*            was *R-prec
                set_P			was exact_prec
	        set_recall 		was exact_recall
	        set_relative_P		was exact_relative_prec
	        set_recall		was exact_recall
	        set_map			was exact_unranked_avg_prec
		gm_map                  was gm_ap
		11pt_avg                was 11-pt_avg
		P_*                     was P*
                recall_*                was recall*
		relative_P_*            was relative_P*
		iprec_at_recall_*       was ircl_prn.*
       Measures dropped for now:
       		3-pt_avg       	
		avg_doc_prec   	
		avg_relative_prec	
		exact_relative_unranked_avg_prec	
		map_at_R       	
                int_map        	
                exact_int_R_rcl_prec
                int_map_at_R   	
                unranked_avg_prec*
                relative_unranked_avg_prec*
                rcl_at_142_nonrel	
                fallout_recall_*
                int_*R-prec
                micro_prec     	
                micro_recall   	
                micro_bpref    	
		bpref variants
		time base measures.
	Input formats added:
	        prefs - allows expression of preferences
		qrels_prefs - same as standard qrels, except treated as prefs
		qrels_jg - same as standard qrels, except allows judgment sets
                           from multiple users (judgment groups).

Version 8.1, Added infAP, minor bug fixes
7/24/06 Improved infAP comments (implementation verified by Yilmaz).
        trec_eval_help.c: allow longer measure explanations.
6/27/06 get_opt.c Fixed error message
6/22/06 Added measure infAP (Aslam et al) to allow judging only sample 
        of pools.  -1 for rel in qrels file interpreted as pool doc not judged.
6/22/06 trvec_teval.c: fixed bugs in calculation of bpref if multiple
	relevance levels were used and a non-default relevance level
	was given. (Eg. A doc with rel level of 2 was counted as unjudged
	rather than judged nonrel if a relevance level of 3 was needed
	to consider relevant.)
4/5/06  Changed comments in README, trec_eval.c, trec_eval_help.c files 
        which incorrectly claimed queries with no relevant docs are 
        ignored (this was true with very old versions of trec_eval).  Now
        reads that queries with no relevance information are ignored.
        Giorgio Di Nunzio and Nicola Ferro,
------------------------------------------------------------------------------
Version 8.0, full bpref bug fix, see file bpref_bug.  I decided to up the 
        version number since bpref results are incompatible with previous 
        results (though the changes are small).
------------------------------------------------------------------------------
------------------------------------------------------------------------------

Files:
Main procedure:
trec_eval.c
---
Procedures to read input files of various formats:
formats.c          Mapping names of input file formats to input procedures
get_qrels.c        Read the standard judged documents (qrels format)
get_qrels_jg.c     Read qrels format with multiple judgment groups per query
get_prefs.c        Read preferences judgments instead of doc judgments - see
		   formats.c for full description.
get_qrels_prefs.c  Read qrels_jg format file, interpret as prefs file.
get_trec_results.c Read the standard result file (trec_results format).
---
Procedures to merge rel_info and results from input form into form that measures
can easily use, if they wish:
form_res_rels.c    'qrels' and 'trec_results' into RES_RELS format.
form_res_rels_jg.c 'qrels_jg' and 'trec_results' into RES_RELS_JG format.
form_pref_counts.c ('prefs' or 'qrels_prefs') and 'trec_results' format
---
The actual measures:
measures.c   Associates measure name with parameters and
             init, calculation, accumulation, printing procedures
meas_*.c     Common procedures used by many measures for init, acc, printing.
m_<measure_name>.c  measure specific procedures
---
Miscellaneous:
Makefile     Compile and test trec_eval
README       This file
CHANGELOG    Recent changes
test         Directory of collection of sample input and output for trec_eval
trec_eval.h  Basic evaluation structures.
functions.h  Prototype decorations of measure procedures.
sysfunc.h
common.h
bpref_bug:   Description of bug in bpref that existed in trec_eval versions 6
             through 7.3.

------------------------------------------------------------------------------
------------------------------------------------------------------------------
Adding a new measure.

Assuming it uses standard input files:
1. In m_<new_measure>.c, write any needed measure specific procedures needed to
      initialize measure
      calculate measure
      accumulate measure (adding one topic's value to summary value over topics)
      calculate the ending average for a measure.
      print single
      print final query measure value (and cleanup if needed)
   Most measures require only a new calculate measure procedure - the
   other procedures are generic and already implemented depending on the type of
   measure (has cutoffs and value for each cutoff, parameters, is a float, etc).
   See functions.h to see fit for these generic procedures.
2. In same file, construct TREC_MEAS te_meas* entry pointing to above 
   procedures and any default cutoffs or parameters.
3. Add pointer to that TREC_MEAS entry in "measures.c"
4. Add measure source file to Makefile

------------------------------------------------------------------------------
Adding a new file format
1. Implement reading of new format in get_<new_format>.c, with returned top
   level output of type ALL_REL_INFO or ALL_RESULTS.  The individual topic
   returned values will be in a format dependent form which will be passed
   to the appropriate measures.
2. Add format to appropriate format list in formats.c
3. Add measures to take advantage of format (see above)
4. To use, invoke trec_eval with -R or -T values, and -m measures that
   are appropriate.
