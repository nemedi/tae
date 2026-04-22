package com.example.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaOrderConsumer {

    private final KafkaConsumer<String, String> consumer;
    private final OrderToInvoiceTransformer transformer;
    private final KafkaInvoiceProducer invoiceProducer;

    public KafkaOrderConsumer(String bootstrapServers, String xsltPath) throws Exception {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-transformer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("orders"));

        transformer = new OrderToInvoiceTransformer(xsltPath);
        invoiceProducer = new KafkaInvoiceProducer(bootstrapServers);
    }

    public void run() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                try {
                    String invoiceXml = transformer.transform(record.value());
                    invoiceProducer.sendInvoice(record.key(), invoiceXml);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
