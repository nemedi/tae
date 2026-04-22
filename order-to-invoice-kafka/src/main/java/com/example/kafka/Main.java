package com.example.kafka;

public class Main {
    public static void main(String[] args) throws Exception {
        KafkaOrderConsumer consumer = new KafkaOrderConsumer("localhost:9092", "src/main/resources/order-to-invoice.xsl");
        consumer.run();
    }
}
