const loginForm = document.getElementById('loginForm');

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, 
                password, 
            })
        });

        if (response.ok) {
            alert("Log in successful! Redirecting to dashboard...");
            window.location.href = 'dashboard.html';
        } else {
            const error = await response.json();
            alert("Error: " + (error.message || "Login failed"));
        }
    } catch (err) {
        console.error("Login error:", err);
    }
});
 
