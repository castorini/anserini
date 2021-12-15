/**********************************
 * FILE NAME: Trace.h
 *
 * DESCRIPTION: Header file Trace class
 **********************************/

#ifndef TRACE_H_
#define TRACE_H_

#include "stdincludes.h"

/*
 * Macros
 */
#define LOG_FILE_LOCATION "machine.log"

/**
 * CLASS NAME: Trace
 *
 * DESCRIPTION: Creates a trace of function entry, exit and variable values for debugging
 */
class Trace {
public:
	FILE *logF;
	int traceFileCreate();
	int printToTrace(
             char *keyMessage,    // Message to be written as key
             char *valueMessage   // Message to be written as value
             );
	int traceFileClose();
	int funcEntry(
              char *valueMessage  // Value
			  );
	int funcExit(
             char *valueMessage, // Value
             int f_rc = SUCCESS           // Function RC
             );
};

#endif
