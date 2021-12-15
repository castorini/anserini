/**********************************
 * FILE NAME: Application.h
 *
 * DESCRIPTION: Header file of all classes pertaining to the Application Layer
 **********************************/

#ifndef _APPLICATION_H_
#define _APPLICATION_H_

#include "stdincludes.h"
#include "MP1Node.h"
#include "Log.h"
#include "Params.h"
#include "Member.h"
#include "EmulNet.h"
#include "Queue.h"
#include "MP2Node.h"
#include "Node.h"
#include "common.h"

/**
 * global variables
 */
int nodeCount = 0;
static const char alphanum[] =
"0123456789"
"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
"abcdefghijklmnopqrstuvwxyz";

/*
 * Macros
 */
#define ARGS_COUNT 2
#define TOTAL_RUNNING_TIME 700
#define INSERT_TIME (TOTAL_RUNNING_TIME-600)
#define TEST_TIME (INSERT_TIME+50)
#define STABILIZE_TIME 50
#define FIRST_FAIL_TIME 25
#define LAST_FAIL_TIME 10
#define RF 3
#define NUMBER_OF_INSERTS 100
#define KEY_LENGTH 5

/**
 * CLASS NAME: Application
 *
 * DESCRIPTION: Application layer of the distributed system
 */
class Application{
private:
	// Address for introduction to the group
	// Coordinator Node
	char JOINADDR[30];
	EmulNet *en;
	EmulNet *en1;
    Log *log;
	MP1Node **mp1;
	MP2Node **mp2;
	Params *par;
	map<string, string> testKVPairs;
public:
	Application(char *);
	virtual ~Application();
	Address getjoinaddr();
	void initTestKVPairs();
	int run();
	void mp1Run();
	void mp2Run();
	void fail();
	void insertTestKVPairs();
	int findARandomNodeThatIsAlive();
	void deleteTest();
	void readTest();
	void updateTest();
};

#endif /* _APPLICATION_H__ */
