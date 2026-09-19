package com.example.rejournal.data

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AvailableUpdate(val version: String, val releaseUrl: String)

object VersionCheckHelper {
    private const val API_URL = "https://api.github.com/repos/GalaxyJammed/ReJournal/releases/latest"


    fun checkForUpdate(currentVersion: String): AvailableUpdate? {
        return try {
            val connection = URL(API_URL).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            if (connection.responseCode != 200) return null

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            val tagName = json.optString("tag_name", "").removePrefix("v")
            val htmlUrl = json.optString("html_url", "")

            if (tagName.isBlank() || htmlUrl.isBlank()) return null
            if (!isNewer(tagName, currentVersion)) return null

            AvailableUpdate(version = tagName, releaseUrl = htmlUrl)
        } catch (e: Exception) {
            null
        }
    }


    private fun isNewer(remote: String, current: String): Boolean {
        val remoteParts = remote.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
        val length = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until length) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r != c) return r > c
        }
        return false
    }
}