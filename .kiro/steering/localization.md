# Localization

Primary language: Persian (Farsi). App must be fully RTL-compatible.

## Language & Locale

- Default locale: `fa-IR`
- String resources in `res/values-fa/strings.xml`
- Fallback: `res/values/strings.xml` (Persian, not English)

## Text Direction

- Layout direction: RTL by default
- Set `android:supportsRtl="true"` in manifest
- Use `start`/`end` instead of `left`/`right` for margins and padding
- Icons with directional meaning (arrows, back buttons) must mirror in RTL

## Number Formatting

- Display prices and quantities in Persian digits: ۰۱۲۳۴۵۶۷۸۹
- Use `Locale("fa")` for number formatting
- Currency: Iranian Rial (ریال) or Toman (تومان) — follow backend convention
- Thousand separator: ٬ (Arabic thousands separator)

## Date & Calendar

- Use Jalali (Persian/Solar Hijri) calendar for all date displays
- Library suggestion: PersianDate or any Jalali-compatible library
- Date format: `yyyy/MM/dd` (e.g., ۱۴۰۳/۱۰/۰۲)
- Weekday names in Persian

## Phone Numbers

- Format: Iranian mobile numbers (09xxxxxxxxx)
- Validate 11-digit format starting with 09
- Display with Persian digits in UI, send Latin digits to API

## Typography

- Font: IranSansX supports Persian characters and digits
- Ensure proper rendering of Persian-specific characters (گ چ پ ژ)
- Line height: accommodate Persian script's vertical space needs

## Backend Sync

- API sends/receives Latin digits and ISO dates
- Convert to Persian format only in presentation layer
- Store dates as ISO 8601, display as Jalali
