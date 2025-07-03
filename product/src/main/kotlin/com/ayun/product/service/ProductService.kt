package com.ayun.product.service

import com.ayun.product.entity.Product
import com.ayun.product.repository.ProductRepository
import com.ayun.product.vo.ProductEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val log = LoggerFactory.getLogger(ProductService::class.java)

    fun getAllProducts(): List<Product> = productRepository.findAll()

    fun getProductById(productId: Long): Product? {
        log.debug("상품 조회 - ID: {}", productId)
        val product = productRepository.findById(productId).orElse(null)

        // 이벤트 발행
        if (product != null) {
            eventPublisher.publishEvent(ProductEvent(productId))
        }

        return product
    }

    @Transactional
    fun createProduct(product: Product): Product {
        log.debug("상품 생성 - 상품명: {}", product.productName)
        return productRepository.save(product)
    }

    fun searchProductsByName(productName: String): List<Product> {
        log.debug("상품명으로 검색 - 검색어: {}", productName)
        return productRepository.findByProductNameContainingIgnoreCase(productName)
    }
}
