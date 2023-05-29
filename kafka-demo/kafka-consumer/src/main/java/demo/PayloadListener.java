package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class PayloadListener {

	private static final Logger log = LoggerFactory.getLogger(PayloadListener.class);

	@KafkaListener(topics = "#{@properties.getTopic()}")
	public void onReceiving(Payload payload,
			@Header(KafkaHeaders.OFFSET) Integer offset,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		log.info("Processing topic = {}, partition = {}, offset = {}, workUnit = {}",
				topic,
				partition,
				offset,
				payload);
	}

}
