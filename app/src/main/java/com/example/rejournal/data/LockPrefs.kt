package com.example.rejournal.data

import android.content.Context
import java.security.MessageDigest

object LockPrefs {
    private const val PREFS_NAME = "lock_prefs"
    private const val KEY_FINGERPRINT_ENABLED = "fingerprint_enabled"
    private const val KEY_PIN_ENABLED = "pin_enabled"
    private const val KEY_PIN_HASH = "pin_hash"

    fun isFingerprintEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_FINGERPRINT_ENABLED, false)

    fun isPinEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_PIN_ENABLED, false)

    fun isEnabled(context: Context): Boolean = isFingerprintEnabled(context) || isPinEnabled(context)

    fun setFingerprintEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_FINGERPRINT_ENABLED, enabled)
            .apply()
    }

    fun setPinEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_PIN_ENABLED, enabled)
            .apply()
    }

    fun hasPin(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).contains(KEY_PIN_HASH)

    fun setPin(context: Context, pin: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_PIN_HASH, hash(pin))
            .apply()
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val storedHash = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PIN_HASH, null) ?: return false
        return storedHash == hash(pin)
    }

    fun clearPin(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .remove(KEY_PIN_HASH)
            .apply()
    }

    private fun hash(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}