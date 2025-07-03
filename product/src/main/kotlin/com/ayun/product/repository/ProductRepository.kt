package com.ayun.product.repository

import com.ayun.product.entity.Product
import java.math.BigDecimal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    // 상품명으로 검색
    fun findByProductNameContainingIgnoreCase(productName: String): List<Product>

    // 가격 범위로 검색
    fun findByProductPriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product>

    // 재고가 있는 상품 조회
    fun findByStockQuantityGreaterThan(stockQuantity: Int): List<Product>

    // 조회수 증가
    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.productId = :productId")
    fun incrementViewCount(@Param("productId") productId: Long): Int

    // 재고 수량 업데이트
    @Modifying
    @Query(
            "UPDATE Product p SET p.stockQuantity = :stockQuantity, p.modifiedBy = :modifiedBy WHERE p.productId = :productId"
    )
    fun updateStockQuantity(
            @Param("productId") productId: Long,
            @Param("stockQuantity") stockQuantity: Int,
            @Param("modifiedBy") modifiedBy: String
    ): Int
}
