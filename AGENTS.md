# AGENTS.md

Single-module Android app (`:app`) — Kotlin + Jetpack Compose + Hilt, MVVM/Clean layers under `app/src/main/java/com/voxcina/shop/{data,domain,presentation,di,util}`.

## Build & verify

The default `JAVA_HOME` is a JRE-only JDK and Gradle fails. Export first:

```bash
export JAVA_HOME=/opt/android-studio/jbr
JAVA_HOME=/opt/android-studio/jbr ./gradlew :app:compileDebugKotlin   # fast compile check
JAVA_HOME=/opt/android-studio/jbr ./gradlew testDebugUnitTest        # unit tests
```

## Backend integration — read-only, reference only

The app consumes a Go backend at `/home/erfan/Projects/shop` (web front-end at `../shop/front_end`). **Never modify anything under `../shop`.** Bugs found there must be documented (a report file or code comment) and the user decides the fix — wait for their verdict.

- Ground truth for API/DTO work: `../shop/routes/routes.go`, `handlers/*.go`, `models/*.go`.
- Visual/UX parity reference: `../shop/front_end/src` (components + stores).
- Base URL is hardcoded: `ApiClient.BASE_URL = "https://voxcina.com/api/"` (`data/remote/ApiClient.kt`). Backend returns **relative image URLs**; prefix `https://voxcina.com` before loading (see `ProductCard.kt:124`, `CategorySection.kt:173`).

## Conventions & gotchas

- Gson with no field-naming policy: backend snake_case fields (`min_order_amount`, `used_count`) require `@SerializedName` in DTOs — omitting it silently yields nulls.
- RTL: the app is Persian-first, RTL-only (no `values-fa/` — Persian lives in the default `res/values/strings.xml`). Directional icons: `AutoMirrored` variants flip in RTL; for "forward" arrows in RTL use plain `Icons.Filled.KeyboardArrowLeft`/`ArrowBack` (their deprecation warnings are intentional — do not "fix" them).
- UI state: sealed classes per screen (Loading/Success/Error/Empty); error handling with user-visible messages.
- Font: `VazirMatnFamily` (`ui/theme/Type.kt`), fonts in `res/font/vazirmatn_*`. (.windsurf/rules claims IranSansX — stale.)
- Persian digits for display via `util/PersianDigitConverter.kt`; Latin digits over the API.
- Pre-existing compile warnings are tolerated: `hiltViewModel` deprecated, `Icons.Filled.ArrowBack` deprecated. Don't churn unrelated code.
- Domain/presentation split: backend `Discount` logic is mirrored by domain helpers (`Cart.discountAmountFor`) — keep the client's discount math identical to `../shop/handlers/orders.go` `calculateCheckoutDiscount`.

## Tests

JUnit Platform with Kotest (runner + property), autoscan disabled in `app/build.gradle.kts`. Only sample tests exist (`ExampleUnitTest.kt`); unit-testable helpers live under `util/` and `domain/`. Android instrumented tests need an emulator (`connectedAndroidTest`).

## Existing rules

`.windsurf/rules/*.md` (architecture, build-tools, localization, security, ui-standards, product) contain extra context but are not auto-loaded by OpenCode and some claims are stale — verify against code before trusting them.
