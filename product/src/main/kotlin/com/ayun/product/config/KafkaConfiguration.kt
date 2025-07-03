package com.ayun.product.config

import com.ayun.product.vo.ProductEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.ProducerListener
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES
import org.springframework.kafka.support.serializer.JsonSerializer
import kotlin.jvm.java

@Configuration
@EnableKafka
class KafkaProducerConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> {
        val configProps =
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.RETRIES_CONFIG to 3,
                ProducerConfig.BATCH_SIZE_CONFIG to 16384,
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BUFFER_MEMORY_CONFIG to 33554432,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to 5,
            )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, Any>): KafkaTemplate<String, Any> {
        val template = KafkaTemplate(producerFactory)

        // 기본 토픽 설정
        template.defaultTopic = "product-events"

        // 전송 결과 콜백 설정
        template.setProducerListener(
            object : ProducerListener<String, Any> {
                override fun onSuccess(
                    producerRecord: ProducerRecord<String, Any>,
                    recordMetadata: RecordMetadata,
                ) {
                    logger.debug("Message sent successfully: ${producerRecord.key()} to ${recordMetadata.topic()}")
                }

                override fun onError(
                    producerRecord: ProducerRecord<String, Any>,
                    recordMetadata: RecordMetadata?,
                    exception: Exception,
                ) {
                    logger.error("Failed to send message: ${producerRecord.key()}", exception)
                }
            },
        )

        return template
    }

    // Consumer 설정 추가 (객체 수신을 위한 JsonDeserializer 사용)
    @Bean
    fun consumerFactory(): ConsumerFactory<String, ProductEvent> {
        val configProps =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to "product-consumer-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
                ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 30000,
                ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 500,
                // JsonDeserializer 설정
                JsonDeserializer.TRUSTED_PACKAGES to "com.ayun.product.vo",
                JsonDeserializer.VALUE_DEFAULT_TYPE to ProductEvent::class.java.name,
                JsonDeserializer.USE_TYPE_INFO_HEADERS to false,
            )
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ProductEvent> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, ProductEvent>()
        factory.consumerFactory = consumerFactory()

        // MANUAL ACK 모드 설정
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.pollTimeout = 3000
        factory.setConcurrency(1)

        return factory
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KafkaProducerConfig::class.java)
    }
}
