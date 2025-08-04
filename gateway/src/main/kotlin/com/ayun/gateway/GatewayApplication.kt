package com.ayun.gateway

import org.apache.coyote.ProtocolHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.util.concurrent.Executors

@SpringBootApplication class ObservabilityApplication

fun main(args: Array<String>) {
    runApplication<ObservabilityApplication>(*args)
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
class RouterConfig {
    @Bean
    fun route() =
        router {
            GET("/products-services") {
                ServerResponse.ok().body("Hello, World!")
            }
        }
}
