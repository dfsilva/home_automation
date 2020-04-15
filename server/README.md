## Run Cluster

`mvn clean package`

### node 1
`mvn exec:java -Dexec.mainClass="br.com.diegosilva.automation.Main" -Dcanonical.port=2551 -Dhttp.port=8080`

### node 2
`mvn exec:java -Dexec.mainClass="br.com.diegosilva.automation.Main" -Dcanonical.port=2552 -Dhttp.port=8081`