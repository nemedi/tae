package demo.buyer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import demo.DocumentTransformer;
import demo.MainInbox;

@Service
public class BuyerListener {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(BuyerListener.class);
	
	@Autowired
	private MainInbox inbox;
	
	@Autowired
	private DocumentTransformer transformer;
	
	@KafkaListener(topics = "#{'${kafka.consumer.topics}'.split(',')}",
			groupId = "#{'${kafka.consumer.group}'}")
	public void onReceive(String data,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
			@Header(KafkaHeaders.OFFSET) int offset)
					throws TransformerFactoryConfigurationError, IOException, TransformerException, InterruptedException, ExecutionException {
		LOGGER.info("topic = {}, partition = {}, offset = {}",
				topic,
				partition,
				offset);
		switch (topic) {
		case "invoice":
			inbox.write("buyer", "invoice", data);
			break;
		}
	}
}
