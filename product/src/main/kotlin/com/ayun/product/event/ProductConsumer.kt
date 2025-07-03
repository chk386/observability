package com.ayun.product.event

import com.ayun.product.config.TransactionHandler
import com.ayun.product.repository.ProductRepository
import com.ayun.product.vo.ProductEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class ProductConsumer(
    private val tx: TransactionHandler,
    private val productRepository: ProductRepository,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass::class.java)

    @KafkaListener(
        topics = ["product-events"],
        groupId = "product-consumer-group",
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun handleProductEvent(
        @Payload message: ProductEvent,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        ack: Acknowledgment,
    ) {
        logger.info(
            "카프카 컨슈머 수신 - productId: {}, topic: {}, partition: {}, offset: {}",
            message.productId,
            topic,
            partition,
            offset,
        )

        tx.runInNewTransaction {
            productRepository.incrementViewCount(message.productId)
        }

        ack.acknowledge()
    }
}
