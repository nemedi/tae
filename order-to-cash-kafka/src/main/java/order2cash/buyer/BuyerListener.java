package order2cash.buyer;

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
public class BuyerListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(BuyerListener.class);
	
	@Autowired
	private BuyerDispatcher dispatcher;
	
	@SuppressWarnings("incomplete-switch")
	@KafkaListener(topics = "#{'${kafka.consumer.topics}'.split(',')}",
		groupId = "#{'${kafka.consumer.group}'}")
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
			case PURCHASE_ORDER_ACKNOWLEDGEMENT:
				break;
			case INVOICE:
				break;
			case SHIPMENT_NOTICE:
				break;
			case SHIPMENT_SCHEDULE:
				break;
			}
		}
	}
}
