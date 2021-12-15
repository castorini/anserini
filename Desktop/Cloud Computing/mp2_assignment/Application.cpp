/**********************************
 * FILE NAME: Application.cpp
 *
 * DESCRIPTION: Application layer class function definitions
 **********************************/

#include "Application.h"

void handler(int sig) {
	void *array[10];
	size_t size;

	// get void*'s for all entries on the stack
	size = backtrace(array, 10);

	// print out all the frames to stderr
	fprintf(stderr, "Error: signal %d:\n", sig);
	backtrace_symbols_fd(array, size, STDERR_FILENO);
	exit(1);
}

/**********************************
 * FUNCTION NAME: main
 *
 * DESCRIPTION: main function. Start from here
 **********************************/
int main(int argc, char *argv[]) {
	//signal(SIGSEGV, handler);
	if ( argc != ARGS_COUNT ) {
		cout<<"Configuration (i.e., *.conf) file File Required"<<endl;
		return FAILURE;
	}

	// Create a new application object
	Application *app = new Application(argv[1]);
	// Call the run function
	app->run();
	// When done delete the application object
	delete(app);

	return SUCCESS;
}

/**
 * Constructor of the Application class
 */
Application::Application(char *infile) {
	int i;
	par = new Params();
	srand (time(NULL));
	par->setparams(infile);
	log = new Log(par);
	en = new EmulNet(par);
	en1 = new EmulNet(par);
	mp1 = (MP1Node **) malloc(par->EN_GPSZ * sizeof(MP1Node *));
	mp2 = (MP2Node **) malloc(par->EN_GPSZ * sizeof(MP2Node *));

	/*
	 * Init all nodes
	 */
	for( i = 0; i < par->EN_GPSZ; i++ ) {
		Member *memberNode = new Member;
		memberNode->inited = false;
		Address *addressOfMemberNode = new Address();
		Address joinaddr;
		joinaddr = getjoinaddr();
		addressOfMemberNode = (Address *) en->ENinit(addressOfMemberNode, par->PORTNUM);
		mp1[i] = new MP1Node(memberNode, par, en, log, addressOfMemberNode);
		mp2[i] = new MP2Node(memberNode, par, en1, log, addressOfMemberNode);
		log->LOG(&(mp1[i]->getMemberNode()->addr), "APP");
		log->LOG(&(mp2[i]->getMemberNode()->addr), "APP MP2");
		delete addressOfMemberNode;
	}
}

/**
 * Destructor
 */
Application::~Application() {
	delete log;
	delete en;
	delete en1;
	for ( int i = 0; i < par->EN_GPSZ; i++ ) {
		delete mp1[i];
		delete mp2[i];
	}
	free(mp1);
	free(mp2);
	delete par;
}

/**
 * FUNCTION NAME: run
 *
 * DESCRIPTION: Main driver function of the Application layer
 */
int Application::run()
{
	int i;
	int timeWhenAllNodesHaveJoined = 0;
	// boolean indicating if all nodes have joined
	bool allNodesJoined = false;
	srand(time(NULL));

	// As time runs along
	for( par->globaltime = 0; par->globaltime < TOTAL_RUNNING_TIME; ++par->globaltime ) {
		// Run the membership protocol
		mp1Run();

		// Wait for all nodes to join
		if ( par->allNodesJoined == nodeCount && !allNodesJoined ) {
			timeWhenAllNodesHaveJoined = par->getcurrtime();
			allNodesJoined = true;
		}
		if ( par->getcurrtime() > timeWhenAllNodesHaveJoined + 50 ) {
			// Call the KV store functionalities
			mp2Run();
		}
		// Fail some nodes
		//fail();
	}

	// Clean up
	en->ENcleanup();
	en1->ENcleanup();

	for(i=0;i<=par->EN_GPSZ-1;i++) {
		 mp1[i]->finishUpThisNode();
	}

	return SUCCESS;
}

/**
 * FUNCTION NAME: mp1Run
 *
 * DESCRIPTION:	This function performs all the membership protocol functionalities
 */
void Application::mp1Run() {
	int i;

	// For all the nodes in the system
	for( i = 0; i <= par->EN_GPSZ-1; i++) {

		/*
		 * Receive messages from the network and queue them in the membership protocol queue
		 */
		if( par->getcurrtime() > (int)(par->STEP_RATE*i) && !(mp1[i]->getMemberNode()->bFailed) ) {
			// Receive messages from the network and queue them
			mp1[i]->recvLoop();
		}

	}

	// For all the nodes in the system
	for( i = par->EN_GPSZ - 1; i >= 0; i-- ) {

		/*
		 * Introduce nodes into the distributed system
		 */
		if( par->getcurrtime() == (int)(par->STEP_RATE*i) ) {
			// introduce the ith node into the system at time STEPRATE*i
			mp1[i]->nodeStart(JOINADDR, par->PORTNUM);
			cout<<i<<"-th introduced node is assigned with the address: "<<mp1[i]->getMemberNode()->addr.getAddress() << endl;
			nodeCount += i;
		}

		/*
		 * Handle all the messages in your queue and send heartbeats
		 */
		else if( par->getcurrtime() > (int)(par->STEP_RATE*i) && !(mp1[i]->getMemberNode()->bFailed) ) {
			// handle messages and send heartbeats
			mp1[i]->nodeLoop();
			#ifdef DEBUGLOG
			if( (i == 0) && (par->globaltime % 500 == 0) ) {
				log->LOG(&mp1[i]->getMemberNode()->addr, "@@time=%d", par->getcurrtime());
			}
			#endif
		}

	}
}

/**
 * FUNCTION NAME: mp2Run
 *
 * DESCRIPTION: This function performs all the key value store related functionalities
 * 				including:
 * 				1) Ring operations
 * 				2) CRUD operations
 */
void Application::mp2Run() {
	int i;

	// For all the nodes in the system
	for( i = 0; i <= par->EN_GPSZ-1; i++) {

		/*
		 * 1) Update the ring
		 * 2) Receive messages from the network and queue them in the KV store queue
		 */
		if ( par->getcurrtime() > (int)(par->STEP_RATE*i) && !mp2[i]->getMemberNode()->bFailed ) {
			if ( mp2[i]->getMemberNode()->inited && mp2[i]->getMemberNode()->inGroup ) {
				// Step 1
				mp2[i]->updateRing();
			}
			// Step 2
			mp2[i]->recvLoop();
		}
	}

	/**
	 * Handle messages from the queue and update the DHT
	 */
	for ( i = par->EN_GPSZ-1; i >= 0; i-- ) {
		if ( par->getcurrtime() > (int)(par->STEP_RATE*i) && !mp2[i]->getMemberNode()->bFailed ) {
			mp2[i]->checkMessages();
		}
	}

	/**
	 * Insert a set of test key value pairs into the system
	 */
	if ( par->getcurrtime() == INSERT_TIME ) {
		insertTestKVPairs();
	}

	/**
	 * Test CRUD operations
	 */
	if ( par->getcurrtime() >= TEST_TIME ) {
		/**************
		 * CREATE TEST
		 **************/
		/**
		 * TEST 1: Checks if there are RF * NUMBER_OF_INSERTS CREATE SUCCESS message are in the log
		 *
		 */
		if ( par->getcurrtime() == TEST_TIME && CREATE_TEST == par->CRUDTEST ) {
			cout<<endl<<"Doing create test at time: "<<par->getcurrtime()<<endl;
		} // End of create test

		/***************
		 * DELETE TESTS
		 ***************/
		/**
		 * TEST 1: NUMBER_OF_INSERTS/2 Key Value pair are deleted.
		 * 		   Check whether RF * NUMBER_OF_INSERTS/2 DELETE SUCCESS message are in the log
		 * TEST 2: Delete a non-existent key. Check for a DELETE FAIL message in the lgo
		 *
		 */
		else if ( par->getcurrtime() == TEST_TIME && DELETE_TEST == par->CRUDTEST ) {
			deleteTest();
		} // End of delete test

		/*************
		 * READ TESTS
		 *************/
		/**
		 * TEST 1: Read a key. Check for correct value being read in quorum of replicas
		 *
		 * Wait for some time after TEST 1
		 *
		 * TEST 2: Fail a single replica of a key. Check for correct value of the key
		 * 		   being read in quorum of replicas
		 *
		 * Wait for STABILIZE_TIME after TEST 2 (stabilization protocol should ensure at least
		 * 3 replicas for all keys at all times)
		 *
		 * TEST 3 part 1: Fail two replicas of a key. Read the key and check for READ FAIL message in the log.
		 * 				  READ should fail because quorum replicas of the key are not up
		 *
		 * Wait for another STABILIZE_TIME after TEST 3 part 1 (stabilization protocol should ensure at least
		 * 3 replicas for all keys at all times)
		 *
		 * TEST 3 part 2: Read the same key as TEST 3 part 1. Check for correct value of the key
		 * 		  		  being read in quorum of replicas
		 *
		 * Wait for some time after TEST 3 part 2
		 *
		 * TEST 4: Fail a non-replica. Check for correct value of the key
		 * 		   being read in quorum of replicas
		 *
		 * TEST 5: Read a non-existent key. Check for a READ FAIL message in the log
		 *
		 */
		else if ( par->getcurrtime() >= TEST_TIME && READ_TEST == par->CRUDTEST ) {
			readTest();
		} // end of read test

		/***************
		 * UPDATE TESTS
		 ***************/
		/**
		 * TEST 1: Update a key. Check for correct new value being updated in quorum of replicas
		 *
		 * Wait for some time after TEST 1
		 *
		 * TEST 2: Fail a single replica of a key. Update the key. Check for correct new value of the key
		 * 		   being updated in quorum of replicas
		 *
		 * Wait for STABILIZE_TIME after TEST 2 (stabilization protocol should ensure at least
		 * 3 replicas for all keys at all times)
		 *
		 * TEST 3 part 1: Fail two replicas of a key. Update the key and check for READ FAIL message in the log
		 * 				  UPDATE should fail because quorum replicas of the key are not up
		 *
		 * Wait for another STABILIZE_TIME after TEST 3 part 1 (stabilization protocol should ensure at least
		 * 3 replicas for all keys at all times)
		 *
		 * TEST 3 part 2: Update the same key as TEST 3 part 1. Check for correct new value of the key
		 * 		   		  being update in quorum of replicas
		 *
		 * Wait for some time after TEST 3 part 2
		 *
		 * TEST 4: Fail a non-replica. Check for correct new value of the key
		 * 		   being updated in quorum of replicas
		 *
		 * TEST 5: Update a non-existent key. Check for a UPDATE FAIL message in the log
		 *
		 */
		else if ( par->getcurrtime() >= TEST_TIME && UPDATE_TEST == par->CRUDTEST ) {
			updateTest();
		} // End of update test

	} // end of if ( par->getcurrtime == TEST_TIME)
}

/**
 * FUNCTION NAME: fail
 *
 * DESCRIPTION: This function controls the failure of nodes
 *
 * Note: this is used only by MP1
 */
void Application::fail() {
	int i, removed;

	// fail half the members at time t=400
	if( par->DROP_MSG && par->getcurrtime() == 50 ) {
		par->dropmsg = 1;
	}

	if( par->SINGLE_FAILURE && par->getcurrtime() == 100 ) {
		removed = (rand() % par->EN_GPSZ);
		#ifdef DEBUGLOG
		log->LOG(&mp1[removed]->getMemberNode()->addr, "Node failed at time=%d", par->getcurrtime());
		#endif
		mp1[removed]->getMemberNode()->bFailed = true;
	}
	else if( par->getcurrtime() == 100 ) {
		removed = rand() % par->EN_GPSZ/2;
		for ( i = removed; i < removed + par->EN_GPSZ/2; i++ ) {
			#ifdef DEBUGLOG
			log->LOG(&mp1[i]->getMemberNode()->addr, "Node failed at time = %d", par->getcurrtime());
			#endif
			mp1[i]->getMemberNode()->bFailed = true;
		}
	}

	if( par->DROP_MSG && par->getcurrtime() == 300) {
		par->dropmsg=0;
	}

}

/**
 * FUNCTION NAME: getjoinaddr
 *
 * DESCRIPTION: This function returns the address of the coordinator
 */
Address Application::getjoinaddr(void){
	//trace.funcEntry("Application::getjoinaddr");
    Address joinaddr;
    joinaddr.init();
    *(int *)(&(joinaddr.addr))=1;
    *(short *)(&(joinaddr.addr[4]))=0;
    //trace.funcExit("Application::getjoinaddr", SUCCESS);
    return joinaddr;
}

/**
 * FUNCTION NAME: findARandomNodeThatIsAlive
 *
 * DESCRTPTION: Finds a random node in the ring that is alive
 */
int Application::findARandomNodeThatIsAlive() {
	int number;
	do {
		number = (rand()%par->EN_GPSZ);
	}while (mp2[number]->getMemberNode()->bFailed);
	return number;
}

/**
 * FUNCTION NAME: initTestKVPairs
 *
 * DESCRIPTION: Init NUMBER_OF_INSERTS test KV pairs in the map
 */
void Application::initTestKVPairs() {
	srand(time(NULL));
	int i;
	string key;
	key.clear();
	testKVPairs.clear();
	int alphanumLen = sizeof(alphanum) - 1;
	while ( testKVPairs.size() != NUMBER_OF_INSERTS ) {
		for ( i = 0; i < KEY_LENGTH; i++ ) {
			key.push_back(alphanum[rand()%alphanumLen]);
		}
		string value = "value" + to_string(rand()%NUMBER_OF_INSERTS);
		testKVPairs[key] = value;
		key.clear();
	}
}

/**
 * FUNCTION NAME: insertTestKVPairs
 *
 * DESCRIPTION: This function inserts test KV pairs into the system
 */
void Application::insertTestKVPairs() {
	int number = 0;

	/*
	 * Init a few test key value pairs
	 */
	initTestKVPairs();

	for ( map<string, string>::iterator it = testKVPairs.begin(); it != testKVPairs.end(); ++it ) {
		// Step 1. Find a node that is alive
		number = findARandomNodeThatIsAlive();

		// Step 2. Issue a create operation
		log->LOG(&mp2[number]->getMemberNode()->addr, "CREATE OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), it->second.c_str(), par->getcurrtime());
		mp2[number]->clientCreate(it->first, it->second);
	}

	cout<<endl<<"Sent " <<testKVPairs.size() <<" create messages to the ring"<<endl;
}

/**
 * FUNCTION NAME: deleteTest
 *
 * DESCRIPTION: Test the delete API of the KV store
 */
void Application::deleteTest() {
	int number;
	/**
	 * Test 1: Delete half the KV pairs
	 */
	cout<<endl<<"Deleting "<<testKVPairs.size()/2 <<" valid keys.... ... .. . ."<<endl;
	map<string, string>::iterator it = testKVPairs.begin();
	for ( int i = 0; i < testKVPairs.size()/2; i++ ) {
		it++;

		// Step 1.a. Find a node that is alive
		number = findARandomNodeThatIsAlive();

		// Step 1.b. Issue a delete operation
		log->LOG(&mp2[number]->getMemberNode()->addr, "DELETE OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), it->second.c_str(), par->getcurrtime());
		mp2[number]->clientDelete(it->first);
	}

	/**
	 * Test 2: Delete a non-existent key
	 */
	cout<<endl<<"Deleting an invalid key.... ... .. . ."<<endl;
	string invalidKey = "invalidKey";
	// Step 2.a. Find a node that is alive
	number = findARandomNodeThatIsAlive();

	// Step 2.b. Issue a delete operation
	log->LOG(&mp2[number]->getMemberNode()->addr, "DELETE OPERATION KEY: %s at time: %d", invalidKey.c_str(), par->getcurrtime());
	mp2[number]->clientDelete(invalidKey);
}

/**
 * FUNCTION NAME: readTest
 *
 * DESCRIPTION: Test the read API of the KV store
 */
void Application::readTest() {

	// Step 0. Key to be read
	// This key is used for all read tests
	map<string, string>::iterator it = testKVPairs.begin();
	int number;
	vector<Node> replicas;
	int replicaIdToFail = TERTIARY;
	int nodeToFail;
	bool failedOneNode = false;

	/**
 	 * Test 1: Test if value of a single read operation is read correctly in quorum number of nodes
 	 */
	if ( par->getcurrtime() == TEST_TIME ) {
		// Step 1.a. Find a node that is alive
		number = findARandomNodeThatIsAlive();

		// Step 1.b Do a read operation
		cout<<endl<<"Reading a valid key.... ... .. . ."<<endl;
		log->LOG(&mp2[number]->getMemberNode()->addr, "READ OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), it->second.c_str(), par->getcurrtime());
		mp2[number]->clientRead(it->first);
	}

	/** end of test1 **/

	/**
	 * Test 2: FAIL ONE REPLICA. Test if value is read correctly in quorum number of nodes after ONE OF THE REPLICAS IS FAILED
	 */
	if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME) ) {
		// Step 2.a Find a node that is alive and assign it as number
		number = findARandomNodeThatIsAlive();

		// Step 2.b Find the replicas of this key
		replicas.clear();
		replicas = mp2[number]->findNodes(it->first);
		// if less than quorum replicas are found then exit
		if ( replicas.size() < (RF-1) ) {
			cout<<endl<<"Could not find at least quorum replicas for this key. Exiting!!! size of replicas vector: "<<replicas.size()<<endl;
			log->LOG(&mp2[number]->getMemberNode()->addr, "Could not find at least quorum replicas for this key. Exiting!!! size of replicas vector: %d", replicas.size());
			exit(1);
		}

		// Step 2.c Fail a replica
		for ( int i = 0; i < par->EN_GPSZ; i++ ) {
			if ( mp2[i]->getMemberNode()->addr.getAddress() == replicas.at(replicaIdToFail).getAddress()->getAddress() ) {
				if ( !mp2[i]->getMemberNode()->bFailed ) {
					nodeToFail = i;
					failedOneNode = true;
					break;
				}
				else {
					// Since we fail at most two nodes, one of the replicas must be alive
					if ( replicaIdToFail > 0 ) {
						replicaIdToFail--;
					}
					else {
						failedOneNode = false;
					}
				}
			}
		}
		if ( failedOneNode ) {
			log->LOG(&mp2[nodeToFail]->getMemberNode()->addr, "Node failed at time=%d", par->getcurrtime());
			mp2[nodeToFail]->getMemberNode()->bFailed = true;
			mp1[nodeToFail]->getMemberNode()->bFailed = true;
			cout<<endl<<"Failed a replica node"<<endl;
		}
		else {
			// The code can never reach here
			log->LOG(&mp2[number]->getMemberNode()->addr, "Could not fail a node");
			cout<<"Could not fail a node. Exiting!!!";
			exit(1);
		}

		number = findARandomNodeThatIsAlive();

		// Step 2.d Issue a read
		cout<<endl<<"Reading a valid key.... ... .. . ."<<endl;
		log->LOG(&mp2[number]->getMemberNode()->addr, "READ OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), it->second.c_str(), par->getcurrtime());
		mp2[number]->clientRead(it->first);

		failedOneNode = false;
	}

	/** end of test 2 **/

	/**
	 * Test 3 part 1: Fail two replicas. Test if value is read correctly in quorum number of nodes after TWO OF THE REPLICAS ARE FAILED
	 */
	// Wait for STABILIZE_TIME and fail two replicas
	if ( par->getcurrtime() >= (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME) ) {
		vector<int> nodesToFail;
		nodesToFail.clear();
		int count = 0;

		if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME) ) {
			// Step 3.a. Find a node that is alive
			number = findARandomNodeThatIsAlive();

			// Get the keys replicas
			replicas.clear();
			replicas = mp2[number]->findNodes(it->first);

			// Step 3.b. Fail two replicas
			//cout<<"REPLICAS SIZE: "<<replicas.size();
			if ( replicas.size() > 2 ) {
				replicaIdToFail = TERTIARY;
				while ( count != 2 ) {
					int i = 0;
					while ( i != par->EN_GPSZ ) {
						if ( mp2[i]->getMemberNode()->addr.getAddress() == replicas.at(replicaIdToFail).getAddress()->getAddress() ) {
							if ( !mp2[i]->getMemberNode()->bFailed ) {
								nodesToFail.emplace_back(i);
								replicaIdToFail--;
								count++;
								break;
							}
							else {
								// Since we fail at most two nodes, one of the replicas must be alive
								if ( replicaIdToFail > 0 ) {
									replicaIdToFail--;
								}
							}
						}
						i++;
					}
				}
			}
			else {
				// If the code reaches here. Test your stabilization protocol
				cout<<endl<<"Not enough replicas to fail two nodes. Number of replicas of this key: " <<replicas.size() <<". Exiting test case !! "<<endl;
				exit(1);
			}
			if ( count == 2 ) {
				for ( int i = 0; i < nodesToFail.size(); i++ ) {
					// Fail a node
					log->LOG(&mp2[nodesToFail.at(i)]->getMemberNode()->addr, "Node failed at time=%d", par->getcurrtime());
					mp2[nodesToFail.at(i)]->getMemberNode()->bFailed = true;
					mp1[nodesToFail.at(i)]->getMemberNode()->bFailed = true;
					cout<<endl<<"Failed a replica node"<<endl;
				}
			}
			else {
				// The code can never reach here
				log->LOG(&mp2[number]->getMemberNode()->addr, "Could not fail two nodes");
				//cout<<"COUNT: " <<count;
				cout<<"Could not fail two nodes. Exiting!!!";
				exit(1);
			}

			number = findARandomNodeThatIsAlive();

			// Step 3.c Issue a read
			cout<<endl<<"Reading a valid key.... ... .. . ."<<endl;
			log->LOG(&mp2[number]->getMemberNode()->addr, "READ OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), it->second.c_str(), par->getcurrtime());
			// This read should fail since at least quorum nodes are not alive
			mp2[number]->clientRead(it->first);
		}

		/**
		 * TEST 3 part 2: After failing two replicas and waiting for STABILIZE_TIME, issue a read
		 */
		// Step 3.d Wait for stabilization protocol to kick in
		if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME + STABILIZE_TIME) ) {
			number = findARandomNodeThatIsAlive();
			// Step 3.e Issue a read
			cout<<endl<<"Reading a valid key.... ... .. . ."<<endl;
			log->LOG(&mp2[number]->getMemberNode()->addr, "READ OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), it->second.c_str(), par->getcurrtime());
			// This read should be successful
			mp2[number]->clientRead(it->first);
		}
	}

	/** end of test 3 **/

	/**
	 * Test 4: FAIL A NON-REPLICA. Test if value is read correctly in quorum number of nodes after a NON-REPLICA IS FAILED
	 */
	if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME + STABILIZE_TIME + LAST_FAIL_TIME ) ) {
		// Step 4.a. Find a node that is alive
		number = findARandomNodeThatIsAlive();

		// Step 4.b Find a non - replica for this key
		replicas.clear();
		replicas = mp2[number]->findNodes(it->first);
		for ( int i = 0; i < par->EN_GPSZ; i++ ) {
			if ( !mp2[i]->getMemberNode()->bFailed ) {
				if ( mp2[i]->getMemberNode()->addr.getAddress() != replicas.at(PRIMARY).getAddress()->getAddress() &&
					 mp2[i]->getMemberNode()->addr.getAddress() != replicas.at(SECONDARY).getAddress()->getAddress() &&
					 mp2[i]->getMemberNode()->addr.getAddress() != replicas.at(TERTIARY).getAddress()->getAddress() ) {
					// Step 4.c Fail a non-replica node
					log->LOG(&mp2[i]->getMemberNode()->addr, "Node failed at time=%d", par->getcurrtime());
					mp2[i]->getMemberNode()->bFailed = true;
					mp1[i]->getMemberNode()->bFailed = true;
					failedOneNode = true;
					cout<<endl<<"Failed a non-replica node"<<endl;
					break;
				}
			}
		}
		if ( !failedOneNode ) {
			// The code can never reach here
			log->LOG(&mp2[number]->getMemberNode()->addr, "Could not fail a node(non-replica)");
			cout<<"Could not fail a node(non-replica). Exiting!!!";
			exit(1);
		}

		number = findARandomNodeThatIsAlive();

		// Step 4.d Issue a read operation
		cout<<endl<<"Reading a valid key.... ... .. . ."<<endl;
		log->LOG(&mp2[number]->getMemberNode()->addr, "READ OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), it->second.c_str(), par->getcurrtime());
		// This read should fail since at least quorum nodes are not alive
		mp2[number]->clientRead(it->first);
	}

	/** end of test 4 **/

	/**
	 * Test 5: Read a non-existent key.
	 */
	if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME + STABILIZE_TIME + LAST_FAIL_TIME ) ) {
		string invalidKey = "invalidKey";

		// Step 5.a Find a node that is alive
		number = findARandomNodeThatIsAlive();

		// Step 5.b Issue a read operation
		cout<<endl<<"Reading an invalid key.... ... .. . ."<<endl;
		log->LOG(&mp2[number]->getMemberNode()->addr, "READ OPERATION KEY: %s at time: %d", invalidKey.c_str(), par->getcurrtime());
		// This read should fail since at least quorum nodes are not alive
		mp2[number]->clientRead(invalidKey);
	}

	/** end of test 5 **/

}

/**
 * FUNCTION NAME: updateTest
 *
 * DECRIPTION: This tests the update API of the KV Store
 */
void Application::updateTest() {
	// Step 0. Key to be updated
	// This key is used for all update tests
	map<string, string>::iterator it = testKVPairs.begin();
	it++;
	string newValue = "newValue";
	int number;
	vector<Node> replicas;
	int replicaIdToFail = TERTIARY;
	int nodeToFail;
	bool failedOneNode = false;

	/**
	 * Test 1: Test if value is updated correctly in quorum number of nodes
	 */
	if ( par->getcurrtime() == TEST_TIME ) {
		// Step 1.a. Find a node that is alive
		number = findARandomNodeThatIsAlive();

		// Step 1.b Do a update operation
		cout<<endl<<"Updating a valid key.... ... .. . ."<<endl;
		log->LOG(&mp2[number]->getMemberNode()->addr, "UPDATE OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), newValue.c_str(), par->getcurrtime());
		mp2[number]->clientUpdate(it->first, newValue);
	}

	/** end of test 1 **/

	/**
	 * Test 2: FAIL ONE REPLICA. Test if value is updated correctly in quorum number of nodes after ONE OF THE REPLICAS IS FAILED
	 */
	if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME) ) {
		// Step 2.a Find a node that is alive and assign it as number
		number = findARandomNodeThatIsAlive();

		// Step 2.b Find the replicas of this key
		replicas.clear();
		replicas = mp2[number]->findNodes(it->first);
		// if quorum replicas are not found then exit
		if ( replicas.size() < RF-1 ) {
			log->LOG(&mp2[number]->getMemberNode()->addr, "Could not find at least quorum replicas for this key. Exiting!!! size of replicas vector: %d", replicas.size());
			cout<<endl<<"Could not find at least quorum replicas for this key. Exiting!!! size of replicas vector: "<<replicas.size()<<endl;
			exit(1);
		}

		// Step 2.c Fail a replica
		for ( int i = 0; i < par->EN_GPSZ; i++ ) {
			if ( mp2[i]->getMemberNode()->addr.getAddress() == replicas.at(replicaIdToFail).getAddress()->getAddress() ) {
				if ( !mp2[i]->getMemberNode()->bFailed ) {
					nodeToFail = i;
					failedOneNode = true;
					break;
				}
				else {
					// Since we fail at most two nodes, one of the replicas must be alive
					if ( replicaIdToFail > 0 ) {
						replicaIdToFail--;
					}
					else {
						failedOneNode = false;
					}
				}
			}
		}
		if ( failedOneNode ) {
			log->LOG(&mp2[nodeToFail]->getMemberNode()->addr, "Node failed at time=%d", par->getcurrtime());
			mp2[nodeToFail]->getMemberNode()->bFailed = true;
			mp1[nodeToFail]->getMemberNode()->bFailed = true;
			cout<<endl<<"Failed a replica node"<<endl;
		}
		else {
			// The code can never reach here
			log->LOG(&mp2[number]->getMemberNode()->addr, "Could not fail a node");
			cout<<"Could not fail a node. Exiting!!!";
			exit(1);
		}

		number = findARandomNodeThatIsAlive();

		// Step 2.d Issue a update
		cout<<endl<<"Updating a valid key.... ... .. . ."<<endl;
		log->LOG(&mp2[number]->getMemberNode()->addr, "UPDATE OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), newValue.c_str(), par->getcurrtime());
		mp2[number]->clientUpdate(it->first, newValue);

		failedOneNode = false;
	}

	/** end of test 2 **/

	/**
	 * Test 3 part 1: Fail two replicas. Test if value is updated correctly in quorum number of nodes after TWO OF THE REPLICAS ARE FAILED
	 */
	if ( par->getcurrtime() >= (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME) ) {

		vector<int> nodesToFail;
		nodesToFail.clear();
		int count = 0;

		if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME) ) {
			// Step 3.a. Find a node that is alive
			number = findARandomNodeThatIsAlive();

			// Get the keys replicas
			replicas.clear();
			replicas = mp2[number]->findNodes(it->first);

			// Step 3.b. Fail two replicas
			if ( replicas.size() > 2 ) {
				replicaIdToFail = TERTIARY;
				while ( count != 2 ) {
					int i = 0;
					while ( i != par->EN_GPSZ ) {
						if ( mp2[i]->getMemberNode()->addr.getAddress() == replicas.at(replicaIdToFail).getAddress()->getAddress() ) {
							if ( !mp2[i]->getMemberNode()->bFailed ) {
								nodesToFail.emplace_back(i);
								replicaIdToFail--;
								count++;
								break;
							}
							else {
								// Since we fail at most two nodes, one of the replicas must be alive
								if ( replicaIdToFail > 0 ) {
									replicaIdToFail--;
								}
							}
						}
						i++;
					}
				}
			}
			else {
				// If the code reaches here. Test your stabilization protocol
				cout<<endl<<"Not enough replicas to fail two nodes. Exiting test case !! "<<endl;
			}
			if ( count == 2 ) {
				for ( int i = 0; i < nodesToFail.size(); i++ ) {
					// Fail a node
					log->LOG(&mp2[nodesToFail.at(i)]->getMemberNode()->addr, "Node failed at time=%d", par->getcurrtime());
					mp2[nodesToFail.at(i)]->getMemberNode()->bFailed = true;
					mp1[nodesToFail.at(i)]->getMemberNode()->bFailed = true;
					cout<<endl<<"Failed a replica node"<<endl;
				}
			}
			else {
				// The code can never reach here
				log->LOG(&mp2[number]->getMemberNode()->addr, "Could not fail two nodes");
				cout<<"Could not fail two nodes. Exiting!!!";
				exit(1);
			}

			number = findARandomNodeThatIsAlive();

			// Step 3.c Issue an update
			cout<<endl<<"Updating a valid key.... ... .. . ."<<endl;
			log->LOG(&mp2[number]->getMemberNode()->addr, "UPDATE OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), newValue.c_str(), par->getcurrtime());
			// This update should fail since at least quorum nodes are not alive
			mp2[number]->clientUpdate(it->first, newValue);
		}

		/**
		 * TEST 3 part 2: After failing two replicas and waiting for STABILIZE_TIME, issue an update
		 */
		// Step 3.d Wait for stabilization protocol to kick in
		if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME + STABILIZE_TIME) ) {
			number = findARandomNodeThatIsAlive();
			// Step 3.e Issue a update
			cout<<endl<<"Updating a valid key.... ... .. . ."<<endl;
			log->LOG(&mp2[number]->getMemberNode()->addr, "UPDATE OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), newValue.c_str(), par->getcurrtime());
			// This update should be successful
			mp2[number]->clientUpdate(it->first, newValue);
		}
	}

	/** end of test 3 **/

	/**
	 * Test 4: FAIL A NON-REPLICA. Test if value is read correctly in quorum number of nodes after a NON-REPLICA IS FAILED
	 */
	if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME + STABILIZE_TIME + LAST_FAIL_TIME ) ) {
		// Step 4.a. Find a node that is alive
		number = findARandomNodeThatIsAlive();

		// Step 4.b Find a non - replica for this key
		replicas.clear();
		replicas = mp2[number]->findNodes(it->first);
		for ( int i = 0; i < par->EN_GPSZ; i++ ) {
			if ( !mp2[i]->getMemberNode()->bFailed ) {
				if ( mp2[i]->getMemberNode()->addr.getAddress() != replicas.at(PRIMARY).getAddress()->getAddress() &&
					 mp2[i]->getMemberNode()->addr.getAddress() != replicas.at(SECONDARY).getAddress()->getAddress() &&
					 mp2[i]->getMemberNode()->addr.getAddress() != replicas.at(TERTIARY).getAddress()->getAddress() ) {
					// Step 4.c Fail a non-replica node
					log->LOG(&mp2[i]->getMemberNode()->addr, "Node failed at time=%d", par->getcurrtime());
					mp2[i]->getMemberNode()->bFailed = true;
					mp1[i]->getMemberNode()->bFailed = true;
					failedOneNode = true;
					cout<<endl<<"Failed a non-replica node"<<endl;
					break;
				}
			}
		}

		if ( !failedOneNode ) {
			// The code can never reach here
			log->LOG(&mp2[number]->getMemberNode()->addr, "Could not fail a node(non-replica)");
			cout<<"Could not fail a node(non-replica). Exiting!!!";
			exit(1);
		}

		number = findARandomNodeThatIsAlive();

		// Step 4.d Issue a update operation
		cout<<endl<<"Updating a valid key.... ... .. . ."<<endl;
		log->LOG(&mp2[number]->getMemberNode()->addr, "UPDATE OPERATION KEY: %s VALUE: %s at time: %d", it->first.c_str(), newValue.c_str(), par->getcurrtime());
		// This read should fail since at least quorum nodes are not alive
		mp2[number]->clientUpdate(it->first, newValue);
	}

	/** end of test 4 **/

	/**
	 * Test 5: Udpate a non-existent key.
	 */
	if ( par->getcurrtime() == (TEST_TIME + FIRST_FAIL_TIME + STABILIZE_TIME + STABILIZE_TIME + LAST_FAIL_TIME ) ) {
		string invalidKey = "invalidKey";
		string invalidValue = "invalidValue";

		// Step 5.a Find a node that is alive
		number = findARandomNodeThatIsAlive();

		// Step 5.b Issue a read operation
		cout<<endl<<"Updating a valid key.... ... .. . ."<<endl;
		log->LOG(&mp2[number]->getMemberNode()->addr, "UPDATE OPERATION KEY: %s VALUE: %s at time: %d", invalidKey.c_str(), invalidValue.c_str(), par->getcurrtime());
		// This read should fail since at least quorum nodes are not alive
		mp2[number]->clientUpdate(invalidKey, invalidValue);
	}

	/** end of test 5 **/

}
