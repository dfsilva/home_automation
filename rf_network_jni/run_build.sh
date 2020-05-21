#!/bin/bash
if [ -z "$JAVA_HOME" ]
then
	echo "JAVA_HOME está vazia."
	exit 1
else	
./build.sh
sudo java -jar target/rfnative-run-test.jar
fi
