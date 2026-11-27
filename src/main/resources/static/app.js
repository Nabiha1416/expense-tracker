const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const message = document.getElementById("message");

async function signup() {
    const response = await fetch("/api/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            username: usernameInput.value,
            password: passwordInput.value
        })
    });

    if (response.ok) {
        message.textContent = "Signed up! Now log in.";
    } else {
        message.textContent = "Signup failed. That username may be taken.";
    }
}

document.getElementById("signup-btn").addEventListener("click", signup);