package com.example.campus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val session = SessionManager(this)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        findViewById<TextView>(R.id.txtName).text = session.name()
        findViewById<TextView>(R.id.txtEmail).text = session.email()

        // Language
        val langs = mapOf(
            R.id.langEn to "en",
            R.id.langZu to "zu",
            R.id.langXh to "xh",
            R.id.langAf to "af",
            R.id.langSt to "st"
        )
        val group = findViewById<RadioGroup>(R.id.langGroup)
        val current = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val selected = langs.entries.firstOrNull { current.startsWith(it.value) }?.key ?: R.id.langEn
        group.check(selected)
        group.setOnCheckedChangeListener { _, checkedId ->
            val tag = langs[checkedId] ?: "en"
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }

        // Notification toggles (saved locally; Person 5 reads these keys)
        val toggles = mapOf(
            R.id.swAssignments to "notif_assignments",
            R.id.swTimetable to "notif_timetable",
            R.id.swAnnouncements to "notif_announcements",
            R.id.swEvents to "notif_events",
            R.id.swDiscussions to "notif_discussions"
        )
        for ((viewId, key) in toggles) {
            val sw = findViewById<MaterialSwitch>(viewId)
            sw.isChecked = prefs.getBoolean(key, true)
            sw.setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean(key, checked).apply()
            }
        }

        // Sign out
        findViewById<Button>(R.id.btnSignOut).setOnClickListener {
            lifecycleScope.launch {
                try {
                    CredentialManager.create(this@SettingsActivity)
                        .clearCredentialState(ClearCredentialStateRequest())
                } catch (_: Exception) {
                }
                session.clear()
                startActivity(
                    Intent(this@SettingsActivity, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }
    }
}