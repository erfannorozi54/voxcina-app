---
trigger: always_on
---
# UI Standards

Visual parity with Voxcina web app. Reference: `/home/erfan/Projects/shop/front_end/tailwind.config.js` and `src/app/globals.css`.

## Brand Colors

| Token | Hex | Usage |
|-------|-----|-------|
| Primary | `#1A3C69` | Buttons, links, active states |
| Primary Dark | `#0A1B3C` | Headers, emphasis |
| Secondary (Cream) | `#F4F1EC` | Backgrounds, cards |
| Secondary Light | `#FCFAF8` | Page backgrounds |
| Destructive | `#EF4444` | Errors, delete actions |
| Success | `#10B981` | Confirmations, success states |
| Warning | `#F59E0B` | Alerts, cautions |

Primary color scale: 100 (`#E6EDF5`) → 600 (`#1A3C69`) → 900 (`#0A1B3C`)

## Typography

- Font family: IranSansX (Persian) — bundle in `res/font/`
- Weights: Light (300), Regular (400), Bold (700)
- Scale: H1 (30sp), H2 (24sp), H3 (20sp), Body (14sp), Caption (12sp)

## Spacing & Radius

- Base radius: 8dp (buttons, inputs)
- Large radius: 16dp (cards, modals)
- Extra large: 24dp (bottom sheets)
- Standard padding: 16dp
- Content spacing: 8dp, 12dp, 16dp, 24dp

## Elevation

- Soft: 4dp — cards, list items
- Medium: 8dp — dialogs, dropdowns
- Strong: 12dp — modals, FABs

## UI Development Rules

1. **Reusable Components**: Create shared composables for buttons, cards, inputs, and typography in a `ui/components` package. All screens must use these shared components.

2. **Theme Consistency**: Define all colors, typography, and shapes in a central theme. Never hardcode style values in individual screens.

3. **Single Source of Truth**: Extract repeated UI patterns (product cards, list items, headers) into dedicated composables.

4. **RTL First**: Default layout direction is RTL for Persian. Ensure all layouts support bidirectional text.

5. **State-Driven UI**: Use sealed classes for UI states (Loading, Success, Error, Empty). Each screen should handle all states consistently.

6. **Accessibility**: Provide content descriptions, sufficient touch targets (48dp minimum), and proper contrast ratios.

7. **Responsive Design**: Support different screen sizes using adaptive layouts. Avoid fixed dimensions where possible.
