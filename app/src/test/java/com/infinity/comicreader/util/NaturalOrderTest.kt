package com.infinity.comicreader.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NaturalOrderTest {
    @Test fun sortsNumberedPagesNumerically() {
        val input = listOf("page10.jpg", "page2.jpg", "page1.jpg", "page12.jpg", "page3.jpg")
        assertEquals(
            listOf("page1.jpg", "page2.jpg", "page3.jpg", "page10.jpg", "page12.jpg"),
            input.sortedWith(NaturalOrder)
        )
    }

    @Test fun comparesDigitRunsWithoutIntegerOverflow() {
        val input = listOf("page999999999999999999999.jpg", "page1000000000000000000000.jpg", "page9.jpg")
        assertEquals(
            listOf("page9.jpg", "page999999999999999999999.jpg", "page1000000000000000000000.jpg"),
            input.sortedWith(NaturalOrder)
        )
    }

    @Test fun comparesCaseInsensitively() {
        assertEquals(0, NaturalOrder.compare("Cover.JPG", "cover.jpg"))
    }

    @Test fun handlesLeadingZerosAndMixedNumberRuns() {
        val input = listOf("ch10-page2.jpg", "ch2-page10.jpg", "ch02-page1.jpg", "ch2-page2.jpg")
        assertEquals(
            listOf("ch02-page1.jpg", "ch2-page2.jpg", "ch2-page10.jpg", "ch10-page2.jpg"),
            input.sortedWith(NaturalOrder)
        )
    }

    @Test fun shorterPrefixSortsFirst() {
        assertEquals(-1, NaturalOrder.compare("page", "page1"))
        assertEquals(1, NaturalOrder.compare("page1", "page"))
    }
}
