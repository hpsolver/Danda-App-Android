package com.sammyekaran.danda.utils

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Created by vijay on 19/7/18.
 */

class Validations
{

        var characterOnly = "^[\\p{L} .'-]+$"

        internal var panPattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")

        fun isEmpty(field: String): Boolean {
            return field.isEmpty()
        }

        fun isValid(field: String): Boolean {
            return field.matches(characterOnly.toRegex())
        }

        fun isValidPan(value: String): Boolean {
            val matcher = panPattern.matcher(value)
            return matcher.matches()
        }

        fun isValidMobile(field: String): Boolean {
            return Patterns.PHONE.matcher(field).matches()
        }

        fun isValidEmail(field: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(field).matches()
        }

        fun isValidPassword(field: String): Boolean {
            return field.length < 6
        }

        fun isEqual(field1: String, field2: String): Boolean {
            return field1.equals(field2, ignoreCase = true)
        }


}