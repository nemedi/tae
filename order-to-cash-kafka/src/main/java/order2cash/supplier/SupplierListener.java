package order2cash.supplier;

import java.text.MessageFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import order2cache.DocumentTransformer;
import order2cache.Topics;

@Service
public class SupplierListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SupplierListener.class);
	
	@Autowired
	private SupplierDispatcher dispatcher;
	
	@SuppressWarnings("incomplete-switch")
	@KafkaListener(topics = "#{'${kafka.consumer.topics}'.split(',')}")
	public void onReceiving(String data,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.OFFSET) int offset) {
		try {
			LOGGER.info("topic = {}, partition = {}, offset = {}, data = {}",
					topic,
					partition,
					offset,
					data);
			Optional<Topics> topicItem = Topics.findTopicByName(topic);
			if (topicItem.isPresent()) {
				switch (topicItem.get()) {
				case PURCHASE_ORDER:
					byte[] results = DocumentTransformer.transform(MessageFormat.format("{0}_to_{1}",
							Topics.PURCHASE_ORDER.getName(),
							Topics.PURCHASE_ORDER_ACKNOWLEDGEMENT.getName()),
							data.getBytes());
					dispatcher.dispatch(Topics.PURCHASE_ORDER_ACKNOWLEDGEMENT.getName(), new String(results));
					break;
				case LOGISTIC_SERVICE_RESPONSE:
					break;
				case CONSOLIDATORS_FREIGHT_BILL:
					break;
				case SHIPMENT_INFORMATION:
					break;
				case SHIPMENT_SCHEDULE:
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
	}
}
