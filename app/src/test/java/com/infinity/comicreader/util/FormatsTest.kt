package com.infinity.comicreader.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatsTest {
    @Test fun ignoresMacResourceForksAndMetadataDirectories() {
        assertFalse(Formats.isImageName("__MACOSX/._001.jpg"))
        assertFalse(Formats.isImageName("__MACOSX/001.jpg"))
        assertFalse(Formats.isImageName("folder/._001.jpg"))
        assertTrue(Formats.isImageName("Chapter 1/001.jpg"))
    }
}
