package com.labb.vishinandroid

import com.labb.vishinandroid.data.util.CaptionUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class CaptionUtilsTest {

    @Test
    fun `extractNewText should return new words when exact overlap exists`() {
        // Arrange (Sätt upp testet)
        val oldText = "Jag ringer från"
        val newText = "Jag ringer från din bank"

        // Act (Kör funktionen vi vill testa)
        val result = CaptionUtils.extractNewText(oldText, newText)

        // Assert (Kontrollera att resultatet blev exakt vad vi förväntade oss)
        assertEquals("din bank", result)
    }

    @Test
    fun `extractNewText should ignore punctuation and uppercase letters`() {
        val oldText = "hej jag heter"
        // Android uppdaterar och lägger till stor bokstav och kommatecken retroaktivt
        val newText = "Hej, jag heter Oskar."

        val result = CaptionUtils.extractNewText(oldText, newText)

        assertEquals("Oskar.", result)
    }

    @Test
    fun `extractNewText should return full new text when there is no overlap`() {
        val oldText = "Tidigare samtal"
        val newText = "Helt ny mening här"

        val result = CaptionUtils.extractNewText(oldText, newText)

        assertEquals("Helt ny mening här", result)
    }

    @Test
    fun `extractNewText should handle empty old text correctly`() {
        val oldText = ""
        val newText = "Första meningen"

        val result = CaptionUtils.extractNewText(oldText, newText)

        assertEquals("Första meningen", result)
    }
}