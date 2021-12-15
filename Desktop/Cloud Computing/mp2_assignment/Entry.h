/**********************************
 * FILE NAME: Application.h
 *
 * DESCRIPTION: Header file Entry class
 **********************************/

#include "stdincludes.h"
#include "Message.h"

/**
 * CLASS NAME: Entry
 *
 * DESCRIPTION: This class describes the entry for each key in the DHT
 */
class Entry{
public:
	string value;
	int timestamp;
	ReplicaType replica;
	string delimiter;

	Entry(string entry);
	Entry(string _value, int _timestamp, ReplicaType _replica);
	string convertToString();
};
