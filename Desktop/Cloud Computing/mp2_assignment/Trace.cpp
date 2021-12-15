//////////////////////////////////////////////////////////////////////////////
//****************************************************************************
//
//    FILE NAME: Trace.cpp
//
//    DECSRIPTION: This is the source file for trace functionality
//
//    CHANGE ACTIVITY:
//    Date        Who      Description
//    ==========  =======  ===============
//
//****************************************************************************
//////////////////////////////////////////////////////////////////////////////

/*
 * Header files
 */
#include "Trace.h"

/*****************************************************************
 * NAME: traceFileCreate
 *
 * DESCRIPTION: Function creates a trace file
 *
 * RETURN:
 * (int) SUCCESS
 *       ERROR otherwise
 *
 ****************************************************************/
int Trace::traceFileCreate() {

    int rc = SUCCESS;        // Return code

    logF = fopen(LOG_FILE_LOCATION, "w");
    if ( NULL == logF )
    {
        printf("\nUnable to open log file in write mode\n");
        rc = FAILURE;
    }

    return rc;
}

/*****************************************************************
 * NAME: printToTrace
 *
 * DESCRIPTION: Function takes care of writing into log
 *
 * PARAMETERS:
 *            (char *) keyMsg - buffer to be written as key
 *            (char *) valueMsg - buffer to be written as value
 *
 * RETURN:
 * (int) SUCCESS
 *       FAILURE otherwise
 *
 ****************************************************************/
int Trace::printToTrace(char *keyMsg, char *valueMsg) {

    int rc = SUCCESS;        // Return code

    fprintf(logF, "%s : %s\n", keyMsg, valueMsg);
    fflush(logF);

    return rc;
}

/*****************************************************************
 * NAME: traceFileClose
 *
 * DESCRIPTION: Function takes care of closing log
 *
 * RETURN:
 * (int) SUCCESS
 *       ERROR otherwise
 *
 ****************************************************************/
int Trace::traceFileClose() {

    int rc = SUCCESS;

    fclose(logF);

    return rc;
}

/*****************************************************************
 * NAME: funcEntry
 *
 * DESCRIPTION: Logs function entry
 *
 * PARAMETERS:
 *            (char *) keyMsg - ipAddress
 *            (char *) valueMsg - funcName
 *
 * RETURN:
 * (int) ZERO if success
 *       ERROR otherwise
 *
 ****************************************************************/
int Trace::funcEntry(char *funcName) {

    int rc = SUCCESS;        // Return code

    fprintf(logF, "ENTRY - %s\n", funcName);
    fflush(logF);

    return rc;
}

/*****************************************************************
 * NAME: funcExit
 *
 * DESCRIPTION: Logs function exit
 *
 * PARAMETERS:
 *            (char *) keyMsg - ipAddress
 *            (char *) valueMsg - funcName
 *            (int) f_rc - return code of function
 *
 * RETURN:
 * (int) ZERO if success
 *       ERROR otherwise
 *
 ****************************************************************/
int Trace::funcExit(char *funcName, int f_rc) {

    int rc = SUCCESS;        // Return code

    fprintf(logF, "EXIT - %s with rc = %d\n", funcName, f_rc);
    fflush(logF);

    return rc;
}
