/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/

#include "common.h"
#include "sysfunc.h"
#include "functions.h"
#include "trec_eval.h"
#include "trec_format.h"

int te_get_qrels (EPI *epi, char *text_qrels_file, ALL_REL_INFO *all_rel_info);
int te_get_qrels_jg (EPI *epi, char *text_qrels_file,
		     ALL_REL_INFO *all_rel_info);
int te_get_prefs (EPI *epi, char *text_prefs_file, ALL_REL_INFO *all_rel_info);
int te_get_qrels_prefs (EPI *epi, char *text_prefs_file,
			ALL_REL_INFO *all_rel_info);
int te_get_trec_results (EPI *epi, char *trec_results_file,
			 ALL_RESULTS *all_results);
int te_get_qrels_cleanup ();
int te_get_qrels_jg_cleanup ();
int te_get_prefs_cleanup ();
int te_get_qrels_prefs_cleanup ();
int te_get_trec_results_cleanup ();

REL_INFO_FILE_FORMAT te_rel_info_format[] = {
    {"qrels",
"Rel_info_file format: Standard 'qrels'\n\
Relevance for each docno to qid is determined from rel_info_file, which \n\
consists of text tuples of the form \n\
   qid  iter  docno  rel \n\
giving TREC document numbers (docno, a string) and their relevance (rel,  \n\
a non-negative integer less than 128, or -1 (unjudged)) \n\
to query qid (a string).  iter string field is ignored.   \n\
Fields are separated by whitespace, string fields can contain no whitespace. \n\
File may contain no NULL characters. \n\
",
     te_get_qrels, te_get_qrels_cleanup},


    {"qrels_jg",
"Rel_info_file format: Standard 'qrels'\n\
Relevance for each docno to qid is determined from rel_info_file, which \n\
consists of text tuples of the form \n\
   qid  ujg  docno  rel \n\
giving TREC document numbers (docno, a string) and their relevance (rel,  \n\
a non-negative integer less than 128, or -1 (unjudged)) \n\
to query qid (a string) for a particular user judgment group. \n\
This allows averaging (or other operations) of appropriate evaluation measures\n\
across multiple users, whoc may differ in their judgments. \n\
Fields are separated by whitespace, string fields can contain no whitespace. \n\
File may contain no NULL characters. \n\
",
     te_get_qrels_jg, te_get_qrels_jg_cleanup},


    {"prefs", 
"Rel_info_file format: Non-standard 'prefs'\n\
Preferences of user(s) for docs for a given qid is determined from\n\
text_prefs_file, which consists of text tuples of the form\n\
   qid  ujg  ujsubg  docno  rel_level\n\
giving TREC document numbers (docno, a string) and their relevance\n\
level (rel_level,a non-negative float) to query qid (a string) for a \n\
user judgment sub-group (ujsubg, a string) within a user judgment\n\
group (ujg, a string).\n\
Fields are separated by whitespace, string fields can contain no whitespace.\n\
File may contain no NULL characters.\n\
\n\
Preferences are indicated indirectly by comparing rel_level of\n\
different docnos within the same user judgment sub group(JSG).  A\n\
judgment sub group establishes preferences between all docnos with\n\
non-tied rel_levels within the group. Except possibly for 0.0, the\n\
actual values of rel_level are ignored by default; they only serve to\n\
establish a ranking within the JSG.\n\
\n\
If a user only expresses a preference between two docs, then that user JSG\n\
will have 2 lines in text_prefs_file:\n\
      qid1  ujg1  sub1 docno1  3.0\n\
      qid1  ujg1  sub1 docno2  2.0\n\
\n\
If a user completely ranks some small number N (5-10) of docs, then N lines \n\
are used.\n\
For example:\n\
      qid1  ujg1  sub1  docno1  3.0\n\
      qid1  ujg1  sub1  docno2  2.0\n\
      qid1  ujg1  sub1  docno3  0.0\n\
      qid1  ujg1  sub1  docno4  6.0\n\
      qid1  ujg1  sub1  docno5  0.0\n\
      qid1  ujg1  sub1  docno6  2.0\n\
establishes a total of 13 preferences (5 with docno4 preferred, 4 with docno1 \n\
preferred, 2 each with docno2 and docno6 preferred).\n\
\n\
If a given user has multiple preferences that aren't complete, the preferences\n\
are expressed in multiple JSGs within a single JG.\n\
For example:\n\
      qid1  ujg1  sub1  docno1  3.0\n\
      qid1  ujg1  sub1  docno2  2.0\n\
      qid1  ujg1  sub1  docno3  1.0\n\
      qid1  ujg1  sub2  docno1  2.0\n\
      qid1  ujg1  sub2  docno2  1.0\n\
      qid1  ujg1  sub2  docno4  3.0\n\
expressses 5 preferences (1>2, 1>3, 2 > 3, 4>1, 4>2).  Note the duplicate\n\
1 > 2 is not counted as a separate preference\n\
\n\
Multiple users are indicated by different JGs.\n\
For example:\n\
      qid1  ujg1  sub1  docno1  3.0\n\
      qid1  ujg1  sub1  docno2  2.0\n\
      qid1  ujg2  sub1  docno1  0.0\n\
      qid1  ujg2  sub1  docno3  6.0\n\
      qid1  ujg2  sub1  docno4  2.0\n\
      qid1  ujg2  sub2  docno1  0.0\n\
      qid1  ujg2  sub2  docno2  8.0\n\
expressses 5 preferences (1>2, 3>1, 4>1, 3>4, 2>1).\n\
\n\
A Judgment Group (JG) conceptually represents preferences for a single\n\
information need of a user at a single time.  Within a single JG, it\n\
is an error if there are inconsistencies (doc A > doc B in one JSG,\n\
but B > A or B == A in another).  The different JSGs within a JG are\n\
just a mechanism tha allows expressing partial ordering within a JG.\n\
Within a single JG, preferences are transistive:\n\
      qid1  ujg1  sub1  docno1  3.0\n\
      qid1  ujg1  sub1  docno2  2.0\n\
      qid1  ujg1  sub1  docno3  1.0\n\
      qid1  ujg1  sub2  docno2  5.0\n\
      qid1  ujg1  sub2  docno4  4.0\n\
expresses 5 preferences (1>2, 1>3, 2>3, 2>4, 1>4).  There is no\n\
preference expressed between 3 and 4.\n\
\n\
Different JGs may contain contradictory preferences, as in an earlier\n\
example.  These disagreements are realistic and desirable: users (or\n\
even the same user at different times) often do not agree with each\n\
other's preferences.  Individual preference evaluation measures will\n\
handle these contradictions (or confirmations) in different ways.\n\
\n\
A rel_level of 0.0 by convention means that doc is non-relevant to the\n\
topic (in that user's opinion).  it is an inconsistency (and an error)\n\
if a doc is assigned a rel_level of 0.0 in one JSG, but a different\n\
rel_level value in another JSG of the same JG.  Some preference\n\
evaluation measures may handle 0.0 differently.  Thus when converting\n\
a preference file in some other format into text_prefs format, do not\n\
assign a rel_level of 0.0 to a docno unless it is known that docno was\n\
considered nonrelevant.\n\
\n\
Handling of rel_level 0.0 separately addresses the general problem\n\
that the number of nonrelevant docs judged for a topic can be critical\n\
to fair evaluation - adding a couple of hundred preferences involving\n\
nonrelevant docs (out of the possibly millions or billions in a\n\
collection) can both change the importance of the topic when averaging\n\
and even change whether system A scores better than system B on a\n\
topic (even given identical retrieval on the added nonrel docs).  How\n\
to handle this correctly for preference evaluation will be an\n\
important future research problem.\n\
",
     te_get_prefs, te_get_prefs_cleanup},

{"qrels_prefs", 
"Rel_info_file format: Non-standard 'qrels_prefs'\n\
The file format is exactly the same as rel_info_file format 'qrels',\n\
however it is interpreted as a restricted 'prefs' rel_info_file.\n\
It cannot represent some user preferences (in particular, if a single user\n\
prefers Doc A to Doc B, and A to C, but does not express a preference\n\
between A and C) , but it allows the standard TREC qrels file to serve as \n\
input for preference evaluation measures.\n\
\n\
Read all relevance preference information from text_qrels_prefs_file.\n\
Preferences of user(s) for docs for a given qid is determined from\n\
text_prefs_file, which consists of text tuples of the form\n\
   qid  ujg   docno  rel_level\n\
giving TREC document numbers (docno, a string) and their relevance\n\
level (rel_level,a non-negative float) to query qid (a string) for a \n\
 user judgment group (ujg, a string).\n\
Fields are separated by whitespace, string fields can contain no whitespace.\n\
File may contain no NULL characters.\n\
\n\
Preferences are indicated indirectly by comparing rel_level of\n\
different docnos within the same user judgment group(JG).  A\n\
judgment group establishes preferences between all docnos with\n\
non-tied rel_levels within the group. Except possibly for 0.0, the\n\
actual values of rel_level are ignored by default; they only serve to\n\
establish a ranking within the JSG.\n\
\n\
If a user only expresses a preference between two docs, then that user JSG\n\
will have 2 lines in text_prefs_file:\n\
      qid1  ujg1   docno1  3.0\n\
      qid1  ujg1   docno2  2.0\n\
\n\
If a user completely ranks some small number N (5-10) of docs, then N lines \n\
are used.\n\
For example:\n\
      qid1  ujg1    docno1  3.0\n\
      qid1  ujg1    docno2  2.0\n\
      qid1  ujg1    docno3  0.0\n\
      qid1  ujg1    docno4  6.0\n\
      qid1  ujg1    docno5  0.0\n\
      qid1  ujg1    docno6  2.0\n\
establishes a total of 13 preferences (5 with docno4 preferred, 4 with docno1 \n\
preferred, 2 each with docno2 and docno6 preferred).\n\
\n\
A Judgment Group (JG) conceptually represents preferences for a single\n\
information need of a user at a single time.  Within a single JG, it\n\
is an error if there are inconsistencies (doc A > doc B in one JSG,\n\
but B > A or B == A in another).\n\
\n\
Different JGs may contain contradictory preferences, These\n\
disagreements are realistic and desirable: users (or even the same\n\
user at different times) often do not agree with each other's\n\
preferences.  Individual preference evaluation measures will handle\n\
these contradictions (or confirmations) in different ways.\n\
\n\
A rel_level of 0.0 by convention means that doc is non-relevant to the\n\
topic (in that user's opinion).  Some preference evaluation measures\n\
may handle 0.0 differently.  Thus when converting a preference file in\n\
some other format into text_prefs format, do not assign a rel_level of\n\
0.0 to a docno unless it is known that docno was considered\n\
nonrelevant.\n\
\n\
Handling of rel_level 0.0 separately addresses the general problem\n\
that the number of nonrelevant docs judged for a topic can be critical\n\
to fair evaluation - adding a couple of hundred preferences involving\n\
nonrelevant docs (out of the possibly millions or billions in a\n\
collection) can both change the importance of the topic when averaging\n\
and even change whether system A scores better than system B on a\n\
topic (even given identical retrieval on the added nonrel docs).  How\n\
to handle this correctly for preference evaluation will be an\n\
important future research problem.\n\
", 
 te_get_qrels_prefs, te_get_qrels_prefs_cleanup},
};
int te_num_rel_info_format =
    sizeof (te_rel_info_format)/sizeof (te_rel_info_format[0]);

RESULTS_FILE_FORMAT te_results_format[] = {
    {"trec_results", 
"Results_file format: Standard 'trec_results'\n\
Lines of results_file are of the form \n\
     030  Q0  ZF08-175-870  0   4238   prise1 \n\
     qid iter   docno      rank  sim   run_id \n\
giving TREC document numbers (a string) retrieved by query qid  \n\
(a string) with similarity sim (a float).  The other fields are ignored, \n\
with the exception that the run_id field of the last line is kept and \n\
output.  In particular, note that the rank field is ignored here; \n\
internally ranks are assigned by sorting by the sim field with ties  \n\
broken deterministicly (using docno). \n\
Sim is assumed to be higher for the docs to be retrieved first. \n\
File may contain no NULL characters. \n\
Lines may contain fields after the run_id; they are ignored. \n\
",
     te_get_trec_results, te_get_trec_results_cleanup},
};
int te_num_results_format =
    sizeof (te_results_format)/sizeof (te_results_format[0]);

int te_form_res_rels_cleanup (), te_form_res_rels_jg_cleanup (),
    te_form_pref_counts_cleanup (), te_form_pref_counts_cleanup ();

FORM_INTER_PROCS te_form_inter_procs[] = {
    {"qrels", "trec_results",
     "Process for evaluating qrels and trec_results",
     /* te_form_res_rels, */
     te_form_res_rels_cleanup},
    {"qrels_jg", "trec_results",
     "Process for evaluating qrels_jg and trec_results",
     /* te_form_res_rels_jg, */
     te_form_res_rels_jg_cleanup},
    {"prefs", "trec_results",
     "Process for evaluating prefs and trec_results",
     /* te_form_prefs_counts, */
     te_form_pref_counts_cleanup},
    {"qrels_prefs", "trec_results",
     "   Copyright (c) 2008 - Chris Buckley. \n\
\n\
   Permission is granted for use and modification of this file for\n\
   research, non-commercial purposes. \n\
\n\
   Process for evaluating qrels_prefs and trec_results",
     /* te_form_prefs_counts, */
     te_form_pref_counts_cleanup},
};

int te_num_form_inter_procs =
    sizeof (te_form_inter_procs)/sizeof (te_form_inter_procs[0]);


