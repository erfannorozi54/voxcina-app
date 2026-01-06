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

/**
 * Review-specific errors for review operations.
 */
sealed class ReviewError : AppError() {
    data object ReviewLoadFailed : ReviewError() {
        override val message: String = "خطا در بارگذاری نظرات"
    }

    data object ReviewSubmitFailed : ReviewError() {
        override val message: String = "خطا در ثبت نظر"
    }

    data object InvalidRating : ReviewError() {
        override val message: String = "امتیاز باید بین ۱ تا ۵ باشد"
    }

    data object NotAuthenticated : ReviewError() {
        override val message: String = "برای ثبت نظر باید وارد شوید"
    }
}

/**
 * Profile-specific errors for profile operations.
 */
sealed class ProfileError : AppError() {
    data object ProfileNotFound : ProfileError() {
        override val message: String = "پروفایل یافت نشد"
    }

    data object ProfileLoadFailed : ProfileError() {
        override val message: String = "خطا در بارگذاری پروفایل"
    }

    data object NotAuthenticated : ProfileError() {
        override val message: String = "برای مشاهده پروفایل باید وارد شوید"
    }

    data object LogoutFailed : ProfileError() {
        override val message: String = "خطا در خروج از حساب کاربری"
    }
}

/**
 * Address-specific errors for address operations.
 */
sealed class AddressError : AppError() {
    data object AddressesLoadFailed : AddressError() {
        override val message: String = "خطا در بارگذاری آدرس‌ها"
    }

    data object AddressNotFound : AddressError() {
        override val message: String = "آدرس یافت نشد"
    }

    data object AddressSaveFailed : AddressError() {
        override val message: String = "خطا در ذخیره آدرس"
    }

    data object AddressDeleteFailed : AddressError() {
        override val message: String = "خطا در حذف آدرس"
    }

    data object InvalidAddress : AddressError() {
        override val message: String = "اطلاعات آدرس نامعتبر است"
    }

    data object NotAuthenticated : AddressError() {
        override val message: String = "برای مدیریت آدرس‌ها باید وارد شوید"
    }
}


/**
 * FAQ-specific errors for FAQ operations.
 */
sealed class FaqError : AppError() {
    data object FaqLoadFailed : FaqError() {
        override val message: String = "خطا در بارگذاری سوالات متداول"
    }
}

/**
 * Ticket-specific errors for ticket operations.
 */
sealed class TicketError : AppError() {
    data object TicketsLoadFailed : TicketError() {
        override val message: String = "خطا در بارگذاری تیکتها"
    }
    data object TicketNotFound : TicketError() {
        override val message: String = "تیکت یافت نشد"
    }
    data object TicketCreateFailed : TicketError() {
        override val message: String = "خطا در ایجاد تیکت"
    }
    data object MessageSendFailed : TicketError() {
        override val message: String = "خطا در ارسال پیام"
    }
}

/**
 * Shipping-specific errors for shipping operations.
 */
sealed class ShippingError : AppError() {
    data object ShippingQuotesLoadFailed : ShippingError() {
        override val message: String = "خطا در دریافت روش‌های ارسال"
    }
    data object InvalidCityCode : ShippingError() {
        override val message: String = "کد شهر نامعتبر است"
    }
    data object NoShippingMethodsAvailable : ShippingError() {
        override val message: String = "روش ارسالی برای این مقصد موجود نیست"
    }
}

/**
 * Checkout-specific errors for checkout operations.
 */
sealed class CheckoutError : AppError() {
    data object OrderCreationFailed : CheckoutError() {
        override val message: String = "خطا در ثبت سفارش. لطفاً دوباره تلاش کنید"
    }
    data object MissingAddress : CheckoutError() {
        override val message: String = "لطفاً آدرس تحویل را انتخاب کنید"
    }
    data object MissingShippingMethod : CheckoutError() {
        override val message: String = "لطفاً روش ارسال را انتخاب کنید"
    }
    data object InvalidCardDetails : CheckoutError() {
        override val message: String = "اطلاعات کارت نامعتبر است"
    }
    data object EmptyCart : CheckoutError() {
        override val message: String = "سبد خرید خالی است"
    }
    data object NotAuthenticated : CheckoutError() {
        override val message: String = "برای تکمیل خرید باید وارد شوید"
    }
}
