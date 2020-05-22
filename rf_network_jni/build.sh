#!/bin/bash
if [ -z "$JAVA_HOME" ]
then
	echo "JAVA_HOME está vazia."
	exit 1
else	
rm src/main/resources/*.so
g++ -shared -lrf24-bcm -lrf24network -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux src/main/resources/rf_native.cpp -o src/main/resources/librfnative.so
mvn clean package
fi
