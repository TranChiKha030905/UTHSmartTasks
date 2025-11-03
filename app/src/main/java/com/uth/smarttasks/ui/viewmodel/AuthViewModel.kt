package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// State chung cho việc đăng nhập/đăng ký
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

// State riêng cho Profile
data class ProfileUiState(
    val isLoading: Boolean = false,
    val message: String? = null // Dùng để báo (Toast)
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    // Khởi tạo Analytics
    private val analytics: FirebaseAnalytics = Firebase.analytics

    // State cho các nút bấm (Login/Register)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    // State cho Profile (Reset pass)
    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState = _profileUiState.asStateFlow()

    // State cho Splash Screen (Fix lỗi treo logo)
    // Bắt đầu là true (đang tải)
    private val _isLoadingFromSplash = MutableStateFlow(true)
    val isLoadingFromSplash = _isLoadingFromSplash.asStateFlow()

    // State cho user (sẽ tự động cập nhật)
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser = _currentUser.asStateFlow()

    // Khối init: Tự chạy khi ViewModel được tạo
    init {
        // Thêm một listener để TỰ ĐỘNG theo dõi trạng thái đăng nhập
        auth.addAuthStateListener { firebaseAuth ->
            // Cập nhật user hiện tại
            _currentUser.value = firebaseAuth.currentUser

            // TẮT Splash Screen (chỉ 1 lần đầu)
            // Khi listener này chạy, ta biết Firebase đã check xong
            if (_isLoadingFromSplash.value) {
                _isLoadingFromSplash.value = false
            }
        }
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.createUserWithEmailAndPassword(email, pass).await()
                _uiState.value = AuthUiState(success = true)

                // --- BÁO CÁO CHO ANALYTICS ---
                analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                    param(FirebaseAnalytics.Param.METHOD, "email")
                }
                // Báo cho Analytics biết User ID này là ai
                analytics.setUserId(auth.currentUser?.uid)

            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message)
            }
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                _uiState.value = AuthUiState(success = true)

                // --- BÁO CÁO CHO ANALYTICS ---
                analytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                    param(FirebaseAnalytics.Param.METHOD, "email")
                }
                // Báo cho Analytics biết User ID này là ai
                analytics.setUserId(auth.currentUser?.uid)

            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message)
            }
        }
    }

    fun signOut() {
        // Xóa User ID khi log out
        analytics.setUserId(null)
        auth.signOut()
        // Listener `addAuthStateListener` ở trên sẽ tự động cập nhật `_currentUser`
    }

    // Hàm để RESET PASSWORD
    fun sendPasswordReset() {
        val email = auth.currentUser?.email
        if (email == null) {
            _profileUiState.value = ProfileUiState(message = "Error: Not logged in")
            return
        }

        _profileUiState.value = ProfileUiState(isLoading = true)
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _profileUiState.value = ProfileUiState(message = "Password reset email sent!")
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState(message = "Error: ${e.message}")
            }
        }
    }

    // Reset state lỗi sau khi đã hiển thị
    fun resetError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Reset message của Profile sau khi đã hiển thị
    fun resetProfileMessage() {
        _profileUiState.value = ProfileUiState()
    }
}