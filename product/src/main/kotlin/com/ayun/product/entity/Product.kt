package com.ayun.product.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    var productId: Long? = null,
    @Column(name = "product_name", nullable = false, length = 255) var productName: String,
    @Column(name = "view_count", nullable = false) var viewCount: Long = 0L,
    @Column(name = "stock_quantity", nullable = false) var stockQuantity: Int = 0,
    @Column(name = "product_price", nullable = false, precision = 10, scale = 2)
    var productPrice: BigDecimal,
) : BaseEntity()
