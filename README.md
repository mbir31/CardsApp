# 📇 CardsApp — AI Business Card Scanner & Smart Contact Manager

[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(M3)-4285F4.svg?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-00C853.svg?style=flat)]()
[![OCR Engine](https://img.shields.io/badge/OCR-ML%20Kit%20%2B%20Gemini%20AI-FF6F00.svg?style=flat&logo=google)](https://ai.google.dev/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

### 💼 *Tired of a wallet stuffed with physical business cards?*
> **Transform your bulky wallet into an organized, digital business contact vault!**  
> **CardsApp** lets you digitize, organize, search, and export physical business cards in seconds. Scan cards on the go, automatically extract contact info using high-precision Multilingual AI OCR, and save them straight to your phone's address book with a single tap.

---

## 🔥 Key Features & Capabilities

### 🧠 1. Hybrid Multilingual AI OCR Engine
* **Offline-First Speed + AI Accuracy**: Instant local text extraction via **Google ML Kit**, coupled with optional cloud AI enhancement using **Gemini 3.1 Pro / Flash**.
* **Multilingual Recognition**: Full support for complex multilingual business cards including **Bangla (বাংলা)**, English, Spanish, French, German, Japanese, and more.
* **Smart Field Extraction**: Automatically parses:
  * 👤 Full Name & Professional Title
  * 🏢 Company & Organization Name
  * 📞 Phone Numbers & WhatsApp contacts
  * ✉️ Email Addresses
  * 🌐 Websites & Social Links
  * 📍 Physical Addresses

---

### 📤 2. Comprehensive Export & 1-Tap Sharing Suite
Never get caught without a way to share contacts. Every card in your vault can be exported or shared in multiple industry-standard formats:

| Format | Description & Use Case |
| :--- | :--- |
| 📱 **Phone Contacts Sync** | 1-tap direct integration to your native Android System Phone Book (`ContactsContract`). |
| 🔳 **Dynamic QR Code** | Generates an instant, high-contrast QR code scannable by *any* mobile camera for instant import. |
| 🎴 **vCard (.vcf)** | Export standard contact files compatible with iOS, Android, Gmail, Outlook, and Apple Contacts. |
| 📊 **CSV Export (.csv)** | Export structured spreadsheet records ideal for Excel, Google Sheets, or CRM databases. |
| 📷 **Scanned Card Image** | Share clean, high-resolution original card images for visual reference. |

---

### 📂 3. Smart Vault & Organization
* **Category Tagging**: Filter contacts by custom categories (*Professional*, *Office*, *Business*, *Family*, *Personal*).
* **Starred Priority Contacts**: Bookmark key leads, executives, and clients for instant access.
* **Instant Instant Search**: Search through names, companies, job titles, or emails instantly.
* **Direct Quick Actions**: Initiate calls, launch email clients, open company websites, or view office locations on Google Maps with a single touch.

---

## 📱 User Manual & Usage Guide

```
┌─────────────────┐     ┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   1. Capture    │ ──> │   2. AI Review   │ ──> │ 3. Vault Storage │ ──> │ 4. Export / Sync │
│ Camera / Gallery│     │ Verify & Edit    │     │ Search & Filter  │     │ vCard/QR/System  │
└─────────────────┘     └──────────────────┘     └──────────────────┘     └──────────────────┘
```

### Step 1: Scan or Upload a Business Card
1. Open **CardsApp** and tap the **Scan Card** tab.
2. Position the business card within the camera viewport framing box and tap **Capture**, or tap **Gallery** to select an existing photo.

### Step 2: AI OCR Extraction & Review
1. The AI engine automatically extracts all contact details.
2. Review the structured fields (Name, Title, Company, Phone, Email, Address).
3. Optionally select a target **Category** or mark the contact as **Starred**.
4. Tap **Save to Vault** or **Save & Add to System Contacts**.

### Step 3: Manage & Search Your Vault
1. Navigate to the **Vault** tab to view your complete digital card collection.
2. Use the **Search bar** to find contacts by name, company, or job title.
3. Tap on any card category pill to filter your vault list.

### Step 4: Share & Export Contacts
1. Tap any card in the Vault to open the **Card Details** screen.
2. Choose your preferred export option:
   * **Save / Edit on Phone Contacts**: Saves the contact directly into your native phone address book.
   * **QR Code**: Displays a scannable QR Code modal with a 1-tap share image option.
   * **vCard**: Shares a `.vcf` file directly via WhatsApp, Email, or Drive.
   * **CSV**: Generates a `.csv` data table.
   * **Image**: Shares the captured physical card photo.

---

## 🛠 Tech Stack & Architecture

* **Language**: [Kotlin](https://kotlinlang.org/) (100% modern idiomatic Kotlin)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3 Dark Palette)
* **Architecture**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (StateFlow / SharedFlow)
* **Database**: [Room Database](https://developer.android.com/training/data-storage/room) (Offline-First local storage)
* **OCR & AI**:
  * [Google ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition)
  * [Google Gemini AI Vision API](https://ai.google.dev/)
* **QR Generation**: [ZXing Core](https://github.com/zxing/zxing)
* **Image Loading**: [Coil Compose](https://coil-kt.github.io/coil/)

---

## 🚀 Getting Started for Developers

### Prerequisites
* Android Studio Ladybug (2024.2.1+) or newer
* JDK 17+
* Android SDK 24+ (Android 7.0 Nougat minimum)

### Build & Run
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/CardsApp.git
   cd CardsApp
   ```
2. **Open in Android Studio**:
   Open the project directory in Android Studio and let Gradle sync.
3. **Build & Run**:
   Select your connected device or emulator and click **Run** (`Shift + F10`).

---

## 🛡 License

Distributed under the MIT License. See `LICENSE` for more information.

---

<p center="align">
Made with ❤️ using Kotlin & Jetpack Compose.
</p>
