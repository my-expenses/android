package com.motawfik.expenses.validation

import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.databinding.BindingAdapter

@BindingAdapter("userName")
fun EditText.bindName(name: String) {
    error = validateName(name)
}

@BindingAdapter("email")
fun EditText.bindEmail(email: String) {
    error = validateEmail(email)
}

@BindingAdapter("password")
fun EditText.bindPassword(password: String) {
    error = validatePassword(password)
}

@BindingAdapter(value = ["actualPassword", "confirmPassword"])
fun EditText.bindConfirmPassword(password: String, confirmPassword: String) {
    error = validateConfirmationPassword(password, confirmPassword)
}

@BindingAdapter(value = ["userFirstName", "userLastName", "userEmail", "userPassword", "userConfirmPassword"])
fun Button.bindValidation(
    userFirstName: String,
    userLastName: String,
    userEmail: String,
    userPassword: String,
    userConfirmPassword: String
) {
    isEnabled =
        validateName(userFirstName) == null &&
                validateName(userLastName) == null &&
                validateEmail(userEmail) == null &&
                validatePassword(userPassword) == null &&
                validateConfirmationPassword(userPassword, userConfirmPassword) == null
}


fun validateName(name: String): String? {
    return if (name.isEmpty()) {
        "You must enter your name"
    } else if (!name.all { it.isLetter() }) {
        "Name must contain letters only"
    } else {
        null
    }
}


fun validateEmail(email: String): String? {
    return if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        null
    } else {
        "You must enter a valid email"
    }
}

fun validatePassword(password: String): String? {
    return if (password.length < 8) {
        "Password must be 8+ characters"
    } else if (!password.any { it.isUpperCase() }) {
        "Password must contain an uppercase letter"
    } else if (!password.any { it.isLowerCase() }) {
        "Password must contain a lowercase letter"
    } else if (!password.any { it.isDigit() }) {
        "Password must contain a digit"
    } else if (password.all { it.isLetterOrDigit() }) {
        "Password must contain a special character"
    } else {
        null
    }
}

fun validateConfirmationPassword(password: String, confirmPassword: String): String? {
    return if (password != confirmPassword) {
        "Passwords don't match"
    } else {
        null
    }
}



