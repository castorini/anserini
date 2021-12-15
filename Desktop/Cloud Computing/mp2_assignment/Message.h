/**********************************
 * FILE NAME: Message.h
 *
 * DESCRIPTION: Message class header file
 **********************************/
#ifndef MESSAGE_H_
#define MESSAGE_H_

#include "stdincludes.h"
#include "Member.h"
#include "common.h"

/**
 * CLASS NAME: Message
 *
 * DESCRIPTION: This class is used for message passing among nodes
 */
class Message{
public:
	MessageType type;
	ReplicaType replica;
	string key;
	string value;
	Address fromAddr;
	int transID;
	bool success; // success or not 
	// delimiter
	string delimiter;
	// construct a message from a string
	Message(string message);
	Message(const Message& anotherMessage);
	// construct a create or update message
	Message(int _transID, Address _fromAddr, MessageType _type, string _key, string _value);
	Message(int _transID, Address _fromAddr, MessageType _type, string _key, string _value, ReplicaType _replica);
	// construct a read or delete message
	Message(int _transID, Address _fromAddr, MessageType _type, string _key);
	// construct reply message
	Message(int _transID, Address _fromAddr, MessageType _type, bool _success);
	// construct read reply message
	Message(int _transID, Address _fromAddr, string _value);
	Message& operator = (const Message& anotherMessage);
	// serialize to a string
	string toString();
};

#endif
