package com.example.loginform

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
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
import com.example.loginform.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust padding when soft keyboard (IME) or system bars open/close
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        // Underline Forgot Password link
        binding.tvForgotPassword.paintFlags = binding.tvForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        // Setup Sign Up Span Link
        val signUpText = getString(R.string.signup_prompt)
        val spannableString = SpannableString(signUpText)
        val startIndex = signUpText.indexOf("Sign up")
        if (startIndex != -1) {
            val endIndex = startIndex + "Sign up".length
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Toast.makeText(this@MainActivity, "Sign up clicked", Toast.LENGTH_SHORT).show()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = getColor(R.color.sky_blue)
                }
            }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.tvSignUp.text = spannableString
            binding.tvSignUp.movementMethod = LinkMovementMethod.getInstance()
            binding.tvSignUp.highlightColor = Color.TRANSPARENT
        }

        // Function to update Login button enabled state and color based on input presence
        fun updateLoginButtonState() {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val isFormValid = username.isNotEmpty() && password.isNotEmpty()

            binding.btnLogin.isEnabled = isFormValid
            binding.btnLogin.backgroundTintList = ColorStateList.valueOf(
                if (isFormValid) getColor(R.color.brand_teal) else Color.GRAY
            )
        }

        // Clear error messages and update button state when user starts typing
        binding.usernameEditText.doOnTextChanged { _, _, _, _ ->
            binding.usernameLayout.error = null
            updateLoginButtonState()
        }
        binding.passwordEditText.doOnTextChanged { _, _, _, _ ->
            binding.passwordLayout.error = null
            updateLoginButtonState()
        }

        // Initialize button state on launch
        updateLoginButtonState()

        // Helper function to hide the soft keyboard
        fun hideKeyboard() {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        // Function to perform login logic with error handling
        fun performLogin() {
            hideKeyboard()

            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Reset errors
            binding.usernameLayout.error = null
            binding.passwordLayout.error = null

            var isValid = true

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

            if (!isValid) return

            if (username == "admin" && password == "123456") {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to Home/Dashboard Activity
            } else {
                binding.usernameLayout.error = getString(R.string.error_invalid_credentials)
                binding.passwordLayout.error = getString(R.string.error_invalid_credentials)
                Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
            }
        }

        // Login button click listener
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // Trigger login when user presses Enter/Done on the password keyboard
        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performLogin()
                true
            } else {
                false
            }
        }
    }
}