package com.example.rejournal.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

object ExportHelper {

    fun exportToCsv(context: Context, entries: List<MoodEntry>): Intent {
        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportsDir, "rejournal_export.csv")

        FileWriter(file).use { writer ->
            writer.append("Date,Mood,Activities,Note\n")
            entries.sortedBy { it.date }.forEach { entry ->
                writer.append(csvEscape(entry.date.toString())).append(",")
                writer.append(entry.mood.toString()).append(",")
                writer.append(csvEscape(entry.activities.joinToString("; "))).append(",")
                writer.append(csvEscape(entry.note)).append("\n")
            }
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    // Wraps a value in quotes and escapes internal quotes, so commas or
    // quote characters typed in a note don't break the CSV file's columns.
    private fun csvEscape(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }
}