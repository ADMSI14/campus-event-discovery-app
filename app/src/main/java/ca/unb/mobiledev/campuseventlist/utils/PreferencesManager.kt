package ca.unb.mobiledev.campuseventlist.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper class to manage app preferences, specifically for saving/loading the last selected school
 */
class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "CampusEventListPrefs"
        private const val KEY_SCHOOL_ID = "selected_school_id"
        private const val KEY_SCHOOL_NAME = "selected_school_name"
        
        @Volatile
        private var INSTANCE: PreferencesManager? = null
        
        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Save the selected school
     */
    fun saveSelectedSchool(schoolId: String, schoolName: String) {
        prefs.edit().apply {
            putString(KEY_SCHOOL_ID, schoolId)
            putString(KEY_SCHOOL_NAME, schoolName)
            apply()
        }
    }
    
    /**
     * Get the saved school ID
     */
    fun getSavedSchoolId(): String? {
        return prefs.getString(KEY_SCHOOL_ID, null)
    }
    
    /**
     * Get the saved school name
     */
    fun getSavedSchoolName(): String? {
        return prefs.getString(KEY_SCHOOL_NAME, null)
    }
    
    /**
     * Check if a school is saved
     */
    fun hasSavedSchool(): Boolean {
        return !getSavedSchoolId().isNullOrEmpty() && !getSavedSchoolName().isNullOrEmpty()
    }
    
    /**
     * Clear the saved school
     */
    fun clearSavedSchool() {
        prefs.edit().apply {
            remove(KEY_SCHOOL_ID)
            remove(KEY_SCHOOL_NAME)
            apply()
        }
    }
}

