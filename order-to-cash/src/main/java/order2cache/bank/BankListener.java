package order2cache.bank;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import order2cache.Topics;

@Service
public class BankListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(BankListener.class);
	
	@Autowired
	private BankDispatcher dispatcher;
	
	@SuppressWarnings("incomplete-switch")
	@KafkaListener(topics = "#{'${kafka.consumer.topics}'.split(',')}")
	public void onReceiving(String data,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset) {
		LOGGER.info("topic = {}, partition = {}, offset = {}, data = {}",
				topic,
				partition,
				offset,
				data);
		Optional<Topics> topicItem = Topics.findTopicByName(topic);
		if (topicItem.isPresent()) {
			switch (topicItem.get()) {
			case SHIPMENT_INFORMATION:
				break;
			case SHIPMENT_SCHEDULE:
				break;
			case PAYMENT_ADVICE:
				break;
			}
		}
	}
}
