Read the specification document thoroughly.

Create a high level design covering all scenarios / test cases before you start coding. 

How do I run only the CRUD tests ? 

$ make clean
$ make
$ ./Application ./testcases/create.conf
or 
$ ./Application ./testcases/delete.conf
or
$ ./Application ./testcases/read.conf
or
$ ./Application ./testcases/update.conf

How do I test if my code passes all the test cases ? 
Run the grader. Check the run procedure in KVStoreGrader.sh