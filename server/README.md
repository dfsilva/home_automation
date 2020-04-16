## Run Cluster

`mvn clean package`

### node 1
`mvn exec:java -Dexec.mainClass="br.com.diegosilva.automation.Main" -Dcanonical.hostname=127.0.0.1 -Dcanonical.port=2551 -Dhttp.port=8080 -Dakka.cluster.seed-nodes.0=akka://Automation@127.0.0.1:2551 -Dakka.cluster.seed-nodes.1=akka://Automation@127.0.0.1:2552`

### node 2
`mvn exec:java -Dexec.mainClass="br.com.diegosilva.automation.Main" -Dcanonical.hostname=127.0.0.1 -Dcanonical.port=2552 -Dhttp.port=8081 -Dakka.cluster.seed-nodes.0=akka://Automation@127.0.0.1:2551 -Dakka.cluster.seed-nodes.1=akka://Automation@127.0.0.1:2552`