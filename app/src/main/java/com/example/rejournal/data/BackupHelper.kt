package com.example.rejournal.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupHelper {

    fun exportFullBackup(context: Context, entries: List<MoodEntry>): Intent {
        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val zipFile = File(exportsDir, "rejournal_backup.zip")

        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            val entriesJson = JSONArray()
            entries.forEach { entry ->
                val obj = JSONObject()
                obj.put("date", entry.date.toString())
                obj.put("mood", entry.mood)
                obj.put("note", entry.note)
                obj.put("energy", entry.energy)
                obj.put("productivity", entry.productivity)
                obj.put("stress", entry.stress)
                obj.put("sleep", entry.sleep)
                obj.put("activities", JSONArray(entry.activities))

                val photoNames = JSONArray()
                entry.photoPaths.forEach { path ->
                    val file = File(path)
                    if (file.exists()) {
                        val zipName = "media/${file.name}"
                        zipOut.putNextEntry(ZipEntry(zipName))
                        file.inputStream().use { it.copyTo(zipOut) }
                        zipOut.closeEntry()
                        photoNames.put(zipName)
                    }
                }
                obj.put("photoPaths", photoNames)

                val audioNames = JSONArray()
                entry.audioPaths.forEach { path ->
                    val file = File(path)
                    if (file.exists()) {
                        val zipName = "media/${file.name}"
                        zipOut.putNextEntry(ZipEntry(zipName))
                        file.inputStream().use { it.copyTo(zipOut) }
                        zipOut.closeEntry()
                        audioNames.put(zipName)
                    }
                }
                obj.put("audioPaths", audioNames)

                entriesJson.put(obj)
            }

            zipOut.putNextEntry(ZipEntry("entries.json"))
            zipOut.write(entriesJson.toString().toByteArray())
            zipOut.closeEntry()
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", zipFile)
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun importFromZip(context: Context, uri: Uri): List<MoodEntry> {
        val mediaNameMap = mutableMapOf<String, String>()
        var entriesJsonText: String? = null

        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    if (entry.name == "entries.json") {
                        entriesJsonText = zipIn.readBytes().toString(Charsets.UTF_8)
                    } else if (entry.name.startsWith("media/")) {
                        val originalName = entry.name.removePrefix("media/")
                        val isAudio = originalName.endsWith(".m4a")
                        val destDir = File(context.filesDir, if (isAudio) "audio" else "photos").apply { mkdirs() }
                        val newFile = File(destDir, "${UUID.randomUUID()}_$originalName")
                        FileOutputStream(newFile).use { out -> zipIn.copyTo(out) }
                        mediaNameMap[entry.name] = newFile.absolutePath
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
        }

        val jsonText = entriesJsonText ?: return emptyList()
        val array = JSONArray(jsonText)
        val result = mutableListOf<MoodEntry>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val activities = obj.getJSONArray("activities").let { arr ->
                (0 until arr.length()).map { arr.getString(it) }
            }
            val photoPaths = obj.optJSONArray("photoPaths")?.let { arr ->
                (0 until arr.length()).mapNotNull { mediaNameMap[arr.getString(it)] }
            } ?: emptyList()
            val audioPaths = obj.optJSONArray("audioPaths")?.let { arr ->
                (0 until arr.length()).mapNotNull { mediaNameMap[arr.getString(it)] }
            } ?: emptyList()

            result.add(
                MoodEntry(
                    date = LocalDate.parse(obj.getString("date")),
                    mood = obj.getInt("mood"),
                    activities = activities,
                    note = obj.getString("note"),
                    energy = obj.optInt("energy", 3),
                    productivity = obj.optInt("productivity", 3),
                    stress = obj.optInt("stress", 3),
                    sleep = obj.optInt("sleep", 3),
                    photoPaths = photoPaths,
                    audioPaths = audioPaths
                )
            )
        }
        return result
    }
}