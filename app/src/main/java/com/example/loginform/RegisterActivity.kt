package com.example.loginform

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.example.loginform.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate View Binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.registerMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup "Already have an account? Log in" Span Link
        val loginText = getString(R.string.login_prompt)
        val spannableString = SpannableString(loginText)
        val startIndex = loginText.indexOf("Log in")
        if (startIndex != -1) {
            val endIndex = startIndex + "Log in".length
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = getColor(R.color.sky_blue)
                }
            }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.tvLogin.text = spannableString
            binding.tvLogin.movementMethod = LinkMovementMethod.getInstance()
            binding.tvLogin.highlightColor = Color.TRANSPARENT
        }

        // Function to update Register button enabled state and background color
        fun updateRegisterButtonState() {
            val email = binding.emailEditText.text.toString().trim()
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            val isFormFilled = email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()

            binding.btnRegister.isEnabled = isFormFilled
            binding.btnRegister.backgroundTintList = ColorStateList.valueOf(
                if (isFormFilled) getColor(R.color.brand_teal) else Color.GRAY
            )
        }

        // Clear errors and update button state when user types
        binding.emailEditText.doOnTextChanged { _, _, _, _ ->
            binding.emailLayout.error = null
            updateRegisterButtonState()
        }
        binding.usernameEditText.doOnTextChanged { _, _, _, _ ->
            binding.usernameLayout.error = null
            updateRegisterButtonState()
        }
        binding.passwordEditText.doOnTextChanged { _, _, _, _ ->
            binding.passwordLayout.error = null
            updateRegisterButtonState()
        }
        binding.confirmPasswordEditText.doOnTextChanged { _, _, _, _ ->
            binding.confirmPasswordLayout.error = null
            updateRegisterButtonState()
        }

        // Initialize button state
        updateRegisterButtonState()

        // Helper function to hide soft keyboard
        fun hideKeyboard() {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        // Perform Registration logic
        fun performRegister() {
            hideKeyboard()

            val email = binding.emailEditText.text.toString().trim()
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            // Reset errors
            binding.emailLayout.error = null
            binding.usernameLayout.error = null
            binding.passwordLayout.error = null
            binding.confirmPasswordLayout.error = null

            var isValid = true

            if (email.isEmpty()) {
                binding.emailLayout.error = getString(R.string.error_email_required)
                isValid = false
            }

            if (username.isEmpty()) {
                binding.usernameLayout.error = getString(R.string.error_username_required)
                isValid = false
            }

            if (password.isEmpty()) {
                binding.passwordLayout.error = getString(R.string.error_password_required)
                isValid = false
            } else if (password.length < 6) {
                binding.passwordLayout.error = getString(R.string.error_password_length)
                isValid = false
            }

            if (confirmPassword.isEmpty()) {
                binding.confirmPasswordLayout.error = getString(R.string.error_password_required)
                isValid = false
            } else if (password != confirmPassword) {
                binding.confirmPasswordLayout.error = getString(R.string.error_confirm_password)
                isValid = false
            }

            if (!isValid) return

            Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
            // Navigate to Login (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnRegister.setOnClickListener {
            performRegister()
        }

        binding.confirmPasswordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performRegister()
                true
            } else {
                false
            }
        }
    }
}
