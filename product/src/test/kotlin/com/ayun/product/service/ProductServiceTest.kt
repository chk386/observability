package com.ayun.product.service

import com.ayun.product.entity.Product
import com.ayun.product.repository.ProductRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {
    @Mock private lateinit var productRepository: ProductRepository

    @Mock private lateinit var eventPublisher: ApplicationEventPublisher

    @InjectMocks private lateinit var productService: ProductService

    private lateinit var testProduct: Product

    @BeforeEach
    fun setUp() {
        testProduct =
            Product(
                productId = 1L,
                productName = "테스트 상품",
                viewCount = 5L,
                stockQuantity = 100,
                productPrice = BigDecimal("29.99"),
            )
    }

    @Test
    fun `전체 상품 목록 조회 테스트`() {
        // given
        val products = listOf(testProduct)
        `when`(productRepository.findAll()).thenReturn(products)

        // when
        val result = productService.getAllProducts()

        // then
        assertEquals(1, result.size)
        assertEquals(testProduct.productName, result[0].productName)
        verify(productRepository).findAll()
    }

    @Test
    fun `상품 ID로 조회 테스트`() {
        // given
        `when`(productRepository.findById(1L)).thenReturn(Optional.of(testProduct))
//        doNothing().`when`(eventPublisher).publishEvent(any())

        // when
        val result = productService.getProductById(1L)

        // then
        assertNotNull(result)
        assertEquals(testProduct.productName, result.productName)
        verify(productRepository).findById(1L)
    }

    @Test
    fun `존재하지 않는 상품 조회 테스트`() {
        // given
        `when`(productRepository.findById(999L)).thenReturn(Optional.empty())

        // when
        val result = productService.getProductById(999L)

        // then
        assertNull(result)
        verify(productRepository).findById(999L)
        verify(productRepository, never()).incrementViewCount(999L)
    }

    @Test
    fun `상품 생성 테스트`() {
        // given
        `when`(productRepository.save(testProduct)).thenReturn(testProduct)

        // when
        val result = productService.createProduct(testProduct)

        // then
        assertEquals(testProduct.productName, result.productName)
        verify(productRepository).save(testProduct)
    }

    @Test
    fun `상품명으로 검색 테스트`() {
        // given
        val products = listOf(testProduct)
        `when`(productRepository.findByProductNameContainingIgnoreCase("테스트")).thenReturn(products)

        // when
        val result = productService.searchProductsByName("테스트")

        // then
        assertEquals(1, result.size)
        assertEquals(testProduct.productName, result[0].productName)
        verify(productRepository).findByProductNameContainingIgnoreCase("테스트")
    }
}
