/**********************************
 * FILE NAME: Node.h
 *
 * DESCRIPTION: Header file Node class
 **********************************/

#ifndef NODE_H_
#define NODE_H_

#include "stdincludes.h"
#include "Member.h"

class Node {
public:
	Address nodeAddress;
	size_t nodeHashCode;
	std::hash<string> hashFunc;
	Node();
	Node(Address address);
	Node(const Node& another);
	Node& operator=(const Node& another);
	bool operator < (const Node& another) const;
	void computeHashCode();
	size_t getHashCode();
	Address * getAddress();
	void setHashCode(size_t hashCode);
	void setAddress(Address address);
	virtual ~Node();
};

#endif /* NODE_H_ */
