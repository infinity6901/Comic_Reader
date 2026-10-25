package com.infinity.comicreader.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SpreadLayoutTest {
    @Test fun oddPageCountKeepsFinalPageInItsOwnSpread() {
        assertEquals(0, SpreadLayout.itemCount(0))
        assertEquals(1, SpreadLayout.itemCount(1))
        assertEquals(2, SpreadLayout.itemCount(3))
        assertEquals(3, SpreadLayout.itemCount(5))
    }

    @Test fun leftToRightSpreadsMapConsecutivePages() {
        assertEquals(0, SpreadLayout.firstPage(0, rtl = false))
        assertEquals(1, SpreadLayout.secondPage(0, rtl = false))
        assertEquals(2, SpreadLayout.firstPage(1, rtl = false))
        assertEquals(3, SpreadLayout.secondPage(1, rtl = false))
    }

    @Test fun rightToLeftSpreadsReverseVisualOrderButNotProgress() {
        assertEquals(1, SpreadLayout.firstPage(0, rtl = true))
        assertEquals(0, SpreadLayout.secondPage(0, rtl = true))
        assertEquals(2, SpreadLayout.firstPageForProgress(1))
        assertEquals(1, SpreadLayout.itemForPage(3))
    }
}
