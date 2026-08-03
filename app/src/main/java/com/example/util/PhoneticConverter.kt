package com.example.util

/**
 * Phonetic Converter & Transliteration Helper.
 * Converts Bangla / non-English script names and company names into phonetic English
 * as mandated by user specifications ("Every card on the list will be in English,
 * in case of Bangla or other languages, name and company name will be translated
 * or converted into English phonetically").
 */
object PhoneticConverter {

    private val banglaToEnglishMap = mapOf(
        'অ' to "o", 'আ' to "a", 'ই' to "i", 'ঈ' to "i", 'উ' to "u", 'ঊ' to "u", 'ঋ' to "ri",
        'এ' to "e", 'ঐ' to "oi", 'ও' to "o", 'ঔ' to "ou",
        'ক' to "k", 'খ' to "kh", 'গ' to "g", 'ঘ' to "gh", 'ঙ' to "ng",
        'চ' to "ch", 'ছ' to "chh", 'জ' to "j", 'ঝ' to "jh", 'ঞ' to "n",
        'ট' to "t", 'ঠ' to "th", 'ড' to "d", 'ঢ' to "dh", 'ণ' to "n",
        'ত' to "t", 'থ' to "th", 'দ' to "d", 'ধ' to "dh", 'ন' to "n",
        'প' to "p", 'ফ' to "f", 'ব' to "b", 'ভ' to "v", 'ম' to "m",
        'য' to "j", 'র' to "r", 'ল' to "l", 'শ' to "sh", 'ষ' to "sh",
        'স' to "s", 'হ' to "h", 'ড়' to "r", 'ঢ়' to "rh", 'য়' to "y",
        'া' to "a", 'ি' to "i", 'ী' to "ee", 'ু' to "u", 'ূ' to "oo",
        'ৃ' to "ri", 'ে' to "e", 'ৈ' to "oi", 'ো' to "o", 'ৌ' to "ou",
        '্' to "", 'ং' to "ng", 'ঃ' to "h", 'ঁ' to "n"
    )

    private val commonBanglaNames = mapOf(
        "আরিফুল" to "Ariful",
        "ইসলাম" to "Islam",
        "রহিম" to "Rahim",
        "আহমেদ" to "Ahmed",
        "মহসিন" to "Mohsin",
        "খান" to "Khan",
        "চৌধুরী" to "Chowdhury",
        "হোসেন" to "Hossain",
        "হাসান" to "Hasan",
        "ড্যাফোডিল" to "Daffodil",
        "টেকনোলজি" to "Technology",
        "গ্রামীণফোন" to "Grameenphone",
        "বাংলালিংক" to "Banglalink",
        "রবি" to "Robi",
        "ব্যাংক" to "Bank",
        "লিমিটেড" to "Limited"
    )

    fun containsBangla(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        return text.any { char -> char in '\u0980'..'\u09FF' }
    }

    fun toPhoneticEnglish(text: String?): String {
        if (text.isNullOrBlank()) return ""
        val nonNullText = text.trim()
        if (!containsBangla(nonNullText)) return nonNullText

        // Check word-by-word against common dictionary
        val words = nonNullText.split("\\s+".toRegex())
        val convertedWords = words.map { word ->
            val cleanWord = word.trim()
            commonBanglaNames[cleanWord] ?: transliterateWord(cleanWord)
        }

        val result = convertedWords.joinToString(" ").trim()
        return if (result.isNotBlank()) capitalizeWords(result) else nonNullText
    }

    private fun transliterateWord(word: String): String {
        val sb = StringBuilder()
        for (ch in word) {
            val mapped = banglaToEnglishMap[ch]
            if (mapped != null) {
                sb.append(mapped)
            } else if (ch.code in 32..126) {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    private fun capitalizeWords(str: String): String {
        return str.split(" ").joinToString(" ") { w ->
            if (w.isNotEmpty()) w.substring(0, 1).uppercase() + w.substring(1).lowercase() else ""
        }
    }
}
