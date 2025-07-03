package com.ayun.product

import com.ayun.product.service.ProductService
import io.opentelemetry.api.trace.Span
import org.apache.coyote.ProtocolHandler
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.util.concurrent.Executors

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
class ObservabilityApplication

fun main(args: Array<String>) {
    val applicationContext = runApplication<ObservabilityApplication>(*args)
}

@Configuration
class VirtualThreadConfig {
    @Bean
    fun protocolHandlerVirtualThreadExecutorCustomizer(): TomcatProtocolHandlerCustomizer<*> =
        TomcatProtocolHandlerCustomizer { protocolHandler: ProtocolHandler ->
            protocolHandler.executor = Executors.newVirtualThreadPerTaskExecutor()
        }
}

@Configuration
class RouterConfig(
    private val productService: ProductService,
) {
    val log = LoggerFactory.getLogger(RouterConfig::class.java)!!

    @Bean
    fun route() =
        router {
            GET("/products/") {
                val current = Span.current()
                val spanContext = current.spanContext

                log.debug("전체 상품 목록 조회 요청 traceId : ${spanContext.traceId}")

                val products = productService.getAllProducts()
                ServerResponse.ok().body(products)
            }

            GET("/products/{productId}") {
                val id = it.pathVariable("productId").toLong()
                log.debug("상품 조회 요청 - ID: {}", id)

                val product = productService.getProductById(id)
                if (product != null) {
                    ServerResponse.ok().body(product)
                } else {
                    ServerResponse.notFound().build()
                }
            }
        }
}
