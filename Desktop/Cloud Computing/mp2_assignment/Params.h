/**********************************
 * FILE NAME: Params.h
 *
 * DESCRIPTION: Header file of Parameter class
 **********************************/

#ifndef _PARAMS_H_
#define _PARAMS_H_

#include "stdincludes.h"
#include "Params.h"
#include "Member.h"

enum testTYPE { CREATE_TEST, READ_TEST, UPDATE_TEST, DELETE_TEST };

/**
 * CLASS NAME: Params
 *
 * DESCRIPTION: Params class describing the test cases
 */
class Params{
public:
	int MAX_NNB;                // max number of neighbors
	int SINGLE_FAILURE;			// single/multi failure
	double MSG_DROP_PROB;		// message drop probability
	double STEP_RATE;		    // dictates the rate of insertion
	int EN_GPSZ;			    // actual number of peers
	int MAX_MSG_SIZE;
	int DROP_MSG;
	int dropmsg;
	int globaltime;
	int allNodesJoined;
	short PORTNUM;
	int CRUDTEST;
	Params();
	void setparams(char *);
	int getcurrtime();
};

#endif /* _PARAMS_H_ */
