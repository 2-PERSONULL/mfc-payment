package com.mfc.payment.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.mfc.payment.dto.kafka.PaymentCompletedEvent;
import com.mfc.payment.dto.kafka.TradeSettledEventDto;

@Configuration
@EnableKafka
public class KafkaConfig {

	@Value("${spring.kafka.consumer.bootstrap-servers}")
	private String consumerBootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String consumerGroupId;

	@Value("${spring.kafka.producer.bootstrap-servers}")
	private String producerBootstrapServers;

	// Producer Configuration
	@Bean
	public ProducerFactory<String, PaymentCompletedEvent> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	// Consumer Configuration
	@Bean
	public ConsumerFactory<String, TradeSettledEventDto> consumerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
		configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(
			configProps,
			new StringDeserializer(),
			new JsonDeserializer<>(TradeSettledEventDto.class, false)
		);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, TradeSettledEventDto> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, TradeSettledEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}