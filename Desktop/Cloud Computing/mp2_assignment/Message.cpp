/**********************************
 * FILE NAME: Message.cpp
 *
 * DESCRIPTION: Message class definition
 **********************************/
#include "Message.h"

/**
 * Constructor
 */
// transID::fromAddr::CREATE::key::value::ReplicaType
// transID::fromAddr::READ::key
// transID::fromAddr::UPDATE::key::value::ReplicaType
// transID::fromAddr::DELETE::key
// transID::fromAddr::REPLY::sucess
// transID::fromAddr::READREPLY::value
Message::Message(string message){
	this->delimiter = "::";
	vector<string> tuple;
	size_t pos = message.find(delimiter);
	size_t start = 0;
	while (pos != string::npos) {
		string field = message.substr(start, pos-start);
		tuple.push_back(field);
		start = pos + 2;
		pos = message.find(delimiter, start);
	}
	tuple.push_back(message.substr(start));

	transID = stoi(tuple.at(0));
	Address addr(tuple.at(1));
	fromAddr = addr;
	type = static_cast<MessageType>(stoi(tuple.at(2)));
	switch(type){
		case CREATE:
		case UPDATE:
			key = tuple.at(3);
			value = tuple.at(4);
			if (tuple.size() > 5)
				replica = static_cast<ReplicaType>(stoi(tuple.at(5)));
			break;
		case READ:
		case DELETE:
			key = tuple.at(3);
			break;
		case REPLY:
			if (tuple.at(3) == "1")
				success = true;
			else
				success = false;
			break;
		case READREPLY:
			value = tuple.at(3);
			break;
	}
}

/**
 * Constructor
 */
// construct a create or update message
Message::Message(int _transID, Address _fromAddr, MessageType _type, string _key, string _value, ReplicaType _replica){
	this->delimiter = "::";
	transID = _transID;
	fromAddr = _fromAddr;
	type = _type;
	key = _key;
	value = _value;
	replica = _replica;
}

/**
 * Constructor
 */
Message::Message(const Message& anotherMessage) {
	this->delimiter = anotherMessage.delimiter;
	this->fromAddr = anotherMessage.fromAddr;
	this->key = anotherMessage.key;
	this->replica = anotherMessage.replica;
	this->success = anotherMessage.success;
	this->transID = anotherMessage.transID;
	this->type = anotherMessage.type;
	this->value = anotherMessage.value;
}

/**
 * Constructor
 */
Message::Message(int _transID, Address _fromAddr, MessageType _type, string _key, string _value){
	this->delimiter = "::";
	transID = _transID;
	fromAddr = _fromAddr;
	type = _type;
	key = _key;
	value = _value;
}

/**
 * Constructor
 */
// construct a read or delete message
Message::Message(int _transID, Address _fromAddr, MessageType _type, string _key){
	this->delimiter = "::";
	transID = _transID;
	fromAddr = _fromAddr;
	type = _type;
	key = _key;
}

/**
 * Constructor
 */
// construct reply message
Message::Message(int _transID, Address _fromAddr, MessageType _type, bool _success){
	this->delimiter = "::";
	transID = _transID;
	fromAddr = _fromAddr;
	type = _type;
	success = _success;
}

/**
 * Constructor
 */
// construct read reply message
Message::Message(int _transID, Address _fromAddr, string _value){
	this->delimiter = "::";
	transID = _transID;
	fromAddr = _fromAddr;
	type = READREPLY;
	value = _value;
}

/**
 * FUNCTION NAME: toString
 *
 * DESCRIPTION: Serialized Message in string format
 */
string Message::toString(){
	string message = to_string(transID) + delimiter + fromAddr.getAddress() + delimiter + to_string(type) + delimiter;
	switch(type){
		case CREATE:
		case UPDATE:
			message += key + delimiter + value + delimiter + to_string(replica);
			break;
		case READ:
		case DELETE:
			message += key;
			break;
		case REPLY:
			if (success)
				message += "1";
			else
				message += "0";
			break;
		case READREPLY:
			message += value;
			break;
	}
	return message;
}

/**
 * Assignment operator overloading
 */
Message& Message::operator =(const Message& anotherMessage) {
	this->delimiter = anotherMessage.delimiter;
	this->fromAddr = anotherMessage.fromAddr;
	this->key = anotherMessage.key;
	this->replica = anotherMessage.replica;
	this->success = anotherMessage.success;
	this->transID = anotherMessage.transID;
	this->type = anotherMessage.type;
	this->value = anotherMessage.value;
	return *this;
}
