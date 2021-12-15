/**********************************
 * FILE NAME: Member.cpp
 *
 * DESCRIPTION: Definition of all Member related class
 **********************************/

#include "Member.h"

/**
 * Constructor
 */
q_elt::q_elt(void *elt, int size): elt(elt), size(size) {}

/**
 * Copy constructor
 */
Address::Address(const Address &anotherAddress) {
	// strcpy(addr, anotherAddress.addr);
	memcpy(&addr, &anotherAddress.addr, sizeof(addr));
}

/**
 * Assignment operator overloading
 */
Address& Address::operator =(const Address& anotherAddress) {
	// strcpy(addr, anotherAddress.addr);
	memcpy(&addr, &anotherAddress.addr, sizeof(addr));
	return *this;
}

/**
 * Compare two Address objects
 */
bool Address::operator ==(const Address& anotherAddress) {
	return !memcmp(this->addr, anotherAddress.addr, sizeof(this->addr));
}

/**
 * Constructor
 */
MemberListEntry::MemberListEntry(int id, short port, long heartbeat, long timestamp): id(id), port(port), heartbeat(heartbeat), timestamp(timestamp) {}

/**
 * Constuctor
 */
MemberListEntry::MemberListEntry(int id, short port): id(id), port(port) {}

/**
 * Copy constructor
 */
MemberListEntry::MemberListEntry(const MemberListEntry &anotherMLE) {
	this->heartbeat = anotherMLE.heartbeat;
	this->id = anotherMLE.id;
	this->port = anotherMLE.port;
	this->timestamp = anotherMLE.timestamp;
}

/**
 * Assignment operator overloading
 */
MemberListEntry& MemberListEntry::operator =(const MemberListEntry &anotherMLE) {
	MemberListEntry temp(anotherMLE);
	swap(heartbeat, temp.heartbeat);
	swap(id, temp.id);
	swap(port, temp.port);
	swap(timestamp, temp.timestamp);
	return *this;
}

/**
 * FUNCTION NAME: getid
 *
 * DESCRIPTION: getter
 */
int MemberListEntry::getid() {
	return id;
}

/**
 * FUNCTION NAME: getport
 *
 * DESCRIPTION: getter
 */
short MemberListEntry::getport() {
	return port;
}

/**
 * FUNCTION NAME: getheartbeat
 *
 * DESCRIPTION: getter
 */
long MemberListEntry::getheartbeat() {
	return heartbeat;
}

/**
 * FUNCTION NAME: gettimestamp
 *
 * DESCRIPTION: getter
 */
long MemberListEntry::gettimestamp() {
	return timestamp;
}

/**
 * FUNCTION NAME: setid
 *
 * DESCRIPTION: setter
 */
void MemberListEntry::setid(int id) {
	this->id = id;
}

/**
 * FUNCTION NAME: setport
 *
 * DESCRIPTION: setter
 */
void MemberListEntry::setport(short port) {
	this->port = port;
}

/**
 * FUNCTION NAME: setheartbeat
 *
 * DESCRIPTION: setter
 */
void MemberListEntry::setheartbeat(long hearbeat) {
	this->heartbeat = hearbeat;
}

/**
 * FUNCTION NAME: settimestamp
 *
 * DESCRIPTION: setter
 */
void MemberListEntry::settimestamp(long timestamp) {
	this->timestamp = timestamp;
}

/**
 * Copy Constructor
 */
Member::Member(const Member &anotherMember) {
	this->addr = anotherMember.addr;
	this->inited = anotherMember.inited;
	this->inGroup = anotherMember.inGroup;
	this->bFailed = anotherMember.bFailed;
	this->nnb = anotherMember.nnb;
	this->heartbeat = anotherMember.heartbeat;
	this->pingCounter = anotherMember.pingCounter;
	this->timeOutCounter = anotherMember.timeOutCounter;
	this->memberList = anotherMember.memberList;
	this->myPos = anotherMember.myPos;
	this->mp1q = anotherMember.mp1q;
	this->mp2q = anotherMember.mp2q;
}

/**
 * Assignment operator overloading
 */
Member& Member::operator =(const Member& anotherMember) {
	this->addr = anotherMember.addr;
	this->inited = anotherMember.inited;
	this->inGroup = anotherMember.inGroup;
	this->bFailed = anotherMember.bFailed;
	this->nnb = anotherMember.nnb;
	this->heartbeat = anotherMember.heartbeat;
	this->pingCounter = anotherMember.pingCounter;
	this->timeOutCounter = anotherMember.timeOutCounter;
	this->memberList = anotherMember.memberList;
	this->myPos = anotherMember.myPos;
	this->mp1q = anotherMember.mp1q;
	this->mp2q = anotherMember.mp2q;
	return *this;
}
