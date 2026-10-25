package com.infinity.comicreader.util

/** Pure page-to-row mapping shared by the reader UI and its spread adapter. */
object SpreadLayout {
    fun itemCount(pageCount: Int): Int = (pageCount.coerceAtLeast(0) + 1) / 2
    fun itemForPage(page: Int): Int = page.coerceAtLeast(0) / 2
    fun firstPage(item: Int, rtl: Boolean): Int = item.coerceAtLeast(0) * 2 + if (rtl) 1 else 0
    fun secondPage(item: Int, rtl: Boolean): Int = item.coerceAtLeast(0) * 2 + if (rtl) 0 else 1
    fun firstPageForProgress(item: Int): Int = item.coerceAtLeast(0) * 2
}
