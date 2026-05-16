// ===== PRODIGY AUTH - Shared Utilities =====

const API_BASE = '';

// Show alert message
function showAlert(message, type = 'error') {
    const alertBox = document.getElementById('alertBox');
    if (!alertBox) return;

    const icon = alertBox.querySelector('.alert-icon');
    const msg = alertBox.querySelector('.alert-message');

    alertBox.className = 'alert show alert-' + type;
    icon.textContent = type === 'success' ? '✅' : '❌';
    msg.textContent = message;

    // Auto-hide after 5s
    setTimeout(() => {
        alertBox.classList.remove('show');
    }, 5000);
}

// Toggle password visibility
function togglePassword(fieldId, btn) {
    const field = document.getElementById(fieldId);
    if (field.type === 'password') {
        field.type = 'text';
        btn.textContent = '🙈';
    } else {
        field.type = 'password';
        btn.textContent = '👁️';
    }
}

// Logout
function logout() {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    window.location.href = 'login.html';
}

// Get auth header
function getAuthHeaders() {
    const token = sessionStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? 'Bearer ' + token : ''
    };
}

// Check if user is authenticated
function isAuthenticated() {
    return !!sessionStorage.getItem('token');
}

// Redirect to login if not authenticated
function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}
