document.getElementById("registrationForm").addEventListener("submit", function(event) {
    event.preventDefault();

    // Get form data
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    // Prepare data to be sent to the server
    const userData = {
        username: username,
        email: email,
        password: password
    };

    // Send data to backend using Axios
    axios.post('https://your-backend-api-url.com/register', userData)
        .then(response => {
            // Handle successful response
            document.getElementById('message').textContent = "Registration successful!";
            document.getElementById('message').style.color = 'green';
        })
        .catch(error => {
            // Handle error
            document.getElementById('message').textContent = "Error during registration!";
            document.getElementById('message').style.color = 'red';
            console.error(error);
        });
});
