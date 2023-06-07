package order2cash.buyer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class BuyerConfiguration {
	
	@Value("${kafka.producer.bootstrap}")
	private String producerBootstrap;
	
	@Value("${kafka.consumer.bootstrap}")
	private String consumerBootstrap;
	
	@Value("${kafka.consumer.group}")
	private String consumerGroup;

	@Bean
	public KafkaTemplate<String, String> template() {
		KafkaTemplate<String, String> template = new KafkaTemplate<String, String>(producerFactory());
		return template;
	}
	
	@Bean
	public ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<String, String>(producerConfiguration(),
				new StringSerializer(),
				new StringSerializer());
	}
	
	public Map<String, Object> producerConfiguration() {
		Map<String, Object> configuration = new HashMap<String, Object>();
		configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrap);
		return configuration;
	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConcurrency(1);
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
	
	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		return new DefaultKafkaConsumerFactory<String, String>(consumerConfiguration(),
				new StringDeserializer(),
				new StringDeserializer());
	}
	
	public Map<String, Object> consumerConfiguration() {
		Map<String, Object> configuration = new HashMap<String, Object>();
		configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrap);
		configuration.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
		configuration.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		configuration.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		configuration.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		return configuration;
	}
}
