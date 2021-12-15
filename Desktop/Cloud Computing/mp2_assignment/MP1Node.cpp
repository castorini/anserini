/**********************************
 * FILE NAME: MP1Node.cpp
 *
 * DESCRIPTION: Membership protocol run by this Node.
 * 				Definition of MP1Node class functions.
 **********************************/

#include "MP1Node.h"

/*
 * Note: You can change/add any functions in MP1Node.{h,cpp}
 */

/**
 * Overloaded Constructor of the MP1Node class
 * You can add new members to the class if you think it
 * is necessary for your logic to work
 */
MP1Node::MP1Node(Member *member, Params *params, EmulNet *emul, Log *log, Address *address) {
	for( int i = 0; i < 6; i++ ) {
		NULLADDR[i] = 0;
	}
	this->memberNode = member;
	this->emulNet = emul;
	this->log = log;
	this->par = params;
	this->memberNode->addr = *address;
}

/**
 * Destructor of the MP1Node class
 */
MP1Node::~MP1Node() {}

/**
 * FUNCTION NAME: recvLoop
 *
 * DESCRIPTION: This function receives message from the network and pushes into the queue
 * 				This function is called by a node to receive messages currently waiting for it
 */
int MP1Node::recvLoop() {
    if ( memberNode->bFailed ) {
    	return false;
    }
    else {
    	return emulNet->ENrecv(&(memberNode->addr), enqueueWrapper, NULL, 1, &(memberNode->mp1q));
    }
}

/**
 * FUNCTION NAME: enqueueWrapper
 *
 * DESCRIPTION: Enqueue the message from Emulnet into the queue
 */
int MP1Node::enqueueWrapper(void *env, char *buff, int size) {
	Queue q;
	return q.enqueue((queue<q_elt> *)env, (void *)buff, size);
}

/**
 * FUNCTION NAME: nodeStart
 *
 * DESCRIPTION: This function bootstraps the node
 * 				All initializations routines for a member.
 * 				Called by the application layer.
 */
void MP1Node::nodeStart(char *servaddrstr, short servport) {
    Address joinaddr;
    joinaddr = getJoinAddress();

    // Self booting routines
    if( initThisNode(&joinaddr) == -1 ) {
#ifdef DEBUGLOG
        log->LOG(&memberNode->addr, "init_thisnode failed. Exit.");
#endif
        exit(1);
    }

    if( !introduceSelfToGroup(&joinaddr) ) {
        finishUpThisNode();
#ifdef DEBUGLOG
        log->LOG(&memberNode->addr, "Unable to join self to group. Exiting.");
#endif
        exit(1);
    }

    return;
}

/**
 * FUNCTION NAME: initThisNode
 *
 * DESCRIPTION: Find out who I am and start up
 */
int MP1Node::initThisNode(Address *joinaddr) {
	/*
	 * This function is partially implemented and may require changes
	 */
	// int id = *(int*)(&memberNode->addr.addr);
	// int port = *(short*)(&memberNode->addr.addr[4]);

	memberNode->bFailed = false;
	memberNode->inited = true;
	memberNode->inGroup = false;
    // node is up!
	memberNode->nnb = 0;
	memberNode->heartbeat = 0;
	memberNode->pingCounter = TFAIL;
	memberNode->timeOutCounter = -1;
    initMemberListTable(memberNode);

    return 0;
}

/**
 * FUNCTION NAME: introduceSelfToGroup
 *
 * DESCRIPTION: Join the distributed system
 */
int MP1Node::introduceSelfToGroup(Address *joinaddr) {
	MessageHdr *msg;
#ifdef DEBUGLOG
    static char s[1024];
#endif

    if ( 0 == memcmp((char *)&(memberNode->addr.addr), (char *)&(joinaddr->addr), sizeof(memberNode->addr.addr))) {
        // I am the group booter (first process to join the group). Boot up the group
#ifdef DEBUGLOG
        log->LOG(&memberNode->addr, "Starting up group...");
#endif
        memberNode->inGroup = true;
    }
    else {
        msg = new MessageHdr();
        memset(&msg->addr, 0, sizeof(msg->addr));
        // size_t msgsize = sizeof(MessageHdr) + sizeof(joinaddr->addr) + sizeof(long) + 1;
        // msg = (MessageHdr *) malloc(msgsize * sizeof(char));

        // create JOINREQ message: format of data is {struct Address myaddr}
        msg->msgType = JOINREQ;
        memcpy(&msg->addr, &memberNode->addr, sizeof(Address));

        // memcpy((char *)(msg+1), &memberNode->addr.addr, sizeof(memberNode->addr.addr));
        // memcpy((char *)(msg+1) + 1 + sizeof(memberNode->addr.addr), &memberNode->heartbeat, sizeof(long));

#ifdef DEBUGLOG
        sprintf(s, "Trying to join...");
        log->LOG(&memberNode->addr, s);
#endif

        // send JOINREQ message to introducer member
        emulNet->ENsend(&memberNode->addr, joinaddr, (char *)msg, sizeof(MessageHdr));

        free(msg);
    }

    return 1;

}

/**
 * FUNCTION NAME: finishUpThisNode
 *
 * DESCRIPTION: Wind up this node and clean up state
 */
int MP1Node::finishUpThisNode(){
   /*
    * Your code goes here
    */
   return 0;
}

/**
 * FUNCTION NAME: nodeLoop
 *
 * DESCRIPTION: Executed periodically at each member
 * 				Check your messages in queue and perform membership protocol duties
 */
void MP1Node::nodeLoop() {
    if (memberNode->bFailed) {
    	return;
    }

    // Check my messages
    checkMessages();

    // Wait until you're in the group...
    if( !memberNode->inGroup ) {
    	return;
    }

    // ...then jump in and share your responsibilites!
    nodeLoopOps();

    return;
}

/**
 * FUNCTION NAME: checkMessages
 *
 * DESCRIPTION: Check messages in the queue and call the respective message handler
 */
void MP1Node::checkMessages() {
    void *ptr;
    int size;

    // Pop waiting messages from memberNode's mp1q
    while ( !memberNode->mp1q.empty() ) {
    	ptr = memberNode->mp1q.front().elt;
    	size = memberNode->mp1q.front().size;
    	memberNode->mp1q.pop();
    	recvCallBack((void *)memberNode, (char *)ptr, size);
    }
    return;
}

/**
 * FUNCTION NAME: recvCallBack
 *
 * DESCRIPTION: Message handler for different message types
 */
bool MP1Node::recvCallBack(void *env, char *data, int size) {
	/*
	 * Your code goes here
	 */


    MessageHdr *msg = (MessageHdr *) data;
    if (msg->msgType == MsgTypes::PING) {
        pingMessage(msg);
        delete msg;
        return true;
    }
    
    if (msg->msgType == MsgTypes::JOINREQ) {
        MessageHdr *replyMsg = spawnMessage(MsgTypes::JOINREP);
        emulNet->ENsend(&memberNode->addr, &msg->addr, (char *) replyMsg, sizeof(MessageHdr));
    } else if (msg->msgType == MsgTypes::JOINREP) {
        memberNode->inGroup = true;
    }

    appendMember(getId(&msg->addr), getPort(&msg->addr), &msg->addr);

    delete msg;
    return true;
}

int MP1Node::getId(Address *addr) {
    return *(int*)(&addr->addr);
}

short MP1Node::getPort(Address *addr) {
    return *(short*)(&addr->addr[4]);
}

void MP1Node::pingMessage(MessageHdr *msg) {
    // update the message source node entry
    MemberListEntry *localCopy = findMember(&msg->addr);
    if (localCopy == nullptr) {
        appendMember(getId(&msg->addr), getPort(&msg->addr), &msg->addr);
    } else {
        localCopy->heartbeat += 1;
        localCopy->timestamp = par->getcurrtime();
    }

    // update the other node entries carried by the message
    for (size_t i = 0; i < msg->memberSize; i++) {
        Address *currAddr = retrieveAddress(msg->carried_members[i].id, msg->carried_members[i].port);
        MemberListEntry *localEntry = findMember(currAddr);
        if (localEntry != nullptr && msg->carried_members[i].heartbeat > localEntry->heartbeat) {
            localEntry->timestamp = par->getcurrtime();
            localEntry->heartbeat = msg->carried_members[i].heartbeat;
        } else {
            appendMember(msg->carried_members[i].id, msg->carried_members[i].port, currAddr, msg->carried_members[i].heartbeat, msg->carried_members[i].timestamp);
        }
        delete currAddr;
    }
}

MessageHdr* MP1Node::spawnMessage(MsgTypes type) {
    MessageHdr *msg = new MessageHdr();
    msg->msgType = type;
    if (memberNode->memberList.size() > 0) {
        msg->carried_members = new MemberListEntry[memberNode->memberList.size()];
        memcpy(msg->carried_members, memberNode->memberList.data(), sizeof(MemberListEntry) * memberNode->memberList.size());
        msg->memberSize = memberNode->memberList.size();
    }
    memcpy(&msg->addr, &memberNode->addr, sizeof(Address));
    return msg;
}

void MP1Node::appendMember(int id, short port, Address *addr, long heartbeat, long timestamp) {
    // std::cout << "appending\n" << std::endl;
    // log->LOG(&memberNode->addr, " appending");
    if (*addr == memberNode->addr) { 
        // log->LOG(&memberNode->addr, " appending Failed same addr");
        return; 
    }
    if (findMember(addr) != nullptr) {
        // log->LOG(&memberNode->addr, " appending Failed already in");
        // log->LOG(&memberNode->addr, addr->addr);
        return;
    }
    // if (findMember(addr) != nullptr) return;
    
    if (timestamp != -1 && par->getcurrtime() - timestamp >= TREMOVE) {
        // log->LOG(&memberNode->addr, " appending Failed entry too old");
        return;
    }
    log->logNodeAdd(&memberNode->addr, addr);
    
    MemberListEntry *entry = new MemberListEntry(id, port, heartbeat, par->getcurrtime());
    memberNode->memberList.push_back(*entry);
    log->LOG(&memberNode->addr, " appended");
}

MemberListEntry* MP1Node::findMember(Address *addr) {
    for (size_t i = 0; i < memberNode->memberList.size(); i++) {
        MemberListEntry* currMember = memberNode->memberList.data() + i;
        //if (currMember->id == getId(addr) && currMember->port == getPort(addr)) return currMember;
        Address *currAddr = retrieveAddress(currMember->id, currMember->port);
        if (*currAddr == *addr) {
            delete currAddr;
            return currMember;
        }
        delete currAddr;
    }
    return nullptr;
}

Address* MP1Node::retrieveAddress(int id, short port) {
    Address *address = new Address();
    memset(address->addr, 0, sizeof(address->addr));
    memcpy(address->addr, &id, sizeof(int));
    memcpy(address->addr + sizeof(int), &port, sizeof(short));
    return address;
}

/**
 * FUNCTION NAME: nodeLoopOps
 *
 * DESCRIPTION: Check if any node hasn't responded within a timeout period and then delete
 * 				the nodes
 * 				Propagate your membership list
 */
void MP1Node::nodeLoopOps() {
	/*
	 * Your code goes here
	 */

    // remove timeout members and ping all neighbours
    MessageHdr *msg = spawnMessage(MsgTypes::PING);
    for (size_t i = 0; i < memberNode->memberList.size(); i++) {
        MemberListEntry *curr = memberNode->memberList.data() + i;
        Address *currAddr = retrieveAddress(curr->id, curr->port);
        if (par->getcurrtime() - curr->timestamp >= TREMOVE) {
            //remove
            memberNode->memberList.erase(memberNode->memberList.begin() + i);
            log->logNodeRemove(&memberNode->addr, currAddr);
        } else {
            // send a ping
            emulNet->ENsend(&memberNode->addr, currAddr, (char *) msg, sizeof(MessageHdr));
        }
        delete currAddr;
    }
    delete msg;
    return;
}

/**
 * FUNCTION NAME: isNullAddress
 *
 * DESCRIPTION: Function checks if the address is NULL
 */
int MP1Node::isNullAddress(Address *addr) {
	return (memcmp(addr->addr, NULLADDR, 6) == 0 ? 1 : 0);
}

/**
 * FUNCTION NAME: getJoinAddress
 *
 * DESCRIPTION: Returns the Address of the coordinator
 */
Address MP1Node::getJoinAddress() {
    Address joinaddr;

    memset(&joinaddr, 0, sizeof(Address));
    *(int *)(&joinaddr.addr) = 1;
    *(short *)(&joinaddr.addr[4]) = 0;

    return joinaddr;
}

/**
 * FUNCTION NAME: initMemberListTable
 *
 * DESCRIPTION: Initialize the membership list
 */
void MP1Node::initMemberListTable(Member *memberNode) {
	memberNode->memberList.clear();
}

/**
 * FUNCTION NAME: printAddress
 *
 * DESCRIPTION: Print the Address
 */
void MP1Node::printAddress(Address *addr)
{
    printf("%d.%d.%d.%d:%d \n",  addr->addr[0],addr->addr[1],addr->addr[2],
                                                       addr->addr[3], *(short*)&addr->addr[4]) ;    
}
