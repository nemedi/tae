package demo;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class PayloadDispatcher {

	@Autowired
    private KafkaTemplate<String, Payload> payloadTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadDispatcher.class);

    public boolean dispatch(Payload workUnit) {
        try {
            SendResult<String, Payload> sendResult = payloadTemplate.sendDefault(workUnit.getId(),
            		workUnit).get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            LOGGER.info("topic = {}, partition = {}, offset = {}, workUnit = {}",
                    recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset(), workUnit);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
