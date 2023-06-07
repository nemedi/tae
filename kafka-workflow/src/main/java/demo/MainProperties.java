package demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MainProperties {

	@Value("${kafka.producer.bootstrap}")
    private String producerBootstrap;
	
	@Value("${kafka.consumer.bootstrap}")
    private String consumerBootstrap;
	
	@Value("${kafka.consumer.topics}")
    private String topics;
	
	@Value("${kafka.consumer.group}")
    private String group;

    public String getProducerBootstrap() {
        return producerBootstrap;
    }
    
    public String getConsumerBootstrap() {
        return consumerBootstrap;
    }

    public String getTopics() {
        return topics;
    }

    public String getGroup() {
		return group;
	}
    
}