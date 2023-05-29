package demo;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class MainConfiguration {

	@Autowired
    private MainProperties properties;
	
    @Bean
    public KafkaTemplate<String, Payload> payloadTemplate() {
        KafkaTemplate<String, Payload> template = new KafkaTemplate<>(producerFactory());
        template.setDefaultTopic(properties.getTopic());
        return template;
    }
	
	@Bean
    public ProducerFactory<String, Payload> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfiguration(),
        		new StringSerializer(),
        		new JsonSerializer<Payload>());
    }

    @Bean
    public Map<String, Object> producerConfiguration() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrap());
        return configuration;
    }

}
