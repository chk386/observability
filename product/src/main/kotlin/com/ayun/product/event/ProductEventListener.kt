package com.ayun.product.event

import com.ayun.product.vo.ProductEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ProductEventListener(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
    private val logger = LoggerFactory.getLogger(ProductEventListener::class.java)

    @EventListener
    @Async
    fun productEventListen(event: ProductEvent) {
        kafkaTemplate
            .send("product-events", event)
            .whenComplete { result, _ ->
                logger.debug("토픽 발행 결과 : {}", result)
            }.exceptionally {
                val message = "상품 이벤트 토픽 발행 실패 productId : ${event.productId}"
                logger.error(message)

                throw RuntimeException(message)
            }
    }
}
