package com.voxcina.shop.domain.model

/**
 * Domain model representing an order.
 */
data class Order(
    val id: String,
    val userId: String,
    val orderNumber: String,
    val items: List<OrderItem>,
    val totalAmount: Long,
    val shippingCost: Long,
    val discountAmount: Long,
    val shippingAddress: ShippingAddress,
    val status: OrderStatus,
    val statusText: String,
    val paymentStatus: PaymentStatus,
    val paymentMethod: String?,
    val createdAt: String,
    val updatedAt: String,
    val jalaliCreatedAt: String?,
    val jalaliUpdatedAt: String?,
    val productCount: Int
)

/**
 * Domain model for an order item.
 */
data class OrderItem(
    val product: OrderProduct,
    val variant: OrderVariant,
    val quantity: Int,
    val priceAtPurchase: Long
)

/**
 * Domain model for product info in an order item.
 */
data class OrderProduct(
    val id: String,
    val name: String,
    val image: String?
)

/**
 * Domain model for variant info in an order item.
 */
data class OrderVariant(
    val size: String,
    val color: String,
    val colorName: String,
    val sku: String
)

/**
 * Domain model for shipping address in an order.
 */
data class ShippingAddress(
    val title: String?,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val province: String?,
    val provinceCode: Int,
    val city: String,
    val cityCode: Int,
    val address: String?,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double
) {
    /**
     * Returns the full name combining first and last name.
     */
    val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    
    /**
     * Returns the full address string.
     */
    val fullAddress: String
        get() = listOfNotNull(province, city, address)
            .filter { it.isNotBlank() }
            .joinToString("، ")
}

/**
 * Enum representing order status values.
 */
enum class OrderStatus(val value: String, val displayName: String) {
    PENDING("pending", "در انتظار پردازش"),
    PROCESSING("processing", "در حال پردازش"),
    SHIPPED("shipped", "ارسال شده"),
    DELIVERED("delivered", "تحویل داده شده"),
    CANCELLED("cancelled", "لغو شده");
    
    companion object {
        fun fromString(value: String): OrderStatus {
            return entries.find { it.value == value.lowercase() } ?: PENDING
        }
    }
}

/**
 * Enum representing payment status values.
 */
enum class PaymentStatus(val value: String, val displayName: String) {
    PENDING("pending", "در انتظار پرداخت"),
    PAID("paid", "پرداخت شده"),
    FAILED("failed", "پرداخت ناموفق");
    
    companion object {
        fun fromString(value: String): PaymentStatus {
            return entries.find { it.value == value.lowercase() } ?: PENDING
        }
    }
}
