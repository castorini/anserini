/**********************************
 * FILE NAME: EmulNet.cpp
 *
 * DESCRIPTION: Emulated Network classes definition
 **********************************/

#include "EmulNet.h"

/**
 * Constructor
 */
EmulNet::EmulNet(Params *p)
{
	//trace.funcEntry("EmulNet::EmulNet");
	int i,j;
	par = p;
	emulnet.setNextId(1);
	emulnet.settCurrBuffSize(0);
	enInited=0;
	for ( i = 0; i < MAX_NODES; i++ ) {
		for ( j = 0; j < MAX_TIME; j++ ) {
			sent_msgs[i][j] = 0;
			recv_msgs[i][j] = 0;
		}
	}
	//trace.funcExit("EmulNet::EmulNet", SUCCESS);
}

/**
 * Copy constructor
 */
EmulNet::EmulNet(EmulNet &anotherEmulNet) {
	int i, j;
	this->par = anotherEmulNet.par;
	this->enInited = anotherEmulNet.enInited;
	for ( i = 0; i < MAX_NODES; i++ ) {
		for ( j = 0; j < MAX_TIME; j++ ) {
			this->sent_msgs[i][j] = anotherEmulNet.sent_msgs[i][j];
			this->recv_msgs[i][j] = anotherEmulNet.recv_msgs[i][j];
		}
	}
	this->emulnet = anotherEmulNet.emulnet;
}

/**
 * Assignment operator overloading
 */
EmulNet& EmulNet::operator =(EmulNet &anotherEmulNet) {
	int i, j;
	this->par = anotherEmulNet.par;
	this->enInited = anotherEmulNet.enInited;
	for ( i = 0; i < MAX_NODES; i++ ) {
		for ( j = 0; j < MAX_TIME; j++ ) {
			this->sent_msgs[i][j] = anotherEmulNet.sent_msgs[i][j];
			this->recv_msgs[i][j] = anotherEmulNet.recv_msgs[i][j];
		}
	}
	this->emulnet = anotherEmulNet.emulnet;
	return *this;
}

/**
 * Destructor
 */
EmulNet::~EmulNet() {}

/**
 * FUNCTION NAME: ENinit
 *
 * DESCRIPTION: Init the emulnet for this node
 */
void *EmulNet::ENinit(Address *myaddr, short port) {
	// Initialize data structures for this member
	*(int *)(myaddr->addr) = emulnet.nextid++;
    *(short *)(&myaddr->addr[4]) = 0;
	return myaddr;
}

/**
 * FUNCTION NAME: ENsend
 *
 * DESCRIPTION: EmulNet send function
 *
 * RETURNS:
 * size
 */
int EmulNet::ENsend(Address *myaddr, Address *toaddr, char *data, int size) {
	en_msg *em;
	static char temp[2048];
	int sendmsg = rand() % 100;

	if( (emulnet.currbuffsize >= ENBUFFSIZE) || (size + (int)sizeof(en_msg) >= par->MAX_MSG_SIZE) || (par->dropmsg && sendmsg < (int) (par->MSG_DROP_PROB * 100)) ) {
		return 0;
	}

	em = (en_msg *)malloc(sizeof(en_msg) + size);
	em->size = size;

	memcpy(&(em->from.addr), &(myaddr->addr), sizeof(em->from.addr));
	memcpy(&(em->to.addr), &(toaddr->addr), sizeof(em->from.addr));
	memcpy(em + 1, data, size);

	emulnet.buff[emulnet.currbuffsize++] = em;

	int src = *(int *)(myaddr->addr);
	int time = par->getcurrtime();

	assert(src <= MAX_NODES);
	assert(time < MAX_TIME);

	sent_msgs[src][time]++;

	#ifdef DEBUGLOG
		sprintf(temp, "Sending 4+%d B msg type %d to %d.%d.%d.%d:%d ", size-4, *(int *)data, toaddr->addr[0], toaddr->addr[1], toaddr->addr[2], toaddr->addr[3], *(short *)&toaddr->addr[4]);
	#endif

	return size;
}

/**
 * FUNCTION NAME: ENsend
 *
 * DESCRIPTION: EmulNet send function
 *
 * RETURNS:
 * size
 */
int EmulNet::ENsend(Address *myaddr, Address *toaddr, string data) {
	char * str = (char *) malloc(data.length() * sizeof(char));
	memcpy(str, data.c_str(), data.size());
	int ret = this->ENsend(myaddr, toaddr, str, (data.length() * sizeof(char)));
	free(str);
	return ret;
}

/**
 * FUNCTION NAME: ENrecv
 *
 * DESCRIPTION: EmulNet receive function
 *
 * RETURN:
 * 0
 */
int EmulNet::ENrecv(Address *myaddr, int (* enq)(void *, char *, int), struct timeval *t, int times, void *queue){
	// times is always assumed to be 1
	int i;
	char* tmp;
	int sz;
	en_msg *emsg;

	for( i = emulnet.currbuffsize - 1; i >= 0; i-- ) {
		emsg = emulnet.buff[i];

		if ( 0 == strcmp(emsg->to.addr, myaddr->addr) ) {
			sz = emsg->size;
			tmp = (char *) malloc(sz * sizeof(char));
			memcpy(tmp, (char *)(emsg+1), sz);

			emulnet.buff[i] = emulnet.buff[emulnet.currbuffsize-1];
			emulnet.currbuffsize--;

			(*enq)(queue, (char *)tmp, sz);

			free(emsg);

			int dst = *(int *)(myaddr->addr);
			int time = par->getcurrtime();

			assert(dst <= MAX_NODES);
			assert(time < MAX_TIME);

			recv_msgs[dst][time]++;
		}
	}

	return 0;
}

/**
 * FUNCTION NAME: ENcleanup
 *
 * DESCRIPTION: Cleanup the EmulNet. Called exactly once at the end of the program.
 */
int EmulNet::ENcleanup() {
	emulnet.nextid=0;
	int i, j;
	int sent_total, recv_total;

	FILE* file = fopen("msgcount.log", "w+");

	while(emulnet.currbuffsize > 0) {
		free(emulnet.buff[--emulnet.currbuffsize]);
	}

	for ( i = 1; i <= par->EN_GPSZ; i++ ) {
		fprintf(file, "node %3d ", i);
		sent_total = 0;
		recv_total = 0;

		for (j = 0; j < par->getcurrtime(); j++) {

			sent_total += sent_msgs[i][j];
			recv_total += recv_msgs[i][j];
			if (i != 67) {
				fprintf(file, " (%4d, %4d)", sent_msgs[i][j], recv_msgs[i][j]);
				if (j % 10 == 9) {
					fprintf(file, "\n         ");
				}
			}
			else {
				fprintf(file, "special %4d %4d %4d\n", j, sent_msgs[i][j], recv_msgs[i][j]);
			}
		}
		fprintf(file, "\n");
		fprintf(file, "node %3d sent_total %6u  recv_total %6u\n\n", i, sent_total, recv_total);
	}

	fclose(file);
	return 0;
}
