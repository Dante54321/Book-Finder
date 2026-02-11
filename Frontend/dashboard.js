const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html'; // kick out user if not logged in
}

document.addEventListener('DOMContentLoaded', () => {
    const username = localStorage.getItem('username');
    const welcomeHeader = document.querySelector('h1');

    if (username) {
        welcomeHeader.textContent = `Welcome back, ${username}!`;
    } else {
       // back to login page if they aren't properly logged in
        window.location.href = 'login.html';
    }
});