package com.example.rejournal.data

import org.json.JSONArray
import java.util.UUID

enum class StickerType {
    EMOJI,
    DRAWABLE_RES,
    IMAGE_PATH
}

data class NoteSticker(
    val id: String = UUID.randomUUID().toString(),
    val type: StickerType,
    val content: String,
    val xRatio: Float = 0.5f,
    val yRatio: Float = 0.5f,
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f
)

object StickerSerializer {
    fun serialize(stickers: List<NoteSticker>): String {
        if (stickers.isEmpty()) return ""
        val sb = StringBuilder("[")
        stickers.forEachIndexed { index, s ->
            if (index > 0) sb.append(",")
            sb.append("{")
            sb.append("\"id\":\"").append(escape(s.id)).append("\",")
            sb.append("\"type\":\"").append(s.type.name).append("\",")
            sb.append("\"content\":\"").append(escape(s.content)).append("\",")
            sb.append("\"xRatio\":").append(s.xRatio).append(",")
            sb.append("\"yRatio\":").append(s.yRatio).append(",")
            sb.append("\"scale\":").append(s.scale).append(",")
            sb.append("\"rotation\":").append(s.rotation)
            sb.append("}")
        }
        sb.append("]")
        return sb.toString()
    }

    private fun escape(s: String): String {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }

    fun deserialize(json: String?): List<NoteSticker> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<NoteSticker>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", UUID.randomUUID().toString())
                val typeStr = obj.optString("type", StickerType.EMOJI.name)
                val type = try {
                    StickerType.valueOf(typeStr)
                } catch (e: Exception) {
                    StickerType.EMOJI
                }
                val content = obj.optString("content", "")
                val xRatio = obj.optDouble("xRatio", 0.5).toFloat()
                val yRatio = obj.optDouble("yRatio", 0.5).toFloat()
                val scale = obj.optDouble("scale", 1.0).toFloat()
                val rotation = obj.optDouble("rotation", 0.0).toFloat()

                list.add(
                    NoteSticker(
                        id = id,
                        type = type,
                        content = content,
                        xRatio = xRatio,
                        yRatio = yRatio,
                        scale = scale,
                        rotation = rotation
                    )
                )
            }
            if (list.isEmpty()) manualParse(json) else list
        } catch (e: Exception) {
            manualParse(json)
        }
    }

    private fun manualParse(json: String): List<NoteSticker> {
        return try {
            val trimmed = json.trim()
            if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()
            val inner = trimmed.substring(1, trimmed.length - 1).trim()
            if (inner.isEmpty()) return emptyList()

            val objectStrings = mutableListOf<String>()
            var depth = 0
            var start = -1
            for (i in inner.indices) {
                val c = inner[i]
                if (c == '{') {
                    if (depth == 0) start = i
                    depth++
                } else if (c == '}') {
                    depth--
                    if (depth == 0 && start != -1) {
                        objectStrings.add(inner.substring(start + 1, i))
                        start = -1
                    }
                }
            }

            val list = mutableListOf<NoteSticker>()
            for (objStr in objectStrings) {
                val map = mutableMapOf<String, String>()
                val pairs = objStr.split(",")
                for (pair in pairs) {
                    val kv = pair.split(":", limit = 2)
                    if (kv.size == 2) {
                        val key = kv[0].trim().trim('"')
                        val value = kv[1].trim().trim('"').replace("\\\"", "\"").replace("\\\\", "\\")
                        map[key] = value
                    }
                }
                val id = map["id"] ?: UUID.randomUUID().toString()
                val typeStr = map["type"] ?: StickerType.EMOJI.name
                val type = try { StickerType.valueOf(typeStr) } catch (e: Exception) { StickerType.EMOJI }
                val content = map["content"] ?: ""
                val xRatio = map["xRatio"]?.toFloatOrNull() ?: 0.5f
                val yRatio = map["yRatio"]?.toFloatOrNull() ?: 0.5f
                val scale = map["scale"]?.toFloatOrNull() ?: 1.0f
                val rotation = map["rotation"]?.toFloatOrNull() ?: 0.0f

                list.add(NoteSticker(id, type, content, xRatio, yRatio, scale, rotation))
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}
