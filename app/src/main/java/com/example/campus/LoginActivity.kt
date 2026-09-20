package com.example.campus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption

class LoginActivity : AppCompatActivity() {

    // Paste the OAuth *Web* client ID from Google Cloud Console here
    private val webClientId = "373850834033-ektif92bf88isp3dhqjdfo39501upo63.apps.googleusercontent.com"

    private lateinit var session: SessionManager
    private lateinit var progress: ProgressBar
    private lateinit var btnGoogle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        }
        session = SessionManager(this)

        if (session.isLoggedIn()) {
            goToMain()
            return
        }

        setContentView(R.layout.activity_login)
        progress = findViewById(R.id.progress)
        btnGoogle = findViewById(R.id.btnGoogle)

        btnGoogle.setOnClickListener { signInWithGoogle() }

        // TEMPORARY: remove before submission
        findViewById<Button>(R.id.btnSkip).setOnClickListener {
            session.saveLogin("Test Student", "test@student.com", "")
            goToMain()
        }
    }

    private fun signInWithGoogle() {
        if (webClientId.startsWith("PASTE")) {
            Toast.makeText(this, "Set the web client ID first", Toast.LENGTH_LONG).show()
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(webClientId).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        setLoading(true)
        lifecycleScope.launch {
            try {
                val result = CredentialManager.create(this@LoginActivity)
                    .getCredential(this@LoginActivity, request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val google = GoogleIdTokenCredential.createFrom(credential.data)
                    session.saveLogin(
                        google.displayName ?: "Student",
                        google.id,
                        google.idToken
                    )
                    // TODO: send google.idToken to Person 1's /api/auth and store the returned JWT
                    goToMain()
                } else {
                    showError("Unexpected credential type")
                }
            } catch (e: GetCredentialException) {
                Log.e("Login", "Sign-in failed", e)
                showError("Sign-in failed: ${e.message}")
            } catch (e: GoogleIdTokenParsingException) {
                showError("Invalid Google token")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        btnGoogle.isEnabled = !loading
    }

    private fun showError(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}