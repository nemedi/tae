package order2cache.logistics;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class LogisticsDispatcher {

	@Autowired
	private KafkaTemplate<String, String> template;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogisticsDispatcher.class);
	
	public void dispatch(String topic, String data) throws InterruptedException, ExecutionException {
		SendResult<String, String> sendResult = template.send(topic, data).get();
		RecordMetadata recordMetadata = sendResult.getRecordMetadata();
		LOGGER.info("topic = {}, partition = {}, offset = {}, data = {}",
				recordMetadata.topic(),
				recordMetadata.partition(),
				recordMetadata.offset(),
				data);
	}
	
}
