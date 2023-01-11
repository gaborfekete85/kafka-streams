package com.fintama.kafka.streaming;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

public class AggregationsMain {
    //  Start the Producer
    // ./kafka-console-producer.sh --broker-list localhost:9092 --topic aggregations-input-topic --property parse.key=true --property key.separator=:

    // Start the Consumers
    // ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic aggregations-output-charactercount-topic --property print.key=true
    // ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic aggregations-output-count-topic --property print.key=true
    // ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic aggregations-output-reduce-topic --property print.key=true


    public static void main(String[] args) {
        // Set up the configuration.
        //RocksDB.loadLibrary();
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "aggregations-example");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        // Since the input topic uses Strings for both key and value, set the default Serdes to String.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // Get the source stream.
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> source = builder.stream("aggregations-input-topic");
        
        // Group the source stream by the existing Key.
        KGroupedStream<String, String> groupedStream = source.groupByKey();
        
        // Create an aggregation that totals the length in characters of the value for all records sharing the same key.
        // () -> 0 Initialiizes the Aggregated value ( The length of record sharing the same keys )
        KTable<String, Integer> aggregatedTable = groupedStream.aggregate(
            () -> 0,
            (aggKey, newValue, aggValue) -> aggValue + newValue.length(),
            Materialized.with(Serdes.String(), Serdes.Integer()));
        aggregatedTable.toStream().foreach((final String key, final Integer value) -> System.out.println("Characters of: " + key + ": " + value));
        aggregatedTable.toStream().to("aggregations-output-charactercount-topic", Produced.with(Serdes.String(), Serdes.Integer()));
        // This should not be here is the it was the same as the incoming strem <String, String>
        // BUT as We generate <String, Integer> here that's why it is required here

        // Count the number of records for each key.
        KTable<String, Long> countedTable = groupedStream.count(Materialized.with(Serdes.String(), Serdes.Long()));
        countedTable.toStream().foreach((final String key, final Long value) -> System.out.println("# of events for: " + key + ": " + value));
        countedTable.toStream().to("aggregations-output-count-topic", Produced.with(Serdes.String(), Serdes.Long()));
        
        // Combine the values of all records with the same key into a string separated by spaces.
        KTable<String, String> reducedTable = groupedStream.reduce((aggValue, newValue) -> aggValue + " " + newValue);
//        KTable<String, String> reducedTable = groupedStream.reduce((aggValue, newValue) -> newValue); // JB Requirement
        reducedTable.toStream().to("aggregations-output-reduce-topic");
        
        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        // Print the topology to the console.
        System.out.println(topology.describe());
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach a shutdown handler to catch control-c and terminate the application gracefully.
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

}
