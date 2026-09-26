package com.example.rejournal.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StickerSerializerTest {

    @Test
    fun `empty list serializes to empty string and deserializes to empty list`() {
        val json = StickerSerializer.serialize(emptyList())
        assertEquals("", json)

        val deserialized = StickerSerializer.deserialize(json)
        assertTrue(deserialized.isEmpty())

        val deserializedNull = StickerSerializer.deserialize(null)
        assertTrue(deserializedNull.isEmpty())
    }

    @Test
    fun `single sticker roundtrip serialization`() {
        val sticker = NoteSticker(
            id = "test-id-123",
            type = StickerType.EMOJI,
            content = "⭐",
            xRatio = 0.4f,
            yRatio = 0.6f,
            scale = 1.2f,
            rotation = 15.0f
        )

        val json = StickerSerializer.serialize(listOf(sticker))
        val deserialized = StickerSerializer.deserialize(json)

        assertEquals(1, deserialized.size)
        val result = deserialized[0]
        assertEquals("test-id-123", result.id)
        assertEquals(StickerType.EMOJI, result.type)
        assertEquals("⭐", result.content)
        assertEquals(0.4f, result.xRatio, 0.001f)
        assertEquals(0.6f, result.yRatio, 0.001f)
        assertEquals(1.2f, result.scale, 0.001f)
        assertEquals(15.0f, result.rotation, 0.001f)
    }

    @Test
    fun `multiple stickers roundtrip serialization`() {
        val stickers = listOf(
            NoteSticker(
                id = "s1",
                type = StickerType.EMOJI,
                content = "🎉",
                xRatio = 0.2f,
                yRatio = 0.3f,
                scale = 1.0f,
                rotation = 0.0f
            ),
            NoteSticker(
                id = "s2",
                type = StickerType.IMAGE_PATH,
                content = "/path/to/sticker.png",
                xRatio = 0.7f,
                yRatio = 0.8f,
                scale = 1.5f,
                rotation = -45.0f
            )
        )

        val json = StickerSerializer.serialize(stickers)
        val deserialized = StickerSerializer.deserialize(json)

        assertEquals(2, deserialized.size)
        assertEquals("s1", deserialized[0].id)
        assertEquals("🎉", deserialized[0].content)
        assertEquals("s2", deserialized[1].id)
        assertEquals(StickerType.IMAGE_PATH, deserialized[1].type)
        assertEquals("/path/to/sticker.png", deserialized[1].content)
    }

    @Test
    fun `invalid json returns empty list gracefully`() {
        val result = StickerSerializer.deserialize("invalid json {[[")
        assertTrue(result.isEmpty())
    }
}
