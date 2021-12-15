/**********************************
 * FILE NAME: Log.h
 *
 * DESCRIPTION: Log class definition
 **********************************/

#include "Log.h"

/**
 * Constructor
 */
Log::Log(Params *p) {
	par = p;
	firstTime = false;
}

/**
 * Copy constructor
 */
Log::Log(const Log &anotherLog) {
	this->par = anotherLog.par;
	this->firstTime = anotherLog.firstTime;
}

/**
 * Assignment Operator Overloading
 */
Log& Log::operator = (const Log& anotherLog) {
	this->par = anotherLog.par;
	this->firstTime = anotherLog.firstTime;
	return *this;
}

/**
 * Destructor
 */
Log::~Log() {}

/**
 * FUNCTION NAME: LOG
 *
 * DESCRIPTION: Print out to file dbg.log, along with Address of node.
 */
void Log::LOG(Address *addr, const char * str, ...) {

	static FILE *fp;
	static FILE *fp2;
	va_list vararglist;
	static char buffer[30000];
	static int numwrites;
	static char stdstring[30];
	static char stdstring2[40];
	static char stdstring3[40]; 
	static int dbg_opened=0;

	if(dbg_opened != 639){
		numwrites=0;

		stdstring2[0]=0;

		strcpy(stdstring3, stdstring2);

		strcat(stdstring2, DBG_LOG);
		strcat(stdstring3, STATS_LOG);

		fp = fopen(stdstring2, "w");
		fp2 = fopen(stdstring3, "w");

		dbg_opened=639;
	}
	else 

	sprintf(stdstring, "%d.%d.%d.%d:%d ", addr->addr[0], addr->addr[1], addr->addr[2], addr->addr[3], *(short *)&addr->addr[4]);

	va_start(vararglist, str);
	vsprintf(buffer, str, vararglist);
	va_end(vararglist);

	if (!firstTime) {
		int magicNumber = 0;
		string magic = MAGIC_NUMBER;
		int len = magic.length();
		for ( int i = 0; i < len; i++ ) {
			magicNumber += (int)magic.at(i);
		}
		fprintf(fp, "%x\n", magicNumber);
		firstTime = true;
	}

	if(memcmp(buffer, "#STATSLOG#", 10)==0){
		fprintf(fp2, "\n %s", stdstring);
		fprintf(fp2, "[%d] ", par->getcurrtime());

		fprintf(fp2, buffer);
	}
	else{
		fprintf(fp, "\n %s", stdstring);
		fprintf(fp, "[%d] ", par->getcurrtime());
		fprintf(fp, buffer);

	}

	if(++numwrites >= MAXWRITES){
		fflush(fp);
		fflush(fp2);
		numwrites=0;
	}

}

/**
 * FUNCTION NAME: logNodeAdd
 *
 * DESCRIPTION: To Log a node add
 */
void Log::logNodeAdd(Address *thisNode, Address *addedAddr) {
	static char stdstring[100];
	sprintf(stdstring, "Node %d.%d.%d.%d:%d joined at time %d", addedAddr->addr[0], addedAddr->addr[1], addedAddr->addr[2], addedAddr->addr[3], *(short *)&addedAddr->addr[4], par->getcurrtime());
    LOG(thisNode, stdstring);
}

/**
 * FUNCTION NAME: logNodeRemove
 *
 * DESCRIPTION: To log a node remove
 */
void Log::logNodeRemove(Address *thisNode, Address *removedAddr) {
	static char stdstring[30];
	sprintf(stdstring, "Node %d.%d.%d.%d:%d removed at time %d", removedAddr->addr[0], removedAddr->addr[1], removedAddr->addr[2], removedAddr->addr[3], *(short *)&removedAddr->addr[4], par->getcurrtime());
    LOG(thisNode, stdstring);
}

/**
 * FUNCTION NAME: logCreateSuccess
 *
 * DESCRTION: Call this function after successfully create a key value pair
 */
void Log::logCreateSuccess(Address * address, bool isCoordinator, int transID, string key, string value){
	static char stdstring[100];
	string str;
	if (isCoordinator)
		str = "coordinator";
	else
		str = "server";
	sprintf(stdstring, "%s: create success at time %d, transID=%d, key=%s, value=%s", str.c_str(), par->getcurrtime(), transID, key.c_str(), value.c_str());
    LOG(address, stdstring);
}

/**
 * FUNCTION NAME: logReadSuccess
 *
 * DESCRIPTION: Call this function after successfully reading a key
 */
void Log::logReadSuccess(Address * address, bool isCoordinator, int transID, string key, string value){
    static char stdstring[100];
	string str;
	if (isCoordinator)
		str = "coordinator";
	else
		str = "server";
	sprintf(stdstring, "%s: read success at time %d, transID=%d, key=%s, value=%s", str.c_str(), par->getcurrtime(), transID, key.c_str(), value.c_str());
    LOG(address, stdstring);
}

/**
 * FUNCTION NAME: logUpdateSuccess
 *
 * DESCRIPTION: Call this function after successfully updating a key
 */
void Log::logUpdateSuccess(Address * address, bool isCoordinator, int transID, string key, string newValue){
    static char stdstring[100];
	string str;
	if (isCoordinator)
		str = "coordinator";
	else
		str = "server";
	sprintf(stdstring, "%s: update success at time %d, transID=%d, key=%s, value=%s", str.c_str(), par->getcurrtime(), transID, key.c_str(), newValue.c_str());
    LOG(address, stdstring);
}

/**
 * FUNCTION NAME: logDeleteSuccess
 *
 * DESCRIPTION: Call this function after successfully deleting a key
 */
void Log::logDeleteSuccess(Address * address, bool isCoordinator, int transID, string key){
    static char stdstring[100];
	string str;
	if (isCoordinator)
		str = "coordinator";
	else
		str = "server";
	sprintf(stdstring, "%s: delete success at time %d, transID=%d, key=%s", str.c_str(), par->getcurrtime(), transID, key.c_str());
    LOG(address, stdstring);
}

/**
 * FUNCTION NAME: logCreateFail
 *
 * DESCRIPTION: Call this function if CREATE failed
 */
void Log::logCreateFail(Address * address, bool isCoordinator, int transID, string key, string value){
	static char stdstring[100];
	string str;
	if (isCoordinator)
		str = "coordinator";
	else
		str = "server";
	sprintf(stdstring, "%s: create fail at time %d, transID=%d, key=%s, value=%s", str.c_str(), par->getcurrtime(), transID, key.c_str(), value.c_str());
    LOG(address, stdstring);
}


/**
 * FUNCTION NAME: logReadFail
 *
 * DESCRIPTION: Call this function if READ failed
 */
void Log::logReadFail(Address * address, bool isCoordinator, int transID, string key){
    static char stdstring[100];
	string str;
	if (isCoordinator)
		str = "coordinator";
	else
		str = "server";
	sprintf(stdstring, "%s: read fail at time %d, transID=%d, key=%s", str.c_str(), par->getcurrtime(), transID, key.c_str());
    LOG(address, stdstring);
}

/**
 * FUNCTION NAME: logUpdateFail
 *
 * DESCRIPTION: Call this function if UPDATE failed
 */
void Log::logUpdateFail(Address * address, bool isCoordinator, int transID, string key, string newValue){
    static char stdstring[100];
	string str;
	if (isCoordinator)
		str = "coordinator";
	else
		str = "server";
	sprintf(stdstring, "%s: update fail at time %d, transID=%d, key=%s, value=%s", str.c_str(), par->getcurrtime(), transID, key.c_str(), newValue.c_str());
    LOG(address, stdstring);
}

/**
 * FUNCTION NAME: logDeleteFail
 *
 * DESCRIPTION: Call this function if DELETE failed
 */
void Log::logDeleteFail(Address * address, bool isCoordinator, int transID, string key){
    static char stdstring[100];
	string str;
	if (isCoordinator)
		str = "coordinator";
	else
		str = "server";
	sprintf(stdstring, "%s: delete fail at time %d, transID=%d, key=%s", str.c_str(), par->getcurrtime(), transID, key.c_str());
    LOG(address, stdstring);
}
