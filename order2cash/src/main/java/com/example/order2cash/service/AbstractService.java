package com.example.order2cash.service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.example.order2cash.util.XmlOutputWriter;
import com.example.order2cash.util.XsltTransformer;

public abstract class AbstractService {

	protected static final Logger log = LoggerFactory.getLogger(AbstractService.class);
    protected final KafkaTemplate<String, String> kafka;
    protected final XsltTransformer xslt;
    protected final XmlOutputWriter writer;
    
    public AbstractService(KafkaTemplate<String, String> kafka, XsltTransformer xslt, XmlOutputWriter writer) {
        this.kafka = kafka;
        this.xslt = xslt;
        this.writer = writer;
    }
    
    protected static ProducerRecord<String, String> record(String topic, String workflowId, String payload) {
        var rec = new ProducerRecord<String, String>(topic, payload);
        rec.headers().add("workflow-id", workflowId.getBytes(StandardCharsets.UTF_8));
        return rec;
    }

    protected static String shortUuid() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
