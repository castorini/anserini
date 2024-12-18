/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/
static char *VersionID = VERSIONID;

static char *help_message = 
"trec_eval [-h] [-q] [-m measure[.params] [-c] [-n] [-l <num>]\n\
   [-D debug_level] [-N <num>] [-M <num>] [-R rel_format] [-T results_format]\n\
   rel_info_file  results_file \n\
 \n\
Calculate and print various evaluation measures, evaluating the results  \n\
in results_file against the relevance info in rel_info_file. \n\
 \n\
There are a fair number of options, of which only the lower case options are \n\
normally ever used.   \n\
 --help:\n\
 -h: Print full help message and exit. Full help message will include\n\
     descriptions for any measures designated by a '-m' parameter, and\n\
     input file format descriptions for any rel_info_format given by '-R'\n\
     and any top results_format given by '-T.'\n\
     Thus to see all info about preference measures use\n\
          trec_eval -h -m all_prefs -R prefs -T trec_results \n\
 --version:\n\
 -v: Print version of trec_eval and exit.\n\
 --query_eval_wanted:\n\
 -q: In addition to summary evaluation, give evaluation for each query/topic\n\
 --measure measure_name[.measure_params]:\n\
 -m measure: Add 'measure' to the lists of measures to calculate and print.\n\
    If 'measure' contains a '.', then the name of the measure is everything\n\
    preceeding the period, and everything to the right of the period is\n\
    assumed to be a list of parameters for the measure, separated by ','. \n\
    There can be multiple occurrences of the -m flag.\n\
    'measure' can also be a nickname for a set of measures. Current \n\
    nicknames include \n\
       'official': the main measures often used by TREC\n\
       'all_trec': all measures calculated with the standard TREC\n\
                   results and rel_info format files.\n\
       'set': subset of all_trec that calculates unranked values.\n\
       'prefs': Measures not in all_trec that calculate preference measures.\n\
 --complete_rel_info_wanted:\n\
 -c: Average over the complete set of queries in the relevance judgements  \n\
     instead of the queries in the intersection of relevance judgements \n\
     and results.  Missing queries will contribute a value of 0 to all \n\
     evaluation measures (which may or may not be reasonable for a  \n\
     particular evaluation measure, but is reasonable for standard TREC \n\
     measures.) Default is off.\n\
 --level_for_rel num:\n\
 -l<num>: Num indicates the minimum relevance judgement value needed for \n\
      a document to be called relevant. Used if rel_info_file contains \n\
      relevance judged on a multi-relevance scale.  Default is 1. \n\
 --nosummary:\n\
 -n: No summary evaluation will be printed\n\
 --Debug_level num:\n\
 -D <num>: Debug level.  1 and 2 used for measures, 3 and 4 for merging\n\
     rel_info and results, 5 and 6 for input.  Currently, num can be of the\n\
     form <num>.<qid> and only qid will be evaluated with debug info printed.\n\
     Default is 0.\n\
 --Number_docs_in_coll num:\n\
 -N <num>: Number of docs in collection Default is MAX_LONG \n\
 -Max_retrieved_per_topic num:\n\
 -M <num>: Max number of docs per topic to use in evaluation (discard rest). \n\
      Default is MAX_LONG.\n\
 --Judged_docs_only:\n\
 -J: Calculate all values only over the judged (either relevant or  \n\
     nonrelevant) documents.  All unjudged documents are removed from the \n\
     retrieved set before any calculations (possibly leaving an empty set). \n\
     DO NOT USE, unless you really know what you're doing - very easy to get \n\
     reasonable looking numbers in a file that you will later forget were \n\
     calculated  with the -J flag.  \n\
 --Rel_info_format format:\n\
 -R format: The rel_info file is assumed to be in format 'format'.  Current\n\
    values for 'format' include 'qrels', 'prefs', 'qrels_prefs'.  Note not\n\
    all measures can be calculated with all formats.\n\
 --Results_format format:\n\
 -T format: the top results_file is assumed to be in format 'format'. Current\n\
    values for 'format' include 'trec_results'. Note not all measures can be\n\
    calculated with all formats.\n\
 --Zscore Zmean_file:\n\
 -Z Zmean_file: Instead of printing the raw score for each measure, print\n\
    a Z score instead. The score printed will be the deviation from the mean\n\
    of the raw score, expressed in standard deviations, where the mean and\n\
    standard deviation for each measure and query are found in Zmean_file.\n\
    If mean is not in Zmeanfile for a measure and query, -1000000 is printed.\n\
    Zmean_file format is ascii lines of form \n\
       qid  measure_name  mean  std_dev\n\
 \n\
 \n\
Standard evaluation procedure:\n\
For each of the standard TREC measures requested, a ranked list of\n\
of relevance judgements is created corresponding to each ranked retrieved doc,\n\
A rel judgement is set to -1 if the document was not in the pool (not in \n\
rel_info_file) or -2 if the document was in the pool but unjudged (some \n\
measures (infAP) allow the pool to be sampled instead of judged fully).  \n\
Otherwise it is set to the value in rel_info_file. \n\
Most measures, but not all, will treat -1 or -2 the same as 0, \n\
namely nonrelevant.  Note that relevance_level is used to \n\
determine if the document is relevant during score calculations. \n\
Queries for which there is no relevance information are ignored. \n\
Warning: queries for which there are relevant docs but no retrieved docs \n\
are also ignored by default.  This allows systems to evaluate over subsets  \n\
of the relevant docs, but means if a system improperly retrieves no docs,  \n\
it will not be detected.  Use the -c flag to avoid this behavior. \n\
";


#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "functions.h"

#ifdef MDEBUG
#include "mcheck.h"
#endif /* MDEBUG */


static char *usage = "Usage: trec_eval [-h] [-q] {-m measure}* trec_rel_file trec_top_file\n\
   -h: Give full help information, including other options\n\
   -q: In addition to summary evaluation, give evaluation for each query\n\
   -m: calculate and print measures indicated by 'measure'\n\
       ('-m all_qrels' prints all qrels measures, '-m official' is default)\n";

extern int te_num_trec_measures;
extern TREC_MEAS *te_trec_measures[];
extern int te_num_trec_measure_nicknames;
extern TREC_MEASURE_NICKNAMES te_trec_measure_nicknames[];
extern int te_num_rel_info_format;
extern REL_INFO_FILE_FORMAT te_rel_info_format[];
extern int te_num_results_format;
extern RESULTS_FILE_FORMAT te_results_format[];
extern int te_num_form_inter_procs;
extern RESULTS_FILE_FORMAT te_form_inter_procs[];

static int mark_measure (EPI *epi, char *optarg);
static int trec_eval_help(EPI *epi);
static void get_debug_level_query (EPI *epi, char *optarg);
static int cleanup (EPI *epi);


int
main (argc, argv)
int argc;
char *argv[];
{
    char *trec_results_file;
    ALL_RESULTS all_results;
    char *trec_rel_info_file;
    ALL_REL_INFO all_rel_info;
    char *zscores_file= NULL;
    ALL_ZSCORES all_zscores;

    EPI epi;              /* Eval parameter info */
    TREC_EVAL accum_eval;
    TREC_EVAL q_eval;
    long i,j,m;
    int c;
    long help_wanted = 0;
    long measure_marked_flag = 0;

#ifdef MDEBUG
    /* Turn on memory debugging if environment variable MALLOC_TRACE is
       defined as an output file for reports.
       mcheck_check_all() will dynamically check */
    mcheck(NULL);
    mtrace();
#endif /* MDEBUG */

    /* Initialize static info before getting program optional args */
    epi.query_flag = 0;
    epi.average_complete_flag = 0;
    epi.judged_docs_only_flag = 0;
    epi.summary_flag = 1;
    epi.relation_flag = 1;
    epi.debug_level = 0;
    epi.debug_query = NULL;
    epi.num_docs_in_coll = 0;
    epi.relevance_level = 1;
    epi.max_num_docs_per_topic = MAXLONG;
    epi.rel_info_format = "qrels";
    epi.results_format = "trec_results";
    epi.zscore_flag = 0;
    if (NULL == (epi.meas_arg = Malloc (argc+1, MEAS_ARG)))
	exit (1);
    epi.meas_arg[0].measure_name = NULL;
    
    /* Get command line options */
    while (1) {
	int option_index = 0;
	static struct option long_options[] = {
	    {"help", 0, 0, 'h'},
	    {"version", 0, 0, 'v'},
	    {"query_eval_wanted", 0, 0, 'q'},
	    {"measure", 1, 0, 'm'},
	    {"complete_rel_info_wanted", 0, 0, 'c'},
	    {"level_for_rel", 1, 0, 'l'},
	    {"nosummary", 0,0,'n'},
	    {"Debug_level", 1, 0, 'D'},
	    {"Judged_docs_only", 0, 0, 'J'},
	    {"Number_docs_in_coll", 1, 0, 'N'},
	    {"Max_retrieved_per_topic", 1, 0, 'M'},
	    {"Rel_info_format", 1, 0, 'R'},
	    {"Results_format", 1, 0, 'T'},
	    {"Output_old_results_format", 1, 0, 'o'},
	    {"Zscore", 1, 0, 'Z'},
	    {0, 0, 0, 0},
	};
	c = getopt_long (argc, argv, "hvqm:cl:nD:JN:M:R:T:oZ:", 
			 long_options, &option_index);
	if (c == -1)
	    break;
	switch (c) {
	case 'h':
	    help_wanted++;
	    break;
	case 'v':
	    fprintf (stderr, "trec_eval version %s\n", VersionID);
	    exit (0);
	case 'q':
	    epi.query_flag++;
	    break;
	case 'm':
	    /* Mark measure(s) indicated by optarg to be done */
	    if (UNDEF == mark_measure (&epi, optarg)) {
		fprintf (stderr, "trec_eval: illegal measure '%s'\n", optarg);
		exit (1);
	    }
	    measure_marked_flag++;
	    break;
	case 'c':		
	    epi.average_complete_flag++;
	    break;
	case 'l':
	    epi.relevance_level = atol (optarg);
	    break;
	case 'n':
	    epi.summary_flag = 0;
	    break;
	case 'D':
	    get_debug_level_query (&epi, optarg);
	    break;
	case 'J':
	    epi.judged_docs_only_flag++;
	    break;
	case 'N':
            epi.num_docs_in_coll = atol (optarg);
	    break;
	case 'M':
            epi.max_num_docs_per_topic = atol (optarg);
	    break;
	case 'R':
            epi.rel_info_format = optarg;
	    break;
	case 'T':
            epi.results_format = optarg;
	    break;
	case 'o':
	    /* Obsolete, no longer supported */
	    epi.relation_flag = 0;
	    break;
	case 'Z':
	    epi.zscore_flag++;
	    zscores_file = optarg;
	    break;
	case '?':
	default:
		(void) fputs (usage,stderr);
		exit (1);
	}
    }

    if (help_wanted) {
	if (UNDEF == trec_eval_help(&epi))
	    return (UNDEF);
	exit (0);
    }

    if (optind + 2 != argc ) {
        (void) fputs (usage,stderr);
        exit (1);
    }

    trec_rel_info_file = argv[optind++];
    trec_results_file = argv[optind++];

    /* Find and get qrels and ranked results information for all queries from
       the input text files */
    for (i = 0; i < te_num_rel_info_format; i++) {
	if (0 == strcmp (epi.rel_info_format, te_rel_info_format[i].name)) {
	    if (UNDEF == te_rel_info_format[i].get_file (&epi,
							 trec_rel_info_file,
							 &all_rel_info)) {
		fprintf (stderr, "trec_eval: Quit in file '%s'\n",
			 trec_rel_info_file);
		exit (2);
	    }
	    break;
	}
    }
    if (i >= te_num_rel_info_format) {
	fprintf (stderr, "trec_eval: Illegal rel_format '%s'\n",
		 epi.rel_info_format);
	exit (2);
    }
    for (i = 0; i < te_num_results_format; i++) {
	if (0 == strcmp (epi.results_format, te_results_format[i].name)) {
	    if (UNDEF == te_results_format[i].get_file (&epi,
							trec_results_file,
							&all_results)) {
		fprintf (stderr, "trec_eval: Quit in file '%s'\n",
			 trec_results_file);
		exit (2);
	    }
	    break;
	}
    }
    if (i >= te_num_results_format) {
	fprintf (stderr, "trec_eval: Illegal retrieval results format '%s'\n",
		 epi.results_format);
	exit (2);
    }

    if (epi.zscore_flag) {
	if (UNDEF == te_get_zscores (&epi, zscores_file, &all_zscores))
	    return (UNDEF);
    }

    /* Initialize all marked measures (possibly using command line info) */
    if (0 == measure_marked_flag) {
	/* If no measures designated on command line, first mark "official" */
	if (UNDEF == mark_measure (&epi, "official")) {
	    fprintf (stderr, "trec_eval: illegal measure 'official'\n");
	    exit (1);
	}
    }
    accum_eval = (TREC_EVAL) {"all",  0, NULL, 0, 0};
    for (m = 0; m < te_num_trec_measures; m++) {
	if (MEASURE_MARKED(te_trec_measures[m])) {
	    if (UNDEF == te_trec_measures[m]->init_meas (&epi,
							te_trec_measures[m],
							&accum_eval)) {
		fprintf (stderr, "trec_eval: Cannot initialize measure '%s'\n",
			 te_trec_measures[m]->name);
		exit (2);
	    }
	}
    }

    /* Reserve space and initialize q_eval to be copy of accum_eval */
    if (NULL == (q_eval.values = Malloc (accum_eval.num_values,
					 TREC_EVAL_VALUE)))
	exit (3);
    (void) memcpy (q_eval.values, accum_eval.values,
		   accum_eval.num_values * sizeof (TREC_EVAL_VALUE));
    q_eval.num_values = accum_eval.num_values;
    q_eval.num_queries  = 0;

    /* For each topic which has both qrels and top results information,
       calculate, possibly print (if query_flag), and accumulate
       evaluation measures. */
    for (i = 0; i < all_results.num_q_results; i++) {
	/* If debugging a particular query, then skip all others */
	if (epi.debug_query &&
	    strcmp (epi.debug_query, all_results.results[i].qid))
	    continue;
	/* Find rel info for this query (skip if no rel info) */
	for (j = 0; j < all_rel_info.num_q_rels; j++) {
	    if (0 == strcmp (all_results.results[i].qid,
			     all_rel_info.rel_info[j].qid))
		break;
	}
	if (j >= all_rel_info.num_q_rels)
	    continue;

	/* zero out all measures for new query */
	for (m = 0; m < q_eval.num_values; m++)
	    q_eval.values[m].value = 0;
	q_eval.qid = all_results.results[i].qid;

	/* Calculate all measure scores */
	for (m = 0; m < te_num_trec_measures; m++) {
	    if (MEASURE_REQUESTED(te_trec_measures[m])) {
		if (UNDEF == te_trec_measures[m]->calc_meas (&epi,
						    &all_rel_info.rel_info[j],
						    &all_results.results[i],
						    te_trec_measures[m],
						    &q_eval)) {
		    fprintf (stderr,"trec_eval: Can't calculate measure '%s'\n",
			     te_trec_measures[m]->name);
		    exit (4);
		}
	    }
	}

	/* Convert values to zscores if requested */
	if (epi.zscore_flag) {
	    if (UNDEF == te_convert_to_zscore (&all_zscores, &q_eval))
		return (UNDEF);
	}

	/* Add this topics value to accumulated values, and possibly print */
	for (m = 0; m < te_num_trec_measures; m++) {
	    if (MEASURE_REQUESTED(te_trec_measures[m])) {
		if (UNDEF == te_trec_measures[m]->acc_meas (&epi,
						   te_trec_measures[m],
						   &q_eval,
						   &accum_eval)) {
		    fprintf(stderr,"trec_eval: Can't accumulate measure '%s'\n",
			    te_trec_measures[m]->name);
		    exit (5);
		}
		if (epi.query_flag &&
		    UNDEF == te_trec_measures[m]->print_single_meas (&epi,
						   te_trec_measures[m],
						   &q_eval)) {
		    fprintf(stderr,
			    "trec_eval: Can't print query measure '%s'\n",
			    te_trec_measures[m]->name);
		    exit (6);
		}
	    }
	}
	accum_eval.num_queries++;
    }

    if (accum_eval.num_queries == 0) {
	fprintf (stderr,
		"trec_eval: No queries with both results and relevance info\n");
	exit (7);
    }

    /* Calculate final averages, and print (if desired) */
    /* Note that averages may depend on the entire rel_info data if
       epi.average_complete_flag is set */
    for (m = 0; m < te_num_trec_measures; m++) {
	if (MEASURE_REQUESTED(te_trec_measures[m])) {
	    if (UNDEF == te_trec_measures[m]->calc_avg_meas
		    (&epi, te_trec_measures[m],
		     &all_rel_info, &accum_eval) ||
		UNDEF == te_trec_measures[m]->print_final_and_cleanup_meas 
		(&epi, te_trec_measures[m],  &accum_eval)) {
		    fprintf (stderr,"trec_eval: Can't print measure '%s'\n",
			     te_trec_measures[m]->name);
		    exit (8);
		}
	}
    }

    if (UNDEF == cleanup (&epi)) {
	fprintf (stderr,"trec_eval: cleanup failed\n");
	exit (10);
    }
    Free (q_eval.values);
    Free (accum_eval.values);
    Free (epi.meas_arg);

    exit (0);
}

static int 
add_meas_arg_info (EPI *epi, char *meas, char *param)
{
    long i;

    /* Guaranteed space since malloc'd argc+1 entries and can't be more
       than one entry per command line argument */

    /* Find non-NULL entry */
    i = 0;
    while (epi->meas_arg[i].measure_name) i++;
    
    epi->meas_arg[i].measure_name = meas;
    epi->meas_arg[i].parameters = param;

    /* Ensure measure_name exists, has non_NULL parameter and mark it 
       to be calculated */
    if (*param == '\0') {
	fprintf (stderr, "trec_eval: improper measure in parameter '%s'\n",
		 epi->meas_arg[i].measure_name);
	return (UNDEF);
    }

    epi->meas_arg[i+1].measure_name = NULL;
    return (1);
}

static int
mark_single_measure (char *optarg)
{
    long i;

    for (i = 0; i < te_num_trec_measures; i++) {
	if (0 == strcmp (optarg, te_trec_measures[i]->name)) {
	    te_trec_measures[i]->eval_index = -2;
	    break;
	}
    }
    if (i >= te_num_trec_measures)
	return (UNDEF);
    return (1);
}

static int
mark_measure (EPI *epi, char *optarg)
{
    long i;
    char *ptr;
    
    ptr = optarg;
    while (*ptr && *ptr != '.') ptr++;
    if (*ptr == '.') {
	*ptr++ = '\0';
	if (UNDEF == add_meas_arg_info (epi, optarg, ptr))
	    return (UNDEF);
    }

    for (i = 0; i < te_num_trec_measure_nicknames; i++) {
	if (0 == strcmp (optarg, te_trec_measure_nicknames[i].name)) {
	    /* Have found nickname.  Mark all real names it refers to */
	    char **name = te_trec_measure_nicknames[i].name_list;
	    while (*name) {
		if (UNDEF == mark_single_measure (*name))
		    return (UNDEF);
		name++;
	    }
	    return (1);
	}
    }

    /* optarg did not match any nickname, mark measure directly */
    return (mark_single_measure (optarg));
}

static int
trec_eval_help(EPI *epi)
{
    long m, f;
    long m_marked = 0;

    printf ("%s\n-----------------------\n", help_message);

    for (f = 0; f < te_num_results_format; f++) {
	if (0 == strcmp (te_results_format[f].name, epi->results_format))
	    break;
    }
    if (f < te_num_results_format)
	printf ("%s\n-----------------------\n",

		te_results_format[f].explanation);

    for (f = 0; f < te_num_rel_info_format; f++) {
	if (0 == strcmp (te_rel_info_format[f].name, epi->rel_info_format))
	    break;
    }
    if (f < te_num_rel_info_format)
	printf ("%s\n-----------------------\n",
		te_rel_info_format[f].explanation);

    printf ("Individual measure documentation for requested measures\n");

    for (m = 0; m < te_num_trec_measures; m++) {
	if (MEASURE_MARKED(te_trec_measures[m])) {
	    m_marked = 1;
	    printf ("%s\n%s",
		    te_trec_measures[m]->name,
		    te_trec_measures[m]->explanation);
	}
    }

    if (! m_marked)
	printf ("-- No measures indicated.\n   Request measure documentation using <-m measure> on command line\n");

    return (1);
}

static void
get_debug_level_query ( EPI *epi, char *optarg)
{
    char *ptr;

    for (ptr = optarg; *ptr && *ptr != '.'; ptr++) 
	;
    if (*ptr) {
	*ptr++ = '\0';
	epi->debug_query = ptr;
    }
    epi->debug_level = atol (optarg);
}

static int
cleanup (EPI *epi)
{
    long i;

    for (i = 0; i < te_num_rel_info_format; i++) {
	if (0 == strcmp (epi->rel_info_format, te_rel_info_format[i].name)) {
	    if (UNDEF == te_rel_info_format[i].cleanup())
		return (UNDEF);
	    break;
	}
    }
    for (i = 0; i < te_num_results_format; i++) {
	if (0 == strcmp (epi->results_format, te_results_format[i].name)) {
	    if (UNDEF == te_results_format[i].cleanup ())
		return (UNDEF);
	    break;
	}
    }
    for (i = 0; i < te_num_form_inter_procs; i++) {
	if (UNDEF == te_form_inter_procs[i].cleanup ())
	    return (UNDEF);
    }
    if (epi->zscore_flag) {
	if (UNDEF == te_get_zscores_cleanup())
	    return (UNDEF);
    }
    return (1);
}
