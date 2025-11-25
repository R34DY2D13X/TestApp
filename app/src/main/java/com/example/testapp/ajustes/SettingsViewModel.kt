package com.example.testapp.ajustes

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.MyApplication
import com.example.testapp.data.db.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PrefKeys {
    val FONT_SIZE = floatPreferencesKey("font_size")
    val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    val ACCESSIBILITY_MODE = booleanPreferencesKey("accessibility_mode")
    val NOTIF_HABITOS = booleanPreferencesKey("notif_habitos")
    val NOTIF_SUENO = booleanPreferencesKey("notif_sueno")
    val NOTIF_ESTUDIOS = booleanPreferencesKey("notif_estudios")
    val REMINDER_TIME = stringPreferencesKey("reminder_time")
    val USER_NAME = stringPreferencesKey("user_name") // Deprecated, but keep for migration
    val USER_LOGGED_IN = booleanPreferencesKey("user_logged_in")
    val LOGGED_IN_USER_EMAIL = stringPreferencesKey("logged_in_user_email") // <-- NUEVA CLAVE
}

data class SettingsState(
    val fontSize: Float = 1f,
    val highContrast: Boolean = false,
    val accessibilityMode: Boolean = false,
    val notifHabitos: Boolean = true,
    val notifSueno: Boolean = true,
    val notifEstudios: Boolean = false,
    val reminderTime: String = "08:00 AM",
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null // <-- NUEVO ESTADO
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = getApplication<Application>().applicationContext.dataStore
    private val userDao = (application as MyApplication).database.userDao()

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState = _uiState.asStateFlow()
    
    // Flow para el usuario actual
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            val initialState = loadSettings()
            _uiState.value = initialState
            // Cargar el usuario si hay un email guardado
            initialState.userEmail?.let { email ->
                _currentUser.value = userDao.getUserByEmail(email)
            }
        }
    }

    private suspend fun loadSettings(): SettingsState {
        return dataStore.data.first().let { prefs ->
            SettingsState(
                fontSize = prefs[PrefKeys.FONT_SIZE] ?: 1f,
                highContrast = prefs[PrefKeys.HIGH_CONTRAST] ?: false,
                accessibilityMode = prefs[PrefKeys.ACCESSIBILITY_MODE] ?: false,
                notifHabitos = prefs[PrefKeys.NOTIF_HABITOS] ?: true,
                notifSueno = prefs[PrefKeys.NOTIF_SUENO] ?: true,
                notifEstudios = prefs[PrefKeys.NOTIF_ESTUDIOS] ?: false,
                reminderTime = prefs[PrefKeys.REMINDER_TIME] ?: "08:00 AM",
                isLoggedIn = prefs[PrefKeys.USER_LOGGED_IN] ?: false,
                userEmail = prefs[PrefKeys.LOGGED_IN_USER_EMAIL]
            )
        }
    }

    fun updateUserEmail(email: String?) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                if (email == null) {
                    prefs.remove(PrefKeys.LOGGED_IN_USER_EMAIL)
                } else {
                    prefs[PrefKeys.LOGGED_IN_USER_EMAIL] = email
                }
            }
            _uiState.value = _uiState.value.copy(userEmail = email)
            _currentUser.value = email?.let { userDao.getUserByEmail(it) }
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            val currentUserEmail = _uiState.value.userEmail
            if (currentUserEmail != null) {
                val user = userDao.getUserByEmail(currentUserEmail)
                if (user != null) {
                    userDao.insertUser(user.copy(nombre = newName))
                    _currentUser.value = userDao.getUserByEmail(currentUserEmail) // Recargar
                }
            }
        }
    }

    fun updateLoginState(loggedIn: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[PrefKeys.USER_LOGGED_IN] = loggedIn }
            _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn)
            if (!loggedIn) {
                updateUserEmail(null)
            }
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            dataStore.edit { it[PrefKeys.FONT_SIZE] = size }
            _uiState.value = _uiState.value.copy(fontSize = size)
        }
    }

    fun updateHighContrast(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[PrefKeys.HIGH_CONTRAST] = enabled }
            _uiState.value = _uiState.value.copy(highContrast = enabled)
        }
    }

    fun updateAccessibilityMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[PrefKeys.ACCESSIBILITY_MODE] = enabled }
            _uiState.value = _uiState.value.copy(accessibilityMode = enabled)
        }
    }

    fun updateNotifHabitos(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[PrefKeys.NOTIF_HABITOS] = enabled }
            _uiState.value = _uiState.value.copy(notifHabitos = enabled)
        }
    }

    fun updateNotifSueno(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[PrefKeys.NOTIF_SUENO] = enabled }
            _uiState.value = _uiState.value.copy(notifSueno = enabled)
        }
    }

    fun updateNotifEstudios(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[PrefKeys.NOTIF_ESTUDIOS] = enabled }
            _uiState.value = _uiState.value.copy(notifEstudios = enabled)
        }
    }
}