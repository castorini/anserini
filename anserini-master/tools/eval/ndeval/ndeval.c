/* only evaluate e (which could cause out-of-bound reference) when z is false */
#define TopicVal(z,e) (z ? 0 : e)
#define RiskBased(r,b,a) ((r) >= (b) ? (r) - (b) : ((a)+1) * ((r)-(b)))

char *usageText = 
  "Usage: %s [options] qrels run   (-help for full usage information)\n";

char *helpText =
"ndeval [options] qrels run\n"
"  Compute novelty and diversity evaluation measures for TREC Web tasks.\n"
"  Evalution measures are written to standard output as a CSV file.\n"
"\n"
"  options:\n"
"    -alpha value\n"
"        Redundancy intolerance (parameter for computing alpha-nDCG and NRBP)\n"
"    -beta value\n"
"        Patience (parameter for computing NRBP)\n"
"    -traditional\n"
"        Sort runs by score and then by docno, which is the traditional\n"
"        behavior for TREC.  By default, the program sorts runs by rank.\n"
"    -baseline BASELINE_RUN_FILE\n"
"        Baseline run to use for risk-sensitive evaluation\n"
"    -riskAlpha value\n"
"        Non-negative Risk sensitivity value to use when doing risk-sensitive\n"
"        evaluation.  A baseline must still be specified.  By default 0.\n"
"        The final weight to downside changes in performance is (1+value).\n"
"    -c\n"
"        Average over the complete set of topics in the relevance judgments\n"
"        instead of the topics in the union of the intersection of relevance\n"
"        judgments and results and the topics in the risk baseline (if\n"
"        specified).\n"
"    -M depth\n"
"        Cut off run at given depth.\n"
"\n"
"  Computes the following measures.  All measures are \"intent aware\" in\n"
"  the sense of Agrawal et al. (WSDM 20009). Normalization may be collection\n"
"  dependent or collection independent.\n"
"\n"
"    ERR-IA@k for k = 5, 10 and 20\n"
"      Chapelle et al. (CIKM 2009) with collection-independent normalization\n"
"    nERR-IA@k for k = 5, 10 and 20\n"
"      Chapelle et al. (CIKM 2009) with collection-dependent normalization\n"
"    alpha-DCG@k for k = 5, 10 and 20\n"
"      Clarke et al. (SIGIR 2008) with collection-independent normalization\n"
"    alpha-nDCG@k for k = 5, 10 and 20\n"
"      Clarke et al., SIGIR 2008 with collection-dependent normalization\n"
"    NRBP\n"
"      Clarke et al. (ICTIR 2009) with collection-independent normalization\n"
"    nNRBP\n"
"      Clarke et al. (ICTIR 2009) with collection-dependent normalization\n"
"    MAP-IA\n"
"      intent aware mean average precision\n"
"    P-IA@k for k = 5, 10, 20\n"
"      intent aware precision@k\n"
"    strec@k for k = 5, 10, 20\n"
"      subtopic recall (the number of subtopics covered by the top k docs)\n";

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <math.h>
#include <ctype.h>

#define DEPTH 20  /* max depth for computing alpha-ndcg, precision-ia, etc. */
#define ALPHA 0.5 /* default alpha value for alpha-nDCG and NRBP */
#define BETA  0.5 /* default beta value for NRBP */

static char *version = "version 4.5 (Mon 29 Apr 2013 20:51:02 EDT)";

#define LARGE_ENOUGH 100000

/* Global command line parameters */
static double alpha = ALPHA;  /* alpha value for alpha-nDCG and NRBP */
static double beta = BETA;    /* beta value for NRBP */
static int traditional = 0;   /* use traditional TREC sort order for runs */
static int depthM = 0;        /* cut off rank for runs (0 => no cut off) */

static char *programName = (char *) 0;

static void
error (char *format, ...)
{
  va_list args;

  fflush (stderr);
  if (programName)
    fprintf (stderr, "%s: ", programName);
  va_start (args, format);
  vfprintf (stderr, format, args);
  va_end (args);
  fflush (stderr);
  exit (1);
}

static void *
localMalloc (size_t size)
{
  void *memory;

  if ((memory = malloc (size)))
    return memory;
  else
    {
      error ("Out of memory!\n");
      /*NOTREACHED*/
      return (void *) 0;
    }
}

static void *
localRealloc (void *memory, size_t size)
{
  if ((memory = realloc (memory, size)))
    return memory;
  else
    {
      error ("Out of memory!\n");
      /*NOTREACHED*/
      return (void *) 0;
    }
}

static char *
localStrdup (const char *string)
{
  return strcpy (localMalloc (strlen (string) + 1), string);
}

static void
setProgramName (char *argv0)
{
  char *pn;

  if (argv0 == (char *) 0)
    {
      programName = (char *) 0;
      return;
    }

  for (pn = argv0 + strlen (argv0); pn > argv0; --pn)
    if (*pn == '/')
      {
        pn++;
        break;
      }

  programName = localStrdup (pn);
}

static char *
mygetline (FILE *fp)
{
  int const GETLINE_INITIAL_BUFSIZ = 256;
  static unsigned bufsiz = 0;
  static char *buffer = (char *) 0;
  unsigned count = 0;

  if (bufsiz == 0)
    {
      buffer = (char *) localMalloc ((unsigned) GETLINE_INITIAL_BUFSIZ);
      bufsiz = GETLINE_INITIAL_BUFSIZ;
    }

  if (fgets (buffer, bufsiz, fp) == NULL)
    return (char *) 0;

  for (;;)
    {
      unsigned nlpos = strlen (buffer + count) - 1;
      if (buffer[nlpos + count] == '\n')
        {
          if (nlpos && buffer[nlpos + count - 1] == '\r')
            --nlpos;
          buffer[nlpos + count] = '\0';
          return buffer;
        }
      count = bufsiz - 1;
      bufsiz <<= 1;
      buffer = (char *) localRealloc (buffer, (unsigned) bufsiz);
      if (fgets (buffer + count, count + 2, fp) == NULL)
        {
          buffer[count] = '\0';
          return buffer;
        }
    }
}

static int
split (char *s, char **a, int m)
{
  int n = 0;

  while (n < m)
    {
      for (; isspace (*s); s++)
        ;
      if (*s == '\0')
        return n;

      a[n++] = s;

      for (s++; *s && !isspace (*s); s++)
        ;
      if (*s == '\0')
        return n;

      *s++ = '\0';
    }

  return n;
}

static int
naturalNumber (char *s)
{
  int value = 0;

  if (s == (char *) 0 || *s == '\0')
    return -1;

  for (; *s; s++)
    if (*s >= '0' && *s <= '9')
      {
	if (value > LARGE_ENOUGH)
	  return -1;
	value = 10*value + (*s - '0');
      }
    else
      return -1;

  return value;
}

/* parseTopic:
     topic numbers in run files may be prefaced by a string indicating the task;
     remove this string (e.g., "wt09-") and extract the topic number;
     we assume the string ends with a "-" character;
*/
static int parseTopic(char *s)
{
  if (*s >= '0' && *s <= '9')
    return naturalNumber (s);

  for (;*s && *s != '-'; s++)
    ;
    
  return naturalNumber (s + 1);
}


struct result { /* a single result, with a pointer to relevance judgments */
  char *docno;
  int topic, rank, *rel;
  double score;  /* used only for traditional sort */
};

struct rList { /* result list (or summarized qrels) for a single topic  */
  struct result *list;
  int topic, subtopics, actualSubtopics, results;
  int nrel, *nrelSub;
  double mapIA, map;
  double nrbp, nnrbp;
  double dcg[DEPTH], ndcg[DEPTH];
  double err[DEPTH], nerr[DEPTH];
  double precision[DEPTH]; /* really precision-IA */
  double strec[DEPTH]; /* subtopic recall */
};

struct qrel {  /* a single qrel */
  char *docno;
  int topic, subtopic, judgment;
};

/* qrelCompare:
     qsort comparison function for qrels
*/
static int
qrelCompare (const void *a, const void *b)
{
  struct qrel *aq = (struct qrel *) a;
  struct qrel *bq = (struct qrel *) b;

  if (aq->topic < bq->topic)
    return -1;
  if (aq->topic > bq->topic)
    return 1;
  return strcmp (aq->docno, bq->docno);
}

/* qrelSort:
     sort qrels by topic and then by docno
*/
static void
qrelSort (struct qrel *q, int n)
{
  qsort (q, n, sizeof (struct qrel), qrelCompare);
}

/* qrelCountTopics:
     count the number of distinct topics in a qrel file; assume qrels are sorted
*/
static int
qrelCountTopics (struct qrel *q, int n)
{
  int i, topics = 1, currentTopic = q[0].topic;

  for (i = 1; i < n; i++)
    if (q[i].topic != currentTopic)
      {
	topics++;
	currentTopic = q[i].topic;
      }

  return topics;
}

/* nrel:
     for a given qrel result list, compute overall number of relevant documents
     by assuming that a document which is relevant to any subtopic is relevant
     to the topic as a whole; used to compute (non-intent-aware) MAP.
*/
static void
nrel (struct rList *rl)
{
  int i, j;

  rl->nrel = 0;
  for (i = 0; i < rl->results; i++)
    {
      int todo = 1;

      for (j = 0; todo && j < rl->subtopics; j++)
	if (rl->list[i].rel[j])
	  {
	    rl->nrel++;
	    todo = 0;
	  }
    }
}

/* actualSubtopics:
     for a given qrel result list, determine the number of subtopics actually
     represented; if a subtopic has never received a positive judgment, we
     ignore it.
*/
static void
actualSubtopics (struct rList *rl)
{
  int i;

  rl->actualSubtopics = 0;
  for (i = 0; i < rl->subtopics; i++)
    if (rl->nrelSub[i])
      rl->actualSubtopics++;
}

/* idealResult:
     for a qrel result list, assign ranks to maximize gain at each rank;
     the problem is NP-complete, but a simple greedy algorithm works fine
*/
static void
idealResult (struct rList *rl)
{
  int i, rank;
  double *subtopicGain = (double *) alloca (rl->subtopics*sizeof(double));

  for (i = 0; i < rl->subtopics; i++)
    subtopicGain[i] = 1.0;

  for (i = 0; i < rl->results; i++)
    rl->list[i].rank = 0;

  /* horrible quadratic greedy approximation of the ideal result */
  for (rank = 1; rank <= rl->results; rank++)
    {
      int where = -1;
      double maxScore = 0.0; 

      for (i = 0; i < rl->results; i++)
	if (rl->list[i].rank == 0)
	  {
	    int j;
	    double currentScore = 0.0;

	    for (j = 0; j < rl->subtopics; j++)
	      if (rl->list[i].rel[j])
		currentScore += subtopicGain[j];

	    /* tied scores are arbitrarily resolved by docno */
	    if (
	      where == -1
	      || currentScore > maxScore
	      || (
		currentScore == maxScore
		&& strcmp (rl->list[i].docno, rl->list[where].docno) > 0
	      )
	    )
	      {
		maxScore = currentScore;
		where = i;
	      }
	  }

      rl->list[where].rank = rank;

      for (i = 0; i < rl->subtopics; i++)
	if (rl->list[where].rel[i])
	  subtopicGain[i] *= (1.0 - alpha);
    }
}

/* resultCompareByRank:
     qsort comparison funtion for results; sort by topic and then by rank
*/
int
resultCompareByRank (const void *a, const void *b)
{
  struct result *ar = (struct result *) a;
  struct result *br = (struct result *) b;
  if (ar->topic < br->topic)
    return -1;
  if (ar->topic > br->topic)
    return 1;
  return ar->rank - br->rank;
}

/* resultSortByRank:
     sort results, first by topic and then by rank
*/
static void
resultSortByRank (struct result *list, int results)
{
  qsort (list, results, sizeof (struct result), resultCompareByRank);
}

/* resultCompareByDocno:
     qsort comparison funtion for results; sort by topic and then by docno
*/
static int
resultCompareByDocno (const void *a, const void *b)
{
  struct result *ar = (struct result *) a;
  struct result *br = (struct result *) b;
  if (ar->topic < br->topic)
    return -1;
  if (ar->topic > br->topic)
    return 1;
  return strcmp(ar->docno, br->docno);
}

/* resultSortByRank:
     sort results, first by topic and then by docno
*/
static void
resultSortByDocno (struct result *list, int results)
{
  qsort (list, results, sizeof (struct result), resultCompareByDocno);
}

/* resultCompareByScore:
     qsort comparison funtion for results; sort by topic, then by score, and
     then by docno, which is the traditional sort order for TREC runs
*/
int
resultCompareByScore (const void *a, const void *b)
{
  struct result *ar = (struct result *) a;
  struct result *br = (struct result *) b;
  if (ar->topic < br->topic)
    return -1;
  if (ar->topic > br->topic)
    return 1;
  if (ar->score < br->score)
    return 1;
  if (ar->score > br->score)
    return -1;
  return strcmp (br->docno, ar->docno);
}

/* resultSortByScore:
     sort results; first by topic, then by score, and then by docno
*/
static void
resultSortByScore (struct result *list, int results)
{
  qsort (list, results, sizeof (struct result), resultCompareByScore);
}

/* computeMAP:
     compute intent-aware mean average precision (MAP-IA);
     also computes standard MAP by assuming that a document relevant to any
     subtopic is relevant to the topic as a whole.
*/
static void    
computeMAP (struct rList *rl)
{
  int i,j;
  double count = 0.0, total = 0.0;
  int *subtopicCount = (int *) alloca (rl->subtopics*sizeof(int));
  double *subtopicTotal = (double *) alloca (rl->subtopics*sizeof(double));

  rl->map = rl->mapIA = 0.0;

  if (rl->actualSubtopics == 0)
    return;

  for (j = 0; j < rl->subtopics; j++)
    subtopicCount[j] = subtopicTotal[j] = 0.0;

  for (i = 0; i < rl->results; i++)
  {
    int todo = 1;

    if (rl->list[i].rel)
      for (j = 0; j < rl->subtopics; j++)
	if (rl->list[i].rel[j])
	  {
	    subtopicCount[j]++;
	    subtopicTotal[j] += subtopicCount[j]/((double)(i+1));
	    if (todo)
	      {
		count++;
		total += count/((double)(i+1));
		todo = 0;
	      }
	  }
  }

  rl->map = total/rl->nrel;
  rl->mapIA = 0.0;
  for (j = 0; j < rl->subtopics; j++)
    if (rl->nrelSub[j])
      rl->mapIA += subtopicTotal[j]/rl->nrelSub[j];
  rl->mapIA /= rl->actualSubtopics;
}

/* computeNRBP:
     compute NRBP for a result list;
     assumes all results are labeled with relevance judgments
*/
static void
computeNRBP (struct rList *rl)
{
  int i;
  double decay = 1.0;
  double *subtopicGain = (double *) alloca (rl->subtopics*sizeof(double));

  rl->nrbp = 0.0;
  if (rl->actualSubtopics == 0)
    return;

  for (i = 0; i < rl->subtopics; i++)
    subtopicGain[i] = 1.0;

  for (i = 0; i < rl->results; i++)
    {
      int j;
      double score = 0.0;

      if (rl->list[i].rel)
	for (j = 0; j < rl->subtopics; j++)
	  if (rl->list[i].rel[j])
	    {
	      score += subtopicGain[j];
	      subtopicGain[j] *= (1.0 - alpha);
	    }
      rl->nrbp += score*decay;
      decay *= beta;
    }

  rl->nrbp *= (1 - (1 - alpha)*beta)/rl->actualSubtopics;
}

/* discount:
    compute (and cache) ranked based discount for ndcg
*/
double
discount (int rank)
{
  static double cache[LARGE_ENOUGH];
  static int top = 0;

  if (rank > 0)
    if (rank < top)
      return cache[rank];
    else if (rank < LARGE_ENOUGH)
      {
	do
	  {
	    cache[top] = log(2.0)/log(top + 2.0);
	    top++;
	  }
	while (rank >= top); 
	return cache[rank];
      }
    else
      return log(2.0)/log(rank + 2.0);
  else
    return 1;
}

/* computeERR:
     compute ERR for a result list (a run or qrels);
     assumes all results are labeled with relevance judgments;
     the result is normalized using a simple "ideal ideal" normalization
*/
static void
computeERR (struct rList *rl)
{
  int i;
  double *subtopicGain = (double *) alloca (rl->subtopics*sizeof(double));
  double idealIdeal[DEPTH], idealIdealGain = (double) rl->actualSubtopics;

  for (i = 0; i < DEPTH; i++)
    rl->err[i] = 0.0;

  if (rl->actualSubtopics == 0)
    return;

  for (i = 0; i < rl->subtopics; i++)
    subtopicGain[i] = 1.0;

  for (i = 0; i < DEPTH && i < rl->results; i++)
    {
      int j;
      double score = 0.0;

      if (rl->list[i].rel)
	for (j = 0; j < rl->subtopics; j++)
	  if (rl->list[i].rel[j])
	    {
	      score += subtopicGain[j];
	      subtopicGain[j] *= (1.0 - alpha);
	    }
      rl->err[i] = score/((double)(i + 1));
    }

  for (i = 0; i < DEPTH; i++)
    {
      idealIdeal[i] = idealIdealGain/((double)(i + 1));
      idealIdealGain *= (1.0 - alpha);
    }

  for (i = 1; i < DEPTH; i++)
    {
      rl->err[i] += rl->err[i-1];
      idealIdeal[i] += idealIdeal[i - 1];
    }

  /* simple normalization ("ideal ideal normalization") */
  for (i = 1; i < DEPTH; i++)
    rl->err[i] /= idealIdeal[i];
}

/* computeDCG:
     compute DCG for a result list (a run or qrels);
     assumes all results are labeled with relevance judgments;
     the result is normalized using a simple "ideal ideal" normalization
     (standard nDCG normalization comes later)
*/
static void
computeDCG (struct rList *rl)
{
  int i;
  double *subtopicGain = (double *) alloca (rl->subtopics*sizeof(double));
  double idealIdeal[DEPTH], idealIdealGain = (double) rl->actualSubtopics;

  for (i = 0; i < DEPTH; i++)
    rl->dcg[i] = 0.0;

  if (rl->actualSubtopics == 0)
    return;

  for (i = 0; i < rl->subtopics; i++)
    subtopicGain[i] = 1.0;

  for (i = 0; i < DEPTH && i < rl->results; i++)
    {
      int j;
      double score = 0.0;

      if (rl->list[i].rel)
	for (j = 0; j < rl->subtopics; j++)
	  if (rl->list[i].rel[j])
	    {
	      score += subtopicGain[j];
	      subtopicGain[j] *= (1.0 - alpha);
	    }
      rl->dcg[i] = score*discount(i);
    }

  for (i = 0; i < DEPTH; i++)
    {
      idealIdeal[i] = idealIdealGain*discount(i);
      idealIdealGain *= (1.0 - alpha);
    }

  for (i = 1; i < DEPTH; i++)
    {
      /* cumulative gain */
      rl->dcg[i] += rl->dcg[i-1];
      idealIdeal[i] += idealIdeal[i - 1];
    }

  /* simple normalization ("ideal ideal normalization") */
  for (i = 1; i < DEPTH; i++)
    rl->dcg[i] /= idealIdeal[i];
}

/* computeSTRecall
     compute subtopic recall for a result list;
     assumes all results are labeled with relevance judgments
*/
static void
computeSTRecall (struct rList *rl)
{
  int i, j, count = 0;
  int *subtopicSeen = (int *) alloca (rl->subtopics*sizeof(int));

  if (rl->actualSubtopics == 0)
    return;

  for (j = 0; j < rl->subtopics; j++)
    subtopicSeen[j] = 0;

  for (i = 0; i < DEPTH && i < rl->results; i++)
    {
      if (rl->list[i].rel)
	for (j = 0; j < rl->subtopics; j++)
	  if (subtopicSeen[j] == 0 && rl->list[i].rel[j])
	    {
	      count++;
	      subtopicSeen[j] = 1;
	    }
      rl->strec[i] = ((double) count/ (double) rl->actualSubtopics);
    }

  for (; i < DEPTH; i++)
    rl->strec[i] = ((double) count/ (double) rl->actualSubtopics);
}

/* computePrecision:
     compute intent aware precision for a result list;
     assumes all results are labeled with relevance judgments
*/
static void
computePrecision (struct rList *rl)
{
  int i, count = 0;

  if (rl->actualSubtopics == 0)
    return;

  for (i = 0; i < DEPTH && i < rl->results; i++)
    {
      if (rl->list[i].rel)
	{
	  int j;

	  for (j = 0; j < rl->subtopics; j++)
	    if (rl->list[i].rel[j])
	      count++;
	}
      rl->precision[i] = ((double) count)/((i + 1)*rl->actualSubtopics);
    }

  for (; i < DEPTH; i++)
    rl->precision[i] = ((double) count)/((i + 1)*rl->actualSubtopics);
}

/* qrelPopulateResultList:
     populate an ideal result list from the qrels;
     also used to label the run with relevance judgments
*/
static void
qrelPopulateResultList (struct qrel *q, int n, struct rList *rl, int topics)
{
  int i, j, k, currentTopic;
  char *currentDocno = "";

  j = currentTopic = -1;
  for (i = 0; i < n; i++)
    {
      if (q[i].topic != currentTopic)
	{
	  j++;
	  currentTopic = q[i].topic;
	  currentDocno = "";
	  rl[j].topic = currentTopic;
	  rl[j].subtopics = rl[j].results = 0;
	  rl[j].nrel = 0;
	  rl[j].mapIA = rl[j].map = 0.0;
	  rl[j].nrbp = rl[j].nnrbp = 0.0;
	  for (k = 0; k < DEPTH; k++)
	    rl[j].dcg[k] = rl[j].ndcg[k] = rl[j].err[k] = rl[j].nerr[k]
	      = rl[j].precision[k] = rl[j].strec[k] = 0.0;
	}
      if (rl[j].subtopics <= q[i].subtopic)
	rl[j].subtopics = q[i].subtopic + 1;
      if (strcmp (q[i].docno, currentDocno) != 0)
	{
	  currentDocno = q[i].docno;
	  rl[j].results++;
	}
    }

  for (i = 0; i < topics; i++)
    {
      rl[i].list =
	(struct result *) localMalloc (rl[i].results*sizeof (struct result));
      rl[i].nrelSub = (int *) localMalloc (rl[i].subtopics*sizeof (int));
      for (j = 0; j < rl[i].subtopics; j++)
	rl[i].nrelSub[j] = 0;
      for (j = 0; j < rl[i].results; j++)
	{
	  rl[i].list[j].topic = rl[i].topic;
	  rl[i].list[j].rel =
	    (int *) localMalloc (rl[i].subtopics*sizeof (int));
	  for (k = 0; k < rl[i].subtopics; k++)
	    rl[i].list[j].rel[k] = 0;
	}
    }

  j = k = currentTopic = -1;
  currentDocno = "";
  for (i = 0; i < n; i++)
    {
      if (q[i].topic != currentTopic)
	{
	  j++;
	  currentTopic = q[i].topic;
	  k = -1;
	  currentDocno = "";
	}
      if (strcmp (q[i].docno, currentDocno) != 0)
	{
	  currentDocno = q[i].docno;
	  k++;
	  rl[j].list[k].docno = localStrdup (currentDocno);
	}
      rl[j].list[k].rel[q[i].subtopic] = q[i].judgment;
      rl[j].nrelSub[q[i].subtopic] += q[i].judgment;
    }

  for (i = 0; i < topics; i++)
    {
      nrel(rl + i);
      actualSubtopics (rl + i);
      idealResult (rl + i);
      resultSortByRank (rl[i].list, rl[i].results);
      computeDCG (rl + i);
      computeNRBP(rl + i);
      computeERR (rl + i);
      resultSortByDocno (rl[i].list, rl[i].results);
    }
}

/* qrelToRList:
     construct an ideal result list from the qrels
*/
static struct rList *
qrelToRList (struct qrel *q, int n, int *topics)
{
  struct rList *rl;

  *topics = qrelCountTopics (q, n);
  rl = (struct rList *) localMalloc ((*topics)*sizeof (struct rList));
  qrelPopulateResultList (q, n, rl, *topics);

  return rl;
}

/* qrelToRList:
     process the qrels file, contructing an ideal result list
*/
static struct rList *
processQrels (char *fileName, int *topics)
{
  FILE *fp;
  char *line;
  struct qrel *q;
  int i = 0, n = 0;

  if ((fp = fopen (fileName, "r")) == NULL)
    error ("cannot open qrel file \"%s\"\n", fileName);

  while (mygetline(fp))
    n++;

  fclose (fp);

  if (n == 0)
    error ("qrel file \"%s\" is empty\n", fileName);

  q = localMalloc (n*sizeof (struct qrel));

  if ((fp = fopen (fileName, "r")) == NULL)
    error ("cannot open qrel file \"%s\"\n", fileName);

  while ((line = mygetline (fp)))
    {
      char *a[4];
      int topic, subtopic, judgment;

      if (
	split (line, a, 4) != 4
	|| (topic = naturalNumber (a[0])) < 0
	|| (subtopic = naturalNumber (a[1])) < 0
	|| (judgment = naturalNumber (a[3])) < 0
      )
	error (
	  "syntax error in qrel file \"%s\" at line %d\n", fileName, i + 1
	);
      else
	{
	  q[i].topic = topic;
	  q[i].subtopic = subtopic;
	  if (judgment > 1) judgment = 1; /* binary assessment only */
	  q[i].judgment = judgment;
	  q[i].docno = localStrdup (a[2]);
	  i++;
	}
    }

  fclose (fp);

  qrelSort (q, n);

  return qrelToRList (q, n, topics);
}

/* resultCountTopics:
     count the number of distinct topics in a run; assumes results are sorted
*/
static int
resultCountTopics (struct result *r, int n)
{
  int i, topics = 1, currentTopic = r[0].topic;

  for (i = 1; i < n; i++)
    if (r[i].topic != currentTopic)
      {
	topics++;
	currentTopic = r[i].topic;
      }

  return topics;
}


/* populateResultList:
     populate a result list from a run
*/
static void
populateResultList (struct result *r, int n, struct rList *rl, int topics)
{
  int i, j, k, currentTopic = -1;

  j = 0;
  for (i = 0; i < n; i++)
    if (r[i].topic != currentTopic)
      {
	currentTopic = r[i].topic;
	rl[j].list = r + i;
	rl[j].topic = currentTopic;
	rl[j].subtopics = 0;
	rl[j].actualSubtopics = 0;
	if (j > 0)
	  rl[j-1].results = rl[j].list - rl[j-1].list;
	rl[j].nrel = 0;
	rl[j].mapIA = rl[j].map = 0.0;
	rl[j].nrbp = rl[j].nnrbp = 0.0;
	for (k = 0; k < DEPTH; k++)
	    rl[j].dcg[k] = rl[j].ndcg[k] = rl[j].err[k] = rl[j].nerr[k]
	      = rl[j].precision[k] = rl[j].strec[k] = 0.0;
	j++;
      }
  if (j > 0)
    rl[j-1].results = (r + n) - rl[j-1].list;
}

/* forceTraditionalRanks:
     Re-assign ranks so that runs are sorted by score and then by docno,
     which is the traditional sort order for TREC runs.
*/
static void
forceTraditionalRanks (struct result *r, int n)
{
  int i, rank, currentTopic = -1;

  resultSortByScore (r, n);

  for (i = 0; i < n; i++)
    {
      if (r[i].topic != currentTopic)
	{
	  currentTopic = r[i].topic;
	  rank = 1;
	}
      r[i].rank = rank;
      rank++;
    }
}

/* applyCutoff:
     Throw away results deeper than a specified depth.
     Run must be sorted by topic and then rank.
     Return number of remaing results.
*/
static int
applyCutoff (struct result *r, int n, int depthMax)
{
  int i, j, depth, currentTopic = -1;

  j = 0;
  for (i = 0; i < n; i++)
    {
      if (r[i].topic != currentTopic)
	{
	  currentTopic = r[i].topic;
	  depth = 1;
	}
      else
	depth++;
      if (depth <= depthMax)
	r[j++] = r[i];
    }

  return j;
}



/* processRun:
     process a run file, returning a result list
*/
static struct rList *
processRun (char *fileName, int *topics, char **runid)
{
  FILE *fp;
  char *line;
  int i = 0, n = 0;
  int needRunid = 1;
  struct result *r;
  struct rList *rl;

  if ((fp = fopen (fileName, "r")) == NULL)
    error ("cannot open run file \"%s\"n", fileName);

  while (mygetline(fp))
    n++;

  fclose (fp);

  if (n == 0)
    error ("run file \"%s\" is empty\n", fileName);

  r = localMalloc (n*sizeof (struct result));

  if ((fp = fopen (fileName, "r")) == NULL)
    error ("cannot open run file \"%s\"\n", fileName);

  while ((line = mygetline (fp)))
    {
      char *a[6];
      int topic, rank;

      if (
	split (line, a, 6) != 6
	|| (topic = parseTopic (a[0])) < 0
	|| (rank = naturalNumber (a[3])) < 0
      )
	error ("syntax error in run file \"%s\" at line %d\n", fileName, i + 1);
      else
	{
	  if (needRunid)
	    {
	      *runid = localStrdup (a[5]);
	      needRunid = 0;
	    }
	  r[i].docno = localStrdup (a[2]);
	  r[i].topic = topic;
	  r[i].rank = rank;
	  r[i].rel = (int *) 0;
	  sscanf (a[4],"%lf", &(r[i].score));
	  i++;
	}
  }

  /* force ranks to be consistent with traditional TREC sort order */
  if (traditional)
    forceTraditionalRanks (r, n);

  /* for each topic, verify that ranks have not been duplicated */
  resultSortByRank (r, n);
  for (i = 1; i < n; i++)
    if (r[i].topic == r[i-1].topic && r[i].rank == r[i-1].rank)
      error (
	"duplicate rank (%d) for topic %d in run file \"%s\"\n",
	r[i].rank, r[i].topic, fileName
      );

  /* apply depth cutoff if specified on the command line */
  if (depthM > 0)
    n = applyCutoff (r, n, depthM);

  /* for each topic, verify that docnos have not been duplicated */
  resultSortByDocno (r, n);
  for (i = 1; i < n; i++)
    if (r[i].topic == r[i-1].topic && strcmp(r[i].docno,r[i-1].docno) == 0)
      error (
	"duplicate docno (%s) for topic %d in run file \"%s\"\n",
	r[i].docno, r[i].topic, fileName
      );

  /* split results by topic */
  *topics = resultCountTopics (r, n);
  rl = (struct rList *) localMalloc ((*topics)*sizeof (struct rList));
  populateResultList (r, n, rl, *topics);

  return rl;
}

/* applyJudgments:
     copy relevance judgments from qrel results to run results;
     assumes results are sorted by docno
*/
static void
applyJudgments (struct result *q, int qResults, struct result *r, int rResults)
{
  int i = 0, j = 0;

  while (i < qResults && j < rResults)
    {
      int cmp = strcmp (q[i].docno, r[j].docno);
      
      if (cmp < 0)
	  i++;
      else if (cmp > 0)
	  j++;
      else
	{
	  r[j].rel = q[i].rel;
	  i++;
	  j++;
	}
    }
}

/* renormalize:
     normalize a result list against an ideal result list (created from qrels)
*/
static void
renormalize (struct rList *ql, struct rList *rl)
{
  int i;

  for (i = 0; i < DEPTH; i++)
    if (rl->dcg[i])
      {
	rl->ndcg[i] = rl->dcg[i]/ql->dcg[i];
	rl->nerr[i] = rl->err[i]/ql->err[i];
      }

  rl->nnrbp = rl->nrbp/ql->nrbp;
}

/* applyQrels:
     transfer relevance judgments from qrels to a run
*/
static int
applyQrels (struct rList *qrl, int qTopics, struct rList *rrl, int rTopics)
{
  int actualTopics = 0, i = 0, j = 0;

  while (i < qTopics && j < rTopics)
    if (qrl[i].topic < rrl[j].topic)
      i++;
    else if (qrl[i].topic > rrl[j].topic)
      j++;
    else
      {
	rrl[j].subtopics = qrl[i].subtopics;
	rrl[j].actualSubtopics = qrl[i].actualSubtopics;
	rrl[j].nrel = qrl[i].nrel;
	rrl[j].nrelSub = qrl[i].nrelSub;
	applyJudgments (
	  qrl[i].list, qrl[i].results, rrl[j].list, rrl[j].results
	);
	resultSortByRank (rrl[j].list, rrl[j].results);
	computeDCG (rrl + j);
	computeNRBP (rrl + j);
	computeERR (rrl + j);
	renormalize (qrl + i, rrl + j);
	computeSTRecall (rrl + j);
	computePrecision (rrl + j);
	computeMAP (rrl + j);
	i++;
	j++;
	actualTopics++;
      }

  return actualTopics;
}


/* outputMeasures:
   output evaluation measures as a CSV file (ugly, ugly, ugly)
*/
static void
outputMeasures (
struct rList *rl, int rTopics, int actualTopics, char *runid, int AvgOverAllTopics, int all,
  struct rList *bl, double riskAlpha, char * riskAlphaString, int bTopics, char *brunid
  )
{ 
int i, j, nexti, nextj, numTopics;
  double totalERR5 = 0.0, totalERR10 = 0.0, totalERR20 = 0.0;
  double totalnERR5 = 0.0, totalnERR10 = 0.0, totalnERR20 = 0.0;
  double totalDCG5 = 0.0, totalDCG10 = 0.0, totalDCG20 = 0.0;
  double totalnDCG5 = 0.0, totalnDCG10 = 0.0, totalnDCG20 = 0.0;
  double totalNRBP = 0.0, totalnNRBP = 0.0;
  double totalMAPIA = 0.0;
  double totalP5 = 0.0, totalP10 = 0.0, totalP20 = 0.0;
  double totalSTRec5 = 0.0, totalSTRec10 = 0.0, totalSTRec20 = 0.0;
  char * runstring = NULL;

  printf ("runid,topic");
  printf (",ERR-IA@5,ERR-IA@10,ERR-IA@20");
  printf (",nERR-IA@5,nERR-IA@10,nERR-IA@20");
  printf (",alpha-DCG@5,alpha-DCG@10,alpha-DCG@20");
  printf (",alpha-nDCG@5,alpha-nDCG@10,alpha-nDCG@20");
  printf (",NRBP,nNRBP");
  printf (",MAP-IA");
  printf (",P-IA@5,P-IA@10,P-IA@20");
  printf (",strec@5,strec@10,strec@20");
  printf ("\n");

  int ZeroBaseline = 0;
  if ((bl == NULL) || (bTopics == 0))
    {
      ZeroBaseline = 1;
      runstring = localStrdup(runid);
    }
  else
    {
      static char * between = " (rel to. ";
      static char * rwstr = ", rs=1+a, a="; /* risk-sensitivity weight */
      static char * end = ")";
      runstring = localMalloc(strlen(runid) + strlen(between) + strlen(brunid) + strlen(rwstr) + strlen(riskAlphaString) + strlen(end) + 1);
      sprintf(runstring,"%s%s%s%s%s%s",runid,between,brunid,rwstr,riskAlphaString,end);
    }

  
  /* shortcut is hard to get right with baseline */
/*   if (actualTopics == 0) */
/*     { */
/*       printf ("%s,amean", runid); */
/*       printf (",0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00"); */
/*       printf (",0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00"); */
/*       printf ("\n"); */
/*       return; */
/*     } */

  i = j = nexti = nextj = 0;
  numTopics = 0;
  while ((i < rTopics) || ((j < bTopics) && AvgOverAllTopics))
  {
    int ZeroRun = 0;
    int ZeroBase = ZeroBaseline;
    int topic = 0;
    if (i >= rTopics)
      ZeroRun = 1;
    if (j >= bTopics)
      ZeroBase = 1;
    if (!ZeroRun && !ZeroBase)
      {
	if (rl[i].topic == bl[j].topic)
	  {
	    topic = rl[i].topic;
	    nexti++;
	    nextj++;
	  }
	else if (rl[i].topic > bl[j].topic)
	  {
	    /* process baseline, run is zero */
	    ZeroRun = 1;
	    topic = bl[j].topic;
	    nextj++;
	    if (!AvgOverAllTopics)
	      {
		i = nexti;
		j = nextj;
		continue;
	      }
	  }
	else /* (rl[i].topic < bl[j].topic) */
	  {
	    /* process run, baseline is zero */
	    topic = rl[i].topic;
	    ZeroBase = 1;
	    nexti++;
	  }
      }
    else if (ZeroRun)
      {
	topic = bl[j].topic;
	nextj++; /* process baseline */
      }
    else /* (ZeroBase) */
      {
	topic = rl[i].topic;
	nexti++; /* process run */
      }

    printf (
	    "%s,%d"
	    ",%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f"
	    ",%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f\n",
	    runstring, topic,
	      RiskBased(TopicVal(ZeroRun,rl[i].err[4]),TopicVal(ZeroBase,bl[j].err[4]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].err[9]),TopicVal(ZeroBase,bl[j].err[9]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].err[19]),TopicVal(ZeroBase,bl[j].err[19]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].nerr[4]),TopicVal(ZeroBase,bl[j].nerr[4]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].nerr[9]),TopicVal(ZeroBase,bl[j].nerr[9]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].nerr[19]),TopicVal(ZeroBase,bl[j].nerr[19]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].dcg[4]),TopicVal(ZeroBase,bl[j].dcg[4]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].dcg[9]),TopicVal(ZeroBase,bl[j].dcg[9]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].dcg[19]),TopicVal(ZeroBase,bl[j].dcg[19]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].ndcg[4]),TopicVal(ZeroBase,bl[j].ndcg[4]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].ndcg[9]),TopicVal(ZeroBase,bl[j].ndcg[9]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].ndcg[19]),TopicVal(ZeroBase,bl[j].ndcg[19]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].nrbp),TopicVal(ZeroBase,bl[j].nrbp), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].nnrbp),TopicVal(ZeroBase,bl[j].nnrbp), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].mapIA),TopicVal(ZeroBase,bl[j].mapIA), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].precision[4]),TopicVal(ZeroBase,bl[j].precision[4]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].precision[9]),TopicVal(ZeroBase,bl[j].precision[9]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].precision[19]),TopicVal(ZeroBase,bl[j].precision[19]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].strec[4]),TopicVal(ZeroBase,bl[j].strec[4]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].strec[9]),TopicVal(ZeroBase,bl[j].strec[9]), riskAlpha),
	      RiskBased(TopicVal(ZeroRun,rl[i].strec[19]),TopicVal(ZeroBase,bl[j].strec[19]), riskAlpha)
     );
     totalERR5 += RiskBased(TopicVal(ZeroRun,rl[i].err[4]),TopicVal(ZeroBase,bl[j].err[4]), riskAlpha);
     totalERR10 += RiskBased(TopicVal(ZeroRun,rl[i].err[9]),TopicVal(ZeroBase,bl[j].err[9]), riskAlpha);
     totalERR20 += RiskBased(TopicVal(ZeroRun,rl[i].err[19]),TopicVal(ZeroBase,bl[j].err[19]), riskAlpha);
     

     totalnERR5 += RiskBased(TopicVal(ZeroRun,rl[i].nerr[4]),TopicVal(ZeroBase,bl[j].nerr[4]), riskAlpha);
     totalnERR10 += RiskBased(TopicVal(ZeroRun,rl[i].nerr[9]),TopicVal(ZeroBase,bl[j].nerr[9]), riskAlpha);
     totalnERR20 += RiskBased(TopicVal(ZeroRun,rl[i].nerr[19]),TopicVal(ZeroBase,bl[j].nerr[19]), riskAlpha);
    
     totalDCG5 += RiskBased(TopicVal(ZeroRun,rl[i].dcg[4]),TopicVal(ZeroBase,bl[j].dcg[4]), riskAlpha);
     totalDCG10 += RiskBased(TopicVal(ZeroRun,rl[i].dcg[9]),TopicVal(ZeroBase,bl[j].dcg[9]), riskAlpha);
     totalDCG20 += RiskBased(TopicVal(ZeroRun,rl[i].dcg[19]),TopicVal(ZeroBase,bl[j].dcg[19]), riskAlpha);
    

     totalnDCG5 += RiskBased(TopicVal(ZeroRun,rl[i].ndcg[4]),TopicVal(ZeroBase,bl[j].ndcg[4]), riskAlpha);
     totalnDCG10 += RiskBased(TopicVal(ZeroRun,rl[i].ndcg[9]),TopicVal(ZeroBase,bl[j].ndcg[9]), riskAlpha);
     totalnDCG20 += RiskBased(TopicVal(ZeroRun,rl[i].ndcg[19]),TopicVal(ZeroBase,bl[j].ndcg[19]), riskAlpha);


     totalNRBP += RiskBased(TopicVal(ZeroRun,rl[i].nrbp),TopicVal(ZeroBase,bl[j].nrbp), riskAlpha);
     totalnNRBP += RiskBased(TopicVal(ZeroRun,rl[i].nnrbp),TopicVal(ZeroBase,bl[j].nnrbp), riskAlpha);
    
     totalMAPIA += RiskBased(TopicVal(ZeroRun,rl[i].mapIA),TopicVal(ZeroBase,bl[j].mapIA), riskAlpha);

     totalP5 += RiskBased(TopicVal(ZeroRun,rl[i].precision[4]),TopicVal(ZeroBase,bl[j].precision[4]), riskAlpha);
     totalP10 += RiskBased(TopicVal(ZeroRun,rl[i].precision[9]),TopicVal(ZeroBase,bl[j].precision[9]), riskAlpha);
     totalP20 += RiskBased(TopicVal(ZeroRun,rl[i].precision[19]),TopicVal(ZeroBase,bl[j].precision[19]), riskAlpha);

     totalSTRec5 += RiskBased(TopicVal(ZeroRun,rl[i].strec[4]),TopicVal(ZeroBase,bl[j].strec[4]), riskAlpha);
     totalSTRec10 += RiskBased(TopicVal(ZeroRun,rl[i].strec[9]),TopicVal(ZeroBase,bl[j].strec[9]), riskAlpha);
     totalSTRec20 += RiskBased(TopicVal(ZeroRun,rl[i].strec[19]),TopicVal(ZeroBase,bl[j].strec[19]), riskAlpha);

     i = nexti;
     j = nextj;
     numTopics++;
  }

  if ((bl != NULL) && (bTopics != 0))
    {
      if (numTopics > actualTopics)
	actualTopics = numTopics; /* not using the full set but baseline contains some not in the run */
    }


  printf (
	  "%s,amean"
	  ",%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f"
	  ",%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f\n",
	  runstring,
	  totalERR5/actualTopics, totalERR10/actualTopics, totalERR20/actualTopics,
	  totalnERR5/actualTopics, totalnERR10/actualTopics, totalnERR20/actualTopics,
	  totalDCG5/actualTopics, totalDCG10/actualTopics, totalDCG20/actualTopics,
	  totalnDCG5/actualTopics, totalnDCG10/actualTopics, totalnDCG20/actualTopics,
	  totalNRBP/actualTopics, totalnNRBP/actualTopics,
	  totalMAPIA/actualTopics, 
	  totalP5/actualTopics, totalP10/actualTopics, totalP20/actualTopics,
	  totalSTRec5/actualTopics,totalSTRec10/actualTopics,totalSTRec20/actualTopics
	  );
  free(runstring);
}

static void
usage ()
{
  error (usageText, programName);
}

int
main (int argc, char **argv)
{
  char *runid;
  char *brunid = NULL; /* baseline run ID */
  int qTopics, rTopics, actualTopics;
  int bTopics = 0;
  double riskAlpha = 0;
  static char * riskAlphaDefString = "0"; /* set to match riskAlpha */
  char * riskAlphaStr = NULL;
  struct rList *qrl, *rrl, *brl;
  int cFlag = 0; /* average over complete set of queries */
  int aFlag = 0; /* report all measures */
  char *baselineRun = NULL;
  brl = NULL;
  setProgramName (argv[0]);

  while (argc != 3)
    if (argc >= 2 && strcmp ("-version", argv[1]) == 0)
      {
	printf ("%s: %s\n", programName, version);
	exit (0);
      }
    else if (argc >= 2 && strcmp ("-help", argv[1]) == 0)
      {
	printf (helpText);
	exit (0);
      }
    else if (argc >= 5 && strcmp ("-alpha", argv[1]) == 0)
      {
	sscanf (argv[2], "%lf", &alpha);
	if (alpha < 0.0 || alpha > 1.0)
	  usage();
	argc -= 2;
	argv += 2;
      }
    else if (argc >= 5 && strcmp ("-beta", argv[1]) == 0)
      {
	sscanf (argv[2], "%lf", &beta);
	if (beta < 0.0 || beta > 1.0)
	  usage();
	argc -= 2;
	argv += 2;
      }
    else if (argc >= 5 && strcmp ("-M", argv[1]) == 0)
      {
	sscanf (argv[2], "%d", &depthM);
	if (depthM <= 0)
	  usage();
	argc -= 2;
	argv += 2;
      }
    else if (argc >= 5 && strcmp ("-baseline", argv[1]) == 0)
      {
	baselineRun = argv[2];
	argc -= 2;
	argv += 2;
      }
    else if (argc >= 5 && strcmp ("-riskAlpha", argv[1]) == 0)
      {
	sscanf (argv[2], "%lf", &riskAlpha);
	riskAlphaStr = argv[2];
	if (riskAlpha < 0.0)
	  usage();
	argc -= 2;
	argv += 2;
      }
    else if (argc >= 4 && strcmp ("-traditional", argv[1]) == 0)
      {
	traditional = 1;
	--argc;
	argv++;
      }
    else if (argc >= 4 && argv[1][0] == '-')
      {
	int i;

	for (i = 1; argv[1][i]; i++)
	  switch (argv[1][i])
	    {
	    case 'c':
	      cFlag = 1;
	      break;
	    case 'a':
	      aFlag = 1;
	      break;
	    default:
	      usage();
	    }
	--argc;
	argv++;
      }
    else
      usage();

  qrl = processQrels (argv[1], &qTopics);
  rrl = processRun (argv[2], &rTopics, &runid);
  actualTopics = applyQrels (qrl, qTopics, rrl, rTopics);
  if (baselineRun != NULL)
    {
      brl = processRun (baselineRun, &bTopics, &brunid); 
      applyQrels (qrl, qTopics, brl, bTopics);
    }
  if (cFlag)
    actualTopics = qTopics;
  outputMeasures (rrl, rTopics, actualTopics, runid, cFlag, aFlag, brl, riskAlpha,
		  (riskAlphaStr == NULL? riskAlphaDefString : riskAlphaStr),
		  bTopics, brunid);

  return 0;
}

