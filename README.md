# Kafka Streams

## Start Kafka
```
cd src/main/resources/stack
docker-compose up
```

## Download Kafka
Download Kafka from: https://kafka.apache.org/downloads

Latest Currently: https://downloads.apache.org/kafka/3.3.1/kafka_2.13-3.3.1.tgz

### Configure Kafka Binary
Extract the downloaded Kafka

In order to run Kafka commands the Kafka binary need to be added to the classpath

Example ( Mac, Linux )
```
export PATH=$PATH:/Users/feketegabor/app/kafka_2.13-3.2.0/bin
```

Now the `kafka-topics.sh` must be available in the console

## Creating the Topics
```
cd src/main/resources
./createTopics.sh
```

# Execute the Streaming Applications
## StreamsMain.java
> Simply writes each event from the input topic to the output one
```
./gradlew runStreams
```

### Run the producer in the console
```
kafka-console-producer.sh --broker-list localhost:9092 --topic streams-input-topic --property parse.key=true --property key.separator=:
```

### Run the consumer in the console
```
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-output-topic --property print.key=true
```

## StatelessTransformationsMain.java
> Demonstrates some Stateless transformations
```
./gradlew runStatelessTransformations
```
### Run the producer in the console
```
kafka-console-producer.sh --broker-list localhost:9092 --topic stateless-transformations-input-topic --property parse.key=true --property key.separator=:
```

### Run the consumer in the console
```
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic stateless-transformations-output-topic --property print.key=true
```

## AggregationsMain.java
> Demonstrates Stateful Transformations
```
./gradlew runAggregations
```
### Run the producer in the console
```
kafka-console-producer.sh --broker-list localhost:9092 --topic aggregations-input-topic --property parse.key=true --property key.separator=:
```
### Run the consumers in the console
```
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic aggregations-output-charactercount-topic --property print.key=true
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic aggregations-output-count-topic --property print.key=true
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic aggregations-output-reduce-topic --property print.key=true
```

## JoinsMain.java
> Demonstrates the Join operations ( inner, left, outer )
```
./gradlew runJoins
```
### Run the producer in the console
```
kafka-console-producer.sh --broker-list localhost:9092 --topic joins-input-topic-left --property parse.key=true --property key.separator=:
kafka-console-producer.sh --broker-list localhost:9092 --topic joins-input-topic-right --property parse.key=true --property key.separator=:
```
### Run the consumers in the console
```
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic inner-join-output-topic --property print.key=true
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic left-join-output-topic --property print.key=true
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic outer-join-output-topic --property print.key=true
```

## WindowingMain.java
> Demonstrates the Windowing functionality
```
./gradlew runWindowing
```
### Run the producer in the console
```
kafka-console-producer.sh --broker-list localhost:9092 --topic windowing-input-topic --property parse.key=true --property key.separator=:```
```
### Run the consumers in the console
```
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic windowing-output-topic --property print.key=true
```