---
trigger: always_on
---
# Architecture

## Tech Stack
- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM with Clean Architecture layers
- DI: Hilt
- Networking: Retrofit + OkHttp
- Async: Kotlin Coroutines + Flow
- Navigation: Compose Navigation

## Project Structure
```
app/src/main/java/
├── data/           # Repositories, data sources, DTOs
├── domain/         # Use cases, domain models
├── presentation/   # ViewModels, Compose screens, UI state
├── di/             # Hilt modules
└── util/           # Extensions, helpers
```

## Backend Integration

This app consumes the backend at `/home/erfan/Projects/shop/`.

### Backend Reference Guide

| Information Needed | Inspect |
|-------------------|---------|
| API routes & endpoints | `routes/routes.go` |
| Request/response handlers | `handlers/*.go` |
| Data models & schemas | `models/*.go` |
| Auth & middleware logic | `middlewares/auth.go` |
| AI/chat services | `services/customer_ai_service.go`, `services/chat_service.go` |
| Payment integration | `services/zibal_service.go`, `handlers/payment.go` |
| Environment config | `.env.example` |

### Base URLs
- Production: `https://voxcina.com/api`

### Deployment
The backend is deployed on `voxcina.com` with Nginx proxying requests to the Go backend.
- Nginx config: `/home/erfan/Projects/shop/nginx-voxcina-optimized.conf`

## Rules

1. **Before implementing any feature, inspect the corresponding logic in `/home/erfan/Projects/shop/` to ensure data model consistency.**

2. Follow Modern Android Development (MAD) best practices.

3. Use sealed classes for UI state management.

4. Handle API errors with proper error states and user feedback.

5. Store auth tokens securely using EncryptedSharedPreferences.
