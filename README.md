# 📇 CardsApp — AI Business Card Scanner & Smart Contact Manager
### 🇧🇩 🇬🇧 স্মার্ট বিজনেস কার্ড স্ক্যানার ও এআই কন্টাক্ট ম্যানেজার

[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(M3)-4285F4.svg?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-00C853.svg?style=flat)]()
[![OCR Engine](https://img.shields.io/badge/OCR-ML%20Kit%20%2B%20Gemini%20AI-FF6F00.svg?style=flat&logo=google)](https://ai.google.dev/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

> ### 💼 *বিজনেস কার্ডের পাহাড়ে হারিয়ে যাচ্ছেন? (Struggling with paper business cards?)*
> **এখনই আপনার ম্যানুয়াল মানিব্যাগকে ডিজিটাল স্মার্ট ভল্টে রূপান্তর করুন!**  
> **CardsApp** is a professional-grade AI business card scanner that converts physical cards into digital contacts in seconds. Powered by high-accuracy **Multilingual AI OCR (Bangla + English support)**, offline Room database vault, 1-click Google Drive Cloud Backup, and seamless Phone Contacts sync!

---

## ✨ Highlights & Key Features (প্রধান আকর্ষণসমূহ)

### 🤖 1. AI OCR & Multilingual Scanning (স্মার্ট এআই স্ক্যানার)
* **Bangla & English Accuracy (বাংলা ও ইংরেজি স্ক্যান)**: Advanced hybrid OCR with **Google ML Kit** and **Gemini AI** accurately extracts text from both English and Bengali business cards.
* **Auto Field Parsing (স্বয়ংক্রিয় তথ্য সংগ্রহ)**: Automatically extracts Name, Job Title, Company, Mobile Numbers, Email, Website, and Address into organized contact fields.
* **Auto Auto-Crop & High-Res Capture**: High-definition camera integration with automatic perspective orientation fix.

---

### 📱 2. Seamless Phone Contacts Integration (ফোনে ডায়রেক্ট সেভ)
* **Direct Address Book Save**: Save scanned cards directly to your phone's native address book (`ContactsContract`) in 1 tap.
* **One-Tap Dial & Email**: Launch direct calls, WhatsApp chats, send emails, or open company office addresses on Google Maps straight from the app.

---

### ☁️ 3. Google Drive Backup & Cloud Sync (গুগল ড্রাইভ ক্লাউড ব্যাকআপ)
* **Never Lose a Contact**: Securely backup your entire digital card vault to your personal Google Drive account with 1 click.
* **Instant Restore**: Seamlessly restore all cards when switching to a new Android phone.

---

### 📤 4. Smart Sharing & Export Suite (১-ট্যাপে শেয়ার ও এক্সপোর্ট)
* 🔳 **Dynamic QR Code Generator**: Generate scannable vCard QR codes to share contact details instantly without typing.
* 🎴 **vCard Export (.vcf)**: Standard vCard file export compatible with Android, iPhone, Gmail, and Outlook.
* 📊 **CSV Export (.csv)**: Export full spreadsheet databases for CRM or Excel.
* 📷 **Image Sharing**: Share original scanned business card pictures.

---

### 🗂️ 5. Smart Vault & Organization (স্মার্ট ডিজিটাল ভল্ট)
* **Categories & Tagging**: Filter cards by *Professional*, *Office*, *Business*, *Family*, or *Personal*.
* **Starred Favorites**: Pin key leads, top executives, and urgent clients for instant access.
* **Lightning Fast Search**: Find any contact by name, company, title, or keyword instantly.

---

## 🚀 How It Works (ব্যবহার পদ্ধতি)

```
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
## 1. Capture/Scan │ ──> │ 2. AI OCR Review │ ──> │ 3. Smart Vault   │ ──> │ 4. Export & Sync │
│ Camera / Gallery │     │ Verify & Edit    │     │ Search & Filter  │     │ Phone/vCard/Drive│
└──────────────────┘     └──────────────────┘     └──────────────────┘     └──────────────────┘
```

1. **Scan Card (কার্ড স্ক্যান করুন)**: Open the Scan tab and capture a photo or pick one from your gallery.
2. **AI Processing (এআই প্রসেসিং)**: The app automatically detects name, phone, email, and company details.
3. **Save & Sync (সেভ করুন)**: Store in your local encrypted Room Vault and sync to your Phone Contacts with a single tap.
4. **Share Anywhere (শেয়ার করুন)**: Generate QR codes or share vCard/CSV files instantly!

---

## 🛠 Tech Stack & Architecture (প্রযুক্তি ও আর্কিটেকচার)

* **Language**: [Kotlin](https://kotlinlang.org/) (100% Modern Idiomatic Code)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3 Dark Canvas)
* **Architecture**: MVVM + Clean Architecture with Coroutines & StateFlow
* **Database**: [Room Database](https://developer.android.com/training/data-storage/room) (Offline-First)
* **OCR & AI Engine**: Google ML Kit Vision + Gemini AI Vision
* **Cloud Sync**: Google Drive API Integration
* **Barcode & QR**: ZXing Engine

---

## 📱 Quick Setup for Developers (ডেভেলপার গাইড)

```bash
# Clone the repository
git clone https://github.com/your-username/CardsApp.git

# Open in Android Studio Ladybug (2024.2.1+)
# Build and run on device or emulator
```

---

<p align="center">
  <b>CardsApp</b> — Your Ultimate AI Business Card Assistant! 🚀<br>
  Made with ❤️ using Kotlin & Jetpack Compose.
</p>
