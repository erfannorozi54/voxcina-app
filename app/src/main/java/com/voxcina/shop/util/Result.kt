package com.voxcina.shop.util

/**
 * A generic wrapper class for handling success and error states.
 * Used throughout the app for consistent error handling.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
    
    fun errorOrNull(): AppError? = when (this) {
        is Success -> null
        is Error -> error
    }
    
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (AppError) -> Unit): Result<T> {
        if (this is Error) action(error)
        return this
    }
}

/**
 * Base sealed class for application errors.
 * Extended by domain-specific error types.
 */
sealed class AppError {
    abstract val message: String
    
    data class NetworkError(
        override val message: String = "خطا در اتصال به سرور"
    ) : AppError()
    
    data class ServerError(
        val code: Int,
        override val message: String
    ) : AppError()
    
    data class UnknownError(
        override val message: String = "خطای ناشناخته"
    ) : AppError()
}

/**
 * Auth-specific errors for authentication flows.
 */
sealed class AuthError : AppError() {
    data object InvalidCredentials : AuthError() {
        override val message: String = "شماره تلفن یا رمز عبور اشتباه است"
    }
    
    data object InvalidOtp : AuthError() {
        override val message: String = "کد تأیید اشتباه است"
    }
    
    data object OtpExpired : AuthError() {
        override val message: String = "کد تأیید منقضی شده است"
    }
    
    data object PhoneAlreadyExists : AuthError() {
        override val message: String = "این شماره قبلاً ثبت شده است"
    }
    
    data object PhoneNotFound : AuthError() {
        override val message: String = "کاربری با این شماره یافت نشد"
    }
    
    data class RateLimited(val retryAfterSeconds: Int) : AuthError() {
        override val message: String = "لطفاً ${retryAfterSeconds / 60} دقیقه دیگر تلاش کنید"
    }
    
    data object WeakPassword : AuthError() {
        override val message: String = "رمز عبور باید حداقل ۸ کاراکتر با حروف بزرگ، کوچک و عدد باشد"
    }
    
    data object PasswordMismatch : AuthError() {
        override val message: String = "رمز عبور و تکرار آن یکسان نیستند"
    }
    
    data object InvalidPhone : AuthError() {
        override val message: String = "شماره تلفن نامعتبر است"
    }
}

/**
 * Home screen specific errors.
 */
sealed class HomeError : AppError() {
    data object HeroImagesLoadFailed : HomeError() {
        override val message: String = "خطا در بارگذاری بنرها"
    }

    data object CategoriesLoadFailed : HomeError() {
        override val message: String = "خطا در بارگذاری دسته‌بندی‌ها"
    }

    data object ProductsLoadFailed : HomeError() {
        override val message: String = "خطا در بارگذاری محصولات"
    }

    data object FlashSaleLoadFailed : HomeError() {
        override val message: String = "خطا در بارگذاری پیشنهادات شگفت‌انگیز"
    }

    data object BrandsLoadFailed : HomeError() {
        override val message: String = "خطا در بارگذاری برندها"
    }
}


/**
 * Cart-specific errors for shopping cart operations.
 */
sealed class CartError : AppError() {
    data object CartLoadFailed : CartError() {
        override val message: String = "خطا در بارگذاری سبد خرید"
    }

    data object ItemUpdateFailed : CartError() {
        override val message: String = "خطا در بروزرسانی تعداد"
    }

    data object ItemRemoveFailed : CartError() {
        override val message: String = "خطا در حذف محصول"
    }

    data object ClearCartFailed : CartError() {
        override val message: String = "خطا در خالی کردن سبد خرید"
    }

    data object InsufficientStock : CartError() {
        override val message: String = "موجودی کافی نیست"
    }

    data class DiscountInvalid(val reason: String) : CartError() {
        override val message: String = "کد تخفیف نامعتبر است"
    }

    data class DiscountExpired(val code: String) : CartError() {
        override val message: String = "کد تخفیف منقضی شده است"
    }

    data class DiscountMinOrderNotMet(val minAmount: Long) : CartError() {
        override val message: String = "حداقل مبلغ سفارش رعایت نشده است"
    }
}

/**
 * Product-specific errors for product detail operations.
 */
sealed class ProductError : AppError() {
    data object ProductNotFound : ProductError() {
        override val message: String = "محصول یافت نشد"
    }

    data object ProductLoadFailed : ProductError() {
        override val message: String = "خطا در بارگذاری محصول"
    }

    data object InvalidProductId : ProductError() {
        override val message: String = "شناسه محصول نامعتبر است"
    }
}
