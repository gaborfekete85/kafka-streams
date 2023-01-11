#/bin/bash
echo "Creating topics"

function createTopic() {
  local topicName=$1
  echo "Creating topic: ${topicName}"
  kafka-topics.sh --bootstrap-server localhost:9092 --topic ${topicName} --create --partitions 3 --replication-factor 1
}

createTopic streams-example
createTopic streams-input-topic
createTopic stateless-transformations-input-topic
createTopic stateless-transformations-output-topic
createTopic aggregations-input-topic
createTopic aggregations-output-topic
createTopic aggregations-output-charactercount-topic
createTopic aggregations-output-count-topic
createTopic aggregations-output-reduce-topic
createTopic joins-input-topic-left
createTopic joins-input-topic-right
createTopic inner-join-output-topic
createTopic left-join-output-topic
createTopic outer-join-output-topic
createTopic windowing-input-topic
createTopic windowing-output-topic
