package com.ayun.product.repository

import com.ayun.product.entity.Product
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {
    @Autowired private lateinit var entityManager: TestEntityManager

    @Autowired private lateinit var productRepository: ProductRepository

    private lateinit var testProduct: Product

    @BeforeEach
    fun setUp() {
        testProduct =
            Product(
                productName = "테스트 상품",
                viewCount = 0L,
                stockQuantity = 100,
                productPrice = BigDecimal("29.99"),
//                        createdBy = "testUser"
            )
    }

    @Test
    fun `상품 저장 및 조회 테스트`() {
        // given
        val savedProduct = entityManager.persistAndFlush(testProduct)

        // when
        val foundProduct = productRepository.findById(savedProduct.productId!!)

        // then
        assertTrue(foundProduct.isPresent)
        assertEquals(testProduct.productName, foundProduct.get().productName)
        assertEquals(testProduct.productPrice, foundProduct.get().productPrice)
        assertNotNull(foundProduct.get().createdAt)
    }

    @Test
    fun `상품명으로 검색 테스트`() {
        // given
        entityManager.persistAndFlush(testProduct)
        entityManager.persistAndFlush(
            Product(
                productName = "다른 상품",
                stockQuantity = 50,
                productPrice = BigDecimal("19.99"),
//                createdBy = "testUser",
            ),
        )

        // when
        val products = productRepository.findByProductNameContainingIgnoreCase("테스트")

        // then
        assertEquals(1, products.size)
        assertEquals("테스트 상품", products[0].productName)
    }

    @Test
    fun `가격 범위로 검색 테스트`() {
        // given
        entityManager.persistAndFlush(testProduct)
        entityManager.persistAndFlush(
            Product(
                productName = "비싼 상품",
                stockQuantity = 10,
                productPrice = BigDecimal("99.99"),
//                createdBy = "testUser",
            ),
        )

        // when
        val products =
            productRepository.findByProductPriceBetween(
                BigDecimal("20.00"),
                BigDecimal("50.00"),
            )

        // then
        assertEquals(1, products.size)
        assertEquals("테스트 상품", products[0].productName)
    }

    @Test
    fun `재고가 있는 상품 조회 테스트`() {
        // given
        entityManager.persistAndFlush(testProduct)
        entityManager.persistAndFlush(
            Product(
                productName = "품절 상품",
                stockQuantity = 0,
                productPrice = BigDecimal("15.99"),
//                createdBy = "testUser",
            ),
        )

        // when
        val products = productRepository.findByStockQuantityGreaterThan(0)

        // then
        assertEquals(1, products.size)
        assertEquals("테스트 상품", products[0].productName)
    }
}
