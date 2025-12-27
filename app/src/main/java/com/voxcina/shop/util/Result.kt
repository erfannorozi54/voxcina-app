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
