package demo;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class MainDispatcher {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(MainDispatcher.class);

	@Autowired
	private KafkaTemplate<String, String> template;
	
	public void dispatch(String topic, String data)
			throws InterruptedException, ExecutionException {
		SendResult<String, String> result = template.send(topic, data).get();
		RecordMetadata metadata = result.getRecordMetadata();
		LOGGER.info("topic = {}, partition = {}, offset = {}",
				metadata.topic(),
				metadata.partition(),
				metadata.offset());
		
	}
}
