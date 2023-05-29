package demo;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class MainConfiguration {

	@Autowired
	private MainProperties properties;
	
	@Bean
	public MainProperties properties() {
		return properties;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Payload> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Payload> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConcurrency(1);
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, Payload> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfiguration(),
				new StringDeserializer(),
				new JsonDeserializer<Payload>(Payload.class));
	}

	@Bean
	public Map<String, Object> consumerConfiguration() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrap());
		configuration.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroup());
		configuration.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		configuration.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		configuration.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		return configuration;
	}

}
