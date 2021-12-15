#**********************
#*
#* Progam Name: MP1. Membership Protocol.
#*
#* Current file: run.sh
#* About this file: Submission shell script.
#* 
#***********************
#!/bin/sh
rm -rf grade-dir # Make sure grade-dir is clean before starting
rm -f dbg.*.log

mkdir grade-dir
cd grade-dir
wget https://spark-public.s3.amazonaws.com/cloudcomputing2/assignments/mp2_assignment.zip || { echo 'ERROR ... Please install wget' ; exit 1; }
unzip mp2_assignment.zip || { echo 'ERROR ... Zip file not found' ; exit 1; }
cd mp2_assignment
cp ../../MP2Node.* .
cp ../../MP1Node.* .
make clean > /dev/null 2>&1
make > /dev/null 2>&1 

echo "CREATE test"
./Application testcases/create.conf > /dev/null 2>&1
cp dbg.log ../../dbg.0.log
echo "DELETE test" 
./Application testcases/delete.conf > /dev/null 2>&1
cp dbg.log ../../dbg.1.log
echo "READ test"
./Application testcases/read.conf > /dev/null 2>&1
cp dbg.log ../../dbg.2.log
echo "UPDATE test"
./Application testcases/update.conf > /dev/null 2>&1
cp dbg.log ../../dbg.3.log

cd ../..
rm -rf grade-dir
