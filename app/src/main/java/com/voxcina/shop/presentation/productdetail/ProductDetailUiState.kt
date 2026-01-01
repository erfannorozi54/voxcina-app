package com.voxcina.shop.presentation.productdetail

import com.voxcina.shop.domain.model.ColorVariant
import com.voxcina.shop.domain.model.ProductDetail
import com.voxcina.shop.domain.model.ProductReview
import com.voxcina.shop.domain.model.SizeVariant

/**
 * Sealed interface representing all possible UI states for the Product Details screen.
 *
 * Requirements: 11.1, 11.2, 11.3
 */
sealed interface ProductDetailUiState {

    /**
     * Loading state when the product details screen first loads.
     * Displays shimmer loading skeleton for image, header, price, colors, sizes.
     *
     * Requirements: 11.1
     */
    data object Loading : ProductDetailUiState

    /**
     * Success state containing full product details with user selections.
     *
     * Requirements: 1.1, 5.2, 5.3, 5.4, 6.2, 6.4, 10.3, 10.4, 10.5
     */
    data class Success(
        val product: ProductDetail,
        val selectedColorVariant: ColorVariant,
        val selectedSize: SizeVariant?,
        val quantity: Int,
        val isFavorite: Boolean,
        val isAddingToCart: Boolean,
        val addToCartError: String?,
        val displayImages: List<String>,
        val isDescriptionExpanded: Boolean,
        val reviews: List<ProductReview> = emptyList(),
        val isLoadingReviews: Boolean = false,
        val isSubmittingReview: Boolean = false,
        val showAddReviewSheet: Boolean = false
    ) : ProductDetailUiState {

        /**
         * Returns the maximum quantity available for the selected size.
         * Returns 0 if no size is selected.
         */
        val maxQuantity: Int
            get() = selectedSize?.quantity ?: 0

        /**
         * Returns true if the add-to-cart button should be enabled.
         * Requires a size to be selected and not currently adding to cart.
         */
        val canAddToCart: Boolean
            get() = selectedSize != null && !isAddingToCart && maxQuantity > 0

        /**
         * Returns the discount percentage if product has a discount.
         * Returns null if no discount.
         */
        val discountPercentage: Int?
            get() {
                val original = product.originalPrice ?: return null
                if (original <= product.price) return null
                return ((original - product.price) * 100 / original).toInt()
            }

        /**
         * Returns true if the product has a discount.
         */
        val hasDiscount: Boolean
            get() = product.originalPrice != null && product.originalPrice > product.price

        /**
         * Returns the list of sizes available for the selected color variant.
         */
        val availableSizes: List<SizeVariant>
            get() = selectedColorVariant.sizes

        /**
         * Checks if a color variant is available (has at least one size with quantity > 0).
         */
        fun isColorAvailable(colorVariant: ColorVariant): Boolean {
            return colorVariant.sizes.any { it.quantity > 0 }
        }

        /**
         * Checks if a size variant is available (has quantity > 0).
         */
        fun isSizeAvailable(sizeVariant: SizeVariant): Boolean {
            return sizeVariant.quantity > 0
        }
    }

    /**
     * Error state when the product fails to load.
     * Displays error message with retry button.
     *
     * Requirements: 11.2
     */
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : ProductDetailUiState

    /**
     * Not found state when the product doesn't exist.
     * Displays "محصول یافت نشد" message with back button.
     *
     * Requirements: 11.3
     */
    data object NotFound : ProductDetailUiState
}

/**
 * Events that can be triggered from the Product Details screen UI.
 */
sealed class ProductDetailEvent {

    /**
     * User tapped retry on error state.
     */
    data object Retry : ProductDetailEvent()

    /**
     * User selected a different color variant.
     *
     * Requirements: 5.2, 5.3, 5.4
     */
    data class SelectColor(val colorVariant: ColorVariant) : ProductDetailEvent()

    /**
     * User selected a size variant.
     *
     * Requirements: 6.2, 6.4
     */
    data class SelectSize(val sizeVariant: SizeVariant) : ProductDetailEvent()

    /**
     * User changed the quantity.
     *
     * Requirements: 10.3, 10.4
     */
    data class ChangeQuantity(val quantity: Int) : ProductDetailEvent()

    /**
     * User tapped add to cart button.
     *
     * Requirements: 10.5, 10.6
     */
    data object AddToCart : ProductDetailEvent()

    /**
     * User tapped favorite button.
     *
     * Requirements: 2.3
     */
    data object ToggleFavorite : ProductDetailEvent()

    /**
     * User tapped share button.
     *
     * Requirements: 2.4
     */
    data object Share : ProductDetailEvent()

    /**
     * User tapped "read more" on description.
     *
     * Requirements: 8.3
     */
    data object ToggleDescription : ProductDetailEvent()

    /**
     * User tapped back button.
     *
     * Requirements: 2.2
     */
    data object NavigateBack : ProductDetailEvent()

    /**
     * User tapped "view all reviews" button.
     *
     * Requirements: 9.3
     */
    data object ViewAllReviews : ProductDetailEvent()

    /**
     * Clear add-to-cart error message.
     */
    data object ClearError : ProductDetailEvent()

    /**
     * User tapped "add review" button.
     */
    data object ShowAddReview : ProductDetailEvent()

    /**
     * User dismissed the add review sheet.
     */
    data object DismissAddReview : ProductDetailEvent()

    /**
     * User submitted a review.
     */
    data class SubmitReview(
        val rating: Int,
        val comment: String,
        val isRecommended: Boolean
    ) : ProductDetailEvent()
}
