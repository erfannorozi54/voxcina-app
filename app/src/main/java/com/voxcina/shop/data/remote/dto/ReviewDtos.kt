package com.voxcina.shop.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.voxcina.shop.domain.model.ProductReview

/**
 * DTO for review response from GET /api/products/{productId}/reviews
 */
data class ReviewDto(
    @SerializedName("id") val id: IdWrapper?,
    @SerializedName("user_id") val userId: IdWrapper?,
    @SerializedName("user_name") val userName: String,
    @SerializedName("product_id") val productId: IdWrapper?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String,
    @SerializedName("isRecommended") val isRecommended: Boolean?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String
)

/**
 * Wrapper for MongoDB ObjectID which can be serialized as object with $oid field
 */
data class IdWrapper(
    @SerializedName("\$oid") val oid: String?
)

/**
 * Request payload for POST /api/products/{productId}/reviews
 */
data class AddReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String,
    @SerializedName("isRecommended") val isRecommended: Boolean
)

/**
 * Maps ReviewDto to domain ProductReview.
 */
fun ReviewDto.toDomain(): ProductReview = ProductReview(
    id = id?.oid ?: "",
    userId = userId?.oid ?: "",
    userName = userName,
    userAvatar = null,
    rating = rating,
    comment = comment,
    isRecommended = isRecommended ?: false,
    createdAt = createdAt
)
