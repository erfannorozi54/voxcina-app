package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.voxcina.shop.domain.model.Order
import com.voxcina.shop.domain.model.OrderItem
import com.voxcina.shop.domain.model.OrderProduct
import com.voxcina.shop.domain.model.OrderStatus
import com.voxcina.shop.domain.model.OrderVariant
import com.voxcina.shop.domain.model.PaymentStatus
import com.voxcina.shop.domain.model.ShippingAddress

/**
 * Request DTO for creating an order (checkout).
 * POST /api/checkout
 */
data class CreateOrderRequestDto(
    @SerializedName("items") val items: List<OrderItemRequestDto>,
    @SerializedName("totalAmount") val totalAmount: Long,
    @SerializedName("shippingAddress") val shippingAddress: ShippingAddressRequestDto
)

/**
 * Request DTO for a single order item.
 */
data class OrderItemRequestDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("variant") val variant: OrderVariantRequestDto,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("priceAtPurchase") val priceAtPurchase: Long
)

/**
 * Request DTO for order item variant.
 */
data class OrderVariantRequestDto(
    @SerializedName("size") val size: String,
    @SerializedName("color") val color: String,
    @SerializedName("colorName") val colorName: String
)

/**
 * Request DTO for shipping address in order.
 */
data class ShippingAddressRequestDto(
    @SerializedName("title") val title: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("province") val province: String?,
    @SerializedName("province_code") val provinceCode: Int?,
    @SerializedName("city") val city: String,
    @SerializedName("city_code") val cityCode: Int?,
    @SerializedName("address") val address: String?,
    @SerializedName("postal_code") val postalCode: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

/**
 * Response DTO for order creation.
 */
data class OrderResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("order_number") val orderNumber: String,
    @SerializedName("items") val items: List<OrderItemResponseDto>,
    @SerializedName("total_amount") val totalAmount: Long,
    @SerializedName("shipping_cost") val shippingCost: Long,
    @SerializedName("discount_amount") val discountAmount: Long,
    @SerializedName("shipping_address") val shippingAddress: ShippingAddressResponseDto,
    @SerializedName("status") val status: String,
    @SerializedName("status_text") val statusText: String,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("payment_method") val paymentMethod: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("jalali_created_at") val jalaliCreatedAt: String?,
    @SerializedName("jalali_updated_at") val jalaliUpdatedAt: String?,
    @SerializedName("product_count") val productCount: Int
)

/**
 * Response DTO for order item.
 */
data class OrderItemResponseDto(
    @SerializedName("product") val product: OrderProductResponseDto,
    @SerializedName("variant") val variant: OrderVariantResponseDto,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price_at_purchase") val priceAtPurchase: Long
)

/**
 * Response DTO for product in order item.
 */
data class OrderProductResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?
)

/**
 * Response DTO for variant in order item.
 */
data class OrderVariantResponseDto(
    @SerializedName("size") val size: String,
    @SerializedName("color") val color: String,
    @SerializedName("colorName") val colorName: String?,
    @SerializedName("sku") val sku: String?
)

/**
 * Response DTO for shipping address in order.
 */
data class ShippingAddressResponseDto(
    @SerializedName("title") val title: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("province") val province: String?,
    @SerializedName("province_code") val provinceCode: Int?,
    @SerializedName("city") val city: String?,
    @SerializedName("city_code") val cityCode: Int?,
    @SerializedName("address") val address: String?,
    @SerializedName("postal_code") val postalCode: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?
)

// ============ Mapper Extension Functions ============

/**
 * Maps OrderResponseDto to Order domain model.
 */
fun OrderResponseDto.toDomain(): Order = Order(
    id = id,
    userId = userId,
    orderNumber = orderNumber,
    items = items.map { it.toDomain() },
    totalAmount = totalAmount,
    shippingCost = shippingCost,
    discountAmount = discountAmount,
    shippingAddress = shippingAddress.toDomain(),
    status = OrderStatus.fromString(status),
    statusText = statusText,
    paymentStatus = PaymentStatus.fromString(paymentStatus),
    paymentMethod = paymentMethod,
    createdAt = createdAt,
    updatedAt = updatedAt,
    jalaliCreatedAt = jalaliCreatedAt,
    jalaliUpdatedAt = jalaliUpdatedAt,
    productCount = productCount
)

/**
 * Maps OrderItemResponseDto to OrderItem domain model.
 */
fun OrderItemResponseDto.toDomain(): OrderItem = OrderItem(
    product = product.toDomain(),
    variant = variant.toDomain(),
    quantity = quantity,
    priceAtPurchase = priceAtPurchase
)

/**
 * Maps OrderProductResponseDto to OrderProduct domain model.
 */
fun OrderProductResponseDto.toDomain(): OrderProduct = OrderProduct(
    id = id,
    name = name,
    image = image
)

/**
 * Maps OrderVariantResponseDto to OrderVariant domain model.
 */
fun OrderVariantResponseDto.toDomain(): OrderVariant = OrderVariant(
    size = size,
    color = color,
    colorName = colorName ?: "",
    sku = sku ?: ""
)

/**
 * Maps ShippingAddressResponseDto to ShippingAddress domain model.
 */
fun ShippingAddressResponseDto.toDomain(): ShippingAddress = ShippingAddress(
    title = title,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    province = province,
    provinceCode = provinceCode ?: 0,
    city = city ?: "",
    cityCode = cityCode ?: 0,
    address = address,
    postalCode = postalCode ?: "",
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0
)
