package com.nutriwise.auratracks.models

/**
 * Data class representing user profile information
 * @param name User's full name
 * @param age User's age
 * @param email User's email address
 * @param birthday User's birthday in dd/MM/yyyy format (stored as string)
 * @param gender User's gender
 * @param profileImageUri URI string of the profile image
 */
data class UserProfile(
    val name: String = "",
    val age: Int = 0,
    val email: String = "",
    val birthday: String = "",
    val gender: String = "",
    val profileImageUri: String = ""
)
