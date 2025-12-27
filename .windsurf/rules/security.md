---
trigger: always_on
---
# Security

## Authentication Flow

Backend uses JWT + OTP via SMS. Reference: `/home/erfan/Projects/shop/handlers/otp.go`

### Token Structure
- Access token: 24-hour expiry
- Refresh token: 7-day expiry
- Format: Bearer token in `Authorization` header

### Auth Endpoints
| Endpoint | Purpose |
|----------|---------|
| `POST /api/auth/signup/send-otp` | Send OTP for registration |
| `POST /api/auth/signup/verify-otp` | Verify OTP and create account |
| `POST /api/auth/login` | Login with phone + password |
| `POST /api/auth/forgot-password/send-otp` | Password reset OTP |
| `POST /api/auth/refresh-token` | Refresh access token |

## Token Storage

- Store tokens in EncryptedSharedPreferences only
- Never store tokens in plain SharedPreferences, databases, or files
- Clear tokens on logout and app data clear
- Never log tokens or include in crash reports

## Network Security

- Use HTTPS only (`https://voxcina.com`)
- Implement certificate pinning for production
- Add network security config to block cleartext traffic
- Use OkHttp interceptor for automatic token injection and refresh

## Token Refresh Strategy

1. Intercept 401 responses with `TOKEN_EXPIRED` error code
2. Call refresh token endpoint
3. Retry original request with new token
4. If refresh fails, force logout and redirect to login

## OTP Handling

- Use SMS Retriever API for auto-read (no SMS permission needed)
- Support manual entry fallback
- Handle rate limiting (2-minute cooldown between requests)
- Convert Persian digits to Latin before sending to API

## Biometric Authentication

- Optional: Enable biometric unlock after initial login
- Store encrypted token reference in AndroidKeyStore
- Fallback to PIN/password if biometric unavailable
- Re-authenticate with server after biometric unlock

## Data Protection

- Encrypt sensitive local data (user profile, addresses)
- Clear sensitive data from memory after use
- Disable screenshots on sensitive screens (login, payment)
- Exclude sensitive fields from backup (`android:allowBackup="false"`)

## Input Validation

- Validate phone format (09xxxxxxxxx) before API calls
- Sanitize all user inputs
- Validate password strength client-side (min 8 chars, mixed case, numbers)
- Never trust client-side validation alone — backend validates too

## Payment Security

- Never store card details locally
- Redirect to Zibal gateway for payment (no card handling in app)
- Verify payment status with backend after callback
- Use HTTPS callback URLs only

## ProGuard / R8 Rules

- Obfuscate release builds
- Keep data models for JSON serialization
- Remove logging in release builds

## Security Checklist

- [ ] EncryptedSharedPreferences for tokens
- [ ] Certificate pinning configured
- [ ] Network security config blocks cleartext
- [ ] Biometric auth implemented (optional)
- [ ] Screenshots disabled on auth/payment screens
- [ ] ProGuard enabled for release
- [ ] No sensitive data in logs
- [ ] Token refresh interceptor working
