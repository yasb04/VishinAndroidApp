package com.labb.vishinandroid.data.util


object CaptionUtils {

    fun extractNewText(oldText: String, newText: String): String {
        if (oldText.isEmpty()) return newText

        val regex = Regex("[^a-zåäö0-9 ]")
        val simpleOld = oldText.lowercase().replace(regex, "")
        val simpleNew = newText.lowercase().replace(regex, "")

        val oldWords = simpleOld.split(" ").filter { it.isNotBlank() }
        val newWords = simpleNew.split(" ").filter { it.isNotBlank() }
        val actualNewWords = newText.split(" ").filter { it.isNotBlank() }

        val maxOverlap = minOf(oldWords.size, newWords.size)
        for (i in maxOverlap downTo 1) {
            val suffix = oldWords.subList(oldWords.size - i, oldWords.size)
            val prefix = newWords.subList(0, i)

            if (suffix == prefix) {
                return actualNewWords.subList(i, actualNewWords.size).joinToString(" ")
            }
        }
        return newText
    }
}