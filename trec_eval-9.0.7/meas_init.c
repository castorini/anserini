/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/

#include "common.h"
#include "sysfunc.h"
#include "trec_eval.h"
#include "functions.h"

/* Procedures for initialization of several default formats of measure values.
   Used in init_meas procedure description of TREC_MEAS in trec_eval.h:
      typedef struct trec_meas {
         ...
        * Store parameters for measure in meas_params. Reserve space in
	TREC_EVAL.values for results of measure. Store individual measure
	names (possibly altered by parameters) in TREC_EVAL.values.
	Set tm->eval_index to start of reserved space *
         ...
      } TREC_MEAS;
   Measures are defined in measures.c.
*/

/* Static utility functions defined later */
static int get_long_cutoffs (PARAMS *params, char *param_string);
static int get_float_cutoffs (PARAMS *params, char *param_string);
static int get_float_params (PARAMS *params, char *param_string);
static int get_param_pairs (PARAMS *params, char *param_string);
static int comp_long ();
static int comp_float ();
static char *append_long (char *name, long value);
static char *append_float (char *name, double value);
static char *append_string (char *name, char *value);

/* ---------------- Init measure generic procedures -------------- */

/* Measure does not require initialization or storage (eg num_q) */
int 
te_init_meas_empty (EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    tm->eval_index = 0;
    return (1);
}

/* Measure is a single float measure with no parameters */
int 
te_init_meas_s_float (EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    /* Make sure enough space */
    if (NULL == (eval->values =
		 te_chk_and_realloc (eval->values, &eval->max_num_values,
				     eval->num_values + 1,
				     sizeof (TREC_EVAL_VALUE))))
	return (UNDEF);

    /* Set location of value of measure, zero it, and increment
       space used for values */
    tm->eval_index = eval->num_values;
    eval->values[tm->eval_index] = (TREC_EVAL_VALUE) {tm->name, 0.0};
    eval->num_values++;
    return (1);
}

/* Measure is a single long measure with no parameters */
int 
te_init_meas_s_long (EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    /* Make sure enough space */
    if (NULL == (eval->values =
		 te_chk_and_realloc (eval->values, &eval->max_num_values,
				     eval->num_values + 1,
				     sizeof (TREC_EVAL_VALUE))))
	return (UNDEF);

    /* Set location of value of measure, zero it, and increment
       space used for values */
    tm->eval_index = eval->num_values;
    eval->values[tm->eval_index] = (TREC_EVAL_VALUE) {tm->name, 0.0};
    eval->num_values++;
    return (1);
}

/* Measure is a float array with long cutoffs */
int 
te_init_meas_a_float_cut_long (EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    long *cutoffs;
    long i;
    /* See if there are command line parameters for this measure.
       Use those if given, otherwise use default cutoffs */
    if (epi->meas_arg) {
	MEAS_ARG *meas_arg_ptr = epi->meas_arg;
	while (meas_arg_ptr->measure_name) {
	    if (0 == strcmp (meas_arg_ptr->measure_name, tm->name)) {
		if (UNDEF == get_long_cutoffs (tm->meas_params,
					       meas_arg_ptr->parameters))
		    return (UNDEF);
		break;
	    }
	    meas_arg_ptr++;
	}
    }
    cutoffs = (long *) tm->meas_params->param_values;

    /* Make sure enough space */
    if (NULL == (eval->values =
		 te_chk_and_realloc (eval->values, &eval->max_num_values,
				 eval->num_values + tm->meas_params->num_params,
				 sizeof (TREC_EVAL_VALUE))))
	return (UNDEF);

    /* Initialize full measure name and value for each cutoff */
    for (i = 0; i < tm->meas_params->num_params; i++) {
	eval->values[eval->num_values+i] = (TREC_EVAL_VALUE)
	    {append_long (tm->name, cutoffs[i]), 0.0};
	if (NULL == eval->values[eval->num_values+i].name)
	    return (UNDEF);
    }

    /* Set location of value of measure, and increment space used for values */
    tm->eval_index = eval->num_values;
    eval->num_values += tm->meas_params->num_params;
    return (1);
}

/* Measure is a float array with float cutoffs */
int 
te_init_meas_a_float_cut_float (EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    double *cutoffs;
    long i;
    /* See if there are command line parameters for this measure.
       Use those if given, otherwise use default cutoffs */
    if (epi->meas_arg) {
	MEAS_ARG *meas_arg_ptr = epi->meas_arg;
	while (meas_arg_ptr->measure_name) {
	    if (0 == strcmp (meas_arg_ptr->measure_name, tm->name)) {
		if (UNDEF == get_float_cutoffs (tm->meas_params,
						meas_arg_ptr->parameters))
		    return (UNDEF);
		break;
	    }
	    meas_arg_ptr++;
	}
    }
    cutoffs = (double *) tm->meas_params->param_values;

    /* Make sure enough space */
    if (NULL == (eval->values =
		 te_chk_and_realloc (eval->values, &eval->max_num_values,
				 eval->num_values + tm->meas_params->num_params,
				     sizeof (TREC_EVAL_VALUE))))
	return (UNDEF);

    /* Initialize full measure name and value for each cutoff */
    for (i = 0; i < tm->meas_params->num_params; i++) {
	eval->values[eval->num_values+i] = (TREC_EVAL_VALUE)
	    {append_float (tm->name, cutoffs[i]), 0.0};
	if (NULL == eval->values[eval->num_values+i].name)
	    return (UNDEF);
    }

    /* Set location of value of measure, and increment space used for values */
    tm->eval_index = eval->num_values;
    eval->num_values += tm->meas_params->num_params;
    return (1);
}

/* Note difference between float cutoffs and float params is that
   cutoff values are unordered, assumed unique, and one eval value is
   allocated to each cutoff. Param values are ordered and can be anything,
   and number of params means nothing about number of values*/

/* Measure is a single float with float params */
int 
te_init_meas_s_float_p_float (EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval)
{
    /* See if there are command line parameters for this measure.
       Use those if given, otherwise use default cutoffs */
    if (epi->meas_arg) {
	MEAS_ARG *meas_arg_ptr = epi->meas_arg;
	while (meas_arg_ptr->measure_name) {
	    if (0 == strcmp (meas_arg_ptr->measure_name, tm->name)) {
		if (UNDEF == get_float_params (tm->meas_params,
					       meas_arg_ptr->parameters))
		    return (UNDEF);
		break;
	    }
	    meas_arg_ptr++;
	}
    }

    /* Make sure enough space */
    if (NULL == (eval->values =
		 te_chk_and_realloc (eval->values, &eval->max_num_values,
				     eval->num_values + 1,
				     sizeof (TREC_EVAL_VALUE))))
	return (UNDEF);

    /* Set location of value of measure, zero it, and increment
       space used for values */
    tm->eval_index = eval->num_values;
    if (tm->meas_params->printable_params) 
	eval->values[tm->eval_index] = (TREC_EVAL_VALUE)
	    {append_string(tm->name, tm->meas_params->printable_params), 0.0};
    else
	eval->values[tm->eval_index] = (TREC_EVAL_VALUE) {tm->name, 0.0};

    if (NULL == eval->values[eval->num_values].name)
	    return (UNDEF);
    eval->num_values++;
    return (1);
}

/* Measure is a single float with paired name=float params */
int
te_init_meas_s_float_p_pair (EPI *epi, TREC_MEAS *tm, TREC_EVAL *eval) 
{
     if (epi->meas_arg) {
        MEAS_ARG *meas_arg_ptr = epi->meas_arg;
        while (meas_arg_ptr->measure_name) {
            if (0 == strcmp (meas_arg_ptr->measure_name, tm->name)) {
		if (UNDEF == get_param_pairs (tm->meas_params,
					       meas_arg_ptr->parameters))
		    return (UNDEF);
		break;
            }
            meas_arg_ptr++;
        }
    }

    /* Make sure enough space */
    if (NULL == (eval->values =
		 te_chk_and_realloc (eval->values, &eval->max_num_values,
				     eval->num_values + 1,	
				     sizeof (TREC_EVAL_VALUE))))
	return (UNDEF);

    /* Set location of value of measure, zero it, and increment
       space used for values */
    tm->eval_index = eval->num_values;
    if (tm->meas_params->printable_params) 
	eval->values[tm->eval_index] = (TREC_EVAL_VALUE)
	    {append_string(tm->name, tm->meas_params->printable_params), 0.0};
    else
	eval->values[tm->eval_index] = (TREC_EVAL_VALUE) {tm->name, 0.0};
    if (NULL == eval->values[eval->num_values].name)
	    return (UNDEF);
    eval->num_values++;
    return (1);
}


/* ----------------- Utility procedures for initialization -------------- */
static int
get_long_cutoffs (PARAMS *params, char *param_string)
{
    long num_cutoffs;
    char *ptr, *start_ptr;
    long *cutoffs;
    long i;

    /* Count number of parameters in param_string (comma separated) */
    num_cutoffs = 1;
    for (ptr = param_string; *ptr; ptr++) {
	if (*ptr == ',') num_cutoffs++;
    }

    /* Reserve space for cutoffs */
    if (NULL == (params->printable_params =
		 Malloc (strlen(param_string)+1, char)) ||
	NULL == (cutoffs = Malloc (num_cutoffs, long)))
	return (UNDEF);
    (void) strncpy (params->printable_params,
		    param_string,
		    strlen(param_string)+1);

    params->num_params = num_cutoffs;
    params->param_values = cutoffs;
    start_ptr = param_string;
    num_cutoffs = 0;
    for (ptr = param_string; *ptr; ptr++) {
	if (*ptr == ',') {
	    *ptr = '\0';
	    cutoffs[num_cutoffs++] = atol(start_ptr);
	    start_ptr = ptr+1;
	}
    }
    cutoffs[num_cutoffs++] = atol(start_ptr);

    /* Sort cutoffs in increasing order */
    qsort ((char *) cutoffs,
           (int) num_cutoffs,
           sizeof (long),
           comp_long);

    /* Sanity checking: cutoffs > 0 and non-duplicates */
    if (cutoffs[0] <= 0) {
	fprintf (stderr, "trec_eval: Negative cutoff detected\n");
	return (UNDEF);
    }
    for (i = 1; i < num_cutoffs; i++) {
	if (cutoffs[i-1] == cutoffs[i]) {
	    fprintf (stderr, "trec_eval: duplicate cutoffs detected\n");
	    return (UNDEF);
	}
    }

    return (1);
}

static int
get_float_cutoffs (PARAMS *params, char *param_string)
{
    long num_cutoffs;
    char *ptr, *start_ptr;
    double *cutoffs;
    long i;

    /* Count number of parameters in param_string (comma separated) */
    num_cutoffs = 1;
    for (ptr = param_string; *ptr; ptr++) {
	if (*ptr == ',') num_cutoffs++;
    }

    /* Reserve space for cutoffs */
    if (NULL == (params->printable_params =
		 Malloc (strlen(param_string)+1, char)) ||
	NULL == (cutoffs = Malloc (num_cutoffs, double)))
	return (UNDEF);
    (void) strncpy (params->printable_params,
		    param_string,
		    strlen(param_string)+1);

    params->num_params = num_cutoffs;
    params->param_values = cutoffs;
    start_ptr = param_string;
    num_cutoffs = 0;
    for (ptr = param_string; *ptr; ptr++) {
	if (*ptr == ',') {
	    *ptr = '\0';
	    cutoffs[num_cutoffs++] = atof(start_ptr);
	    start_ptr = ptr+1;
	}
    }
    cutoffs[num_cutoffs++] = atof(start_ptr);

    /* Sort cutoffs in increasing order */
    qsort ((char *) cutoffs,
           (int) num_cutoffs,
           sizeof (double),
           comp_float);

    /* Sanity checking: non-duplicates */
    for (i = 1; i < num_cutoffs; i++) {
	if (cutoffs[i-1] == cutoffs[i]) {
	    fprintf (stderr, "trec_eval: duplicate cutoffs detected\n");
	    return (UNDEF);
	}
    }

    return (1);
}

static int
get_float_params (PARAMS *params, char *param_string)
{
    long num_params;
    char *ptr, *start_ptr;
    double *values;

    /* Count number of parameters in param_string (comma separated) */
    num_params = 1;
    for (ptr = param_string; *ptr; ptr++) {
	if (*ptr == ',') num_params++;
    }

    /* Reserve space for params */
    if (NULL == (params->printable_params =
		 Malloc (strlen(param_string)+1, char)) ||
	NULL == (values = Malloc (num_params, double)))
	return (UNDEF);

    (void) strncpy (params->printable_params,
		    param_string,
		    strlen(param_string)+1);

    start_ptr = param_string;
    num_params = 0;
    for (ptr = param_string; *ptr; ptr++) {
	if (*ptr == ',') {
	    *ptr = '\0';
	    values[num_params++] = atof(start_ptr);
	    start_ptr = ptr+1;
	}
    }
    values[num_params++] = atof(start_ptr);
    params->param_values = values;
    params->num_params = num_params;
    return (1);
}

/* Params are in comma separated form name=float. Eg, -m ndcg_p.1=4.0,2=8.0 */
static int
get_param_pairs (PARAMS *params, char *param_string)
{
    long num_params;
    char last_seen;
    char *ptr, *start_ptr;
    FLOAT_PARAM_PAIR *values;

    /* Count number of parameters in param_string (comma separated), all
       of form name=value.  Return error if not of right form */
    num_params = 1;
    last_seen = ',';
    for (ptr = param_string; *ptr; ptr++) {
        if (*ptr == ',') {
	    num_params++;
	    if (*ptr == last_seen)  /* Error */
		break;
	    last_seen = ',';
	}
	else if (*ptr == '=') {
	    if (*ptr == last_seen) {/* Flag as error */
		last_seen = ',';
		break;
	    }
	    last_seen = '=';
	}
    }
    if (last_seen != '=') {
	fprintf (stderr, "trec_eval: malformed pair parameters in '%s'\n",
		 param_string);
	return (UNDEF);
    }

    /* Reserve space for params */
    if (NULL == (params->printable_params =
		 Malloc (strlen(param_string)+1, char)) ||
        NULL == (values = Malloc (num_params, FLOAT_PARAM_PAIR)))
        return (UNDEF);

    (void) strncpy (params->printable_params,
		    param_string,
		    strlen(param_string)+1);

    start_ptr = param_string;
    num_params = 0;
    for (ptr = param_string; *ptr; ptr++) {
        if (*ptr == '=') {
            *ptr = '\0';
            values[num_params].name = start_ptr;
            start_ptr = ptr+1;
        }
        else if (*ptr == ',') {
            *ptr = '\0';
            values[num_params++].value = atof(start_ptr);
            start_ptr = ptr+1;
        }
    }
    values[num_params++].value = atof(start_ptr);

    params->param_values = values;
    params->num_params = num_params;

    return (1);
}

static int
comp_long (long *ptr1, long *ptr2)
{
    return (*ptr1 - *ptr2);
}
static int
comp_float (double *ptr1, double *ptr2)
{
    if (*ptr1 < *ptr2)
	return (-1);
    if (*ptr1 > *ptr2)
	return (1);
    return (0);
}

static char *
append_long (char *name, long value)
{
    long length_required = strlen(name) + 20 + 2;
    char *full_name;
    if (NULL == (full_name = Malloc (length_required, char)))
	return (NULL);
    snprintf (full_name, length_required, "%s_%ld", name, value);
    return (full_name);
}

static char *
append_float (char *name, double value)
{
    long length_required = strlen(name) + 8 + 2;
    char *full_name;
    if (NULL == (full_name = Malloc (length_required, char)))
	return (NULL);
    snprintf (full_name, length_required, "%s_%3.2f", name, value);
    return (full_name);
}
static char *
append_string (char *name, char *value)
{
    long length_required;
    char *full_name;
    if (NULL == value)
	return (name);
    length_required = strlen(name) + strlen(value) + 2;
    if (NULL == (full_name = Malloc (length_required, char)))
	return (NULL);
    snprintf (full_name, length_required, "%s_%s", name, value);
    return (full_name);
}
