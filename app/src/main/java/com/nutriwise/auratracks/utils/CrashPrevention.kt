package com.nutriwise.auratracks.utils

import android.content.Context
import android.util.Log
import android.widget.Toast


object CrashPrevention {
    
    private const val TAG = "CrashPrevention"
    

    fun safeExecute(
        context: Context?,
        operation: () -> Unit,
        errorMessage: String = "An error occurred"
    ) {
        try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, errorMessage, e)
            context?.let {
                try {
                    Toast.makeText(it, errorMessage, Toast.LENGTH_SHORT).show()
                } catch (toastException: Exception) {
                    Log.e(TAG, "Failed to show error toast", toastException)
                }
            }
        }
    }
    

    fun <T> safeExecuteWithDefault(
        default: T,
        operation: () -> T,
        errorMessage: String = "Operation failed"
    ): T {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, errorMessage, e)
            default
        }
    }
    

    fun <T> safeNullCheck(value: T?, operation: (T) -> Unit, errorMessage: String = "Null value encountered") {
        if (value != null) {
            try {
                operation(value)
            } catch (e: Exception) {
                Log.e(TAG, errorMessage, e)
            }
        } else {
            Log.w(TAG, "$errorMessage: Value was null")
        }
    }
    

    fun logCritical(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
    

    fun isFragmentValid(fragment: androidx.fragment.app.Fragment?): Boolean {
        return fragment != null && 
               fragment.isAdded && 
               fragment.context != null &&
               !fragment.isDetached
    }
    

    fun <T> safeListOperation(operation: () -> List<T>): List<T> {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, "List operation failed", e)
            emptyList()
        }
    }
    

    fun safeStringOperation(operation: () -> String): String {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, "String operation failed", e)
            ""
        }
    }
    

    fun safeIntOperation(operation: () -> Int): Int {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, "Integer operation failed", e)
            0
        }
    }
    

    fun safeBooleanOperation(operation: () -> Boolean): Boolean {
        return try {
            operation()
        } catch (e: Exception) {
            Log.e(TAG, "Boolean operation failed", e)
            false
        }
    }
}

