    package com.example.dicodingstory.utils

    import android.content.Context
    import androidx.datastore.core.DataStore
    import androidx.datastore.preferences.core.Preferences
    import androidx.datastore.preferences.core.booleanPreferencesKey
    import androidx.datastore.preferences.core.edit
    import androidx.datastore.preferences.core.stringPreferencesKey
    import androidx.datastore.preferences.preferencesDataStore
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.map

    // Context extension property for DataStore
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    class UserPreferences(private val dataStore: DataStore<Preferences>) {
        companion object {
            private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
            private val ACCESS_TOKEN = stringPreferencesKey("access_token")
            private val USER_ID = stringPreferencesKey("user_id")
            private val USER_EMAIL = stringPreferencesKey("user_email")
            private val USER_NAME = stringPreferencesKey("user_name")

            // Singleton instance method
            fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
                return UserPreferences(dataStore)
            }
        }

        // Data class to represent user data
        data class UserData(
            val isLoggedIn: Boolean = false,
            val token: String = "",
            val userId: String = "",
            val userEmail: String = "",
            val userName: String = ""
        )

        // Get user data as a Flow
        fun getUser(): Flow<UserData> {
            return dataStore.data.map { preferences ->
                UserData(
                    isLoggedIn = preferences[IS_LOGGED_IN] ?: false,
                    token = preferences[ACCESS_TOKEN] ?: "",
                    userId = preferences[USER_ID] ?: "",
                    userEmail = preferences[USER_EMAIL] ?: "",
                    userName = preferences[USER_NAME] ?: ""
                )
            }
        }

        // Save individual user data
        suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
            dataStore.edit { preferences ->
                preferences[IS_LOGGED_IN] = isLoggedIn
            }
        }

        suspend fun saveAccessToken(token: String) {
            dataStore.edit { preferences ->
                preferences[ACCESS_TOKEN] = token
            }
        }

        suspend fun saveUserId(userId: String) {
            dataStore.edit { preferences ->
                preferences[USER_ID] = userId
            }
        }

        suspend fun saveUserEmail(email: String) {
            dataStore.edit { preferences ->
                preferences[USER_EMAIL] = email
            }
        }

        suspend fun saveUserName(name: String) {
            dataStore.edit { preferences ->
                preferences[USER_NAME] = name
            }
        }


        // Logout method
        suspend fun logout() {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }