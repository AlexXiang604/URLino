/*********************************************************
 *  auth.js - Front-end only user auth with localStorage *
 *  IMPORTANT: This is just for demonstration!           *
 *  Real apps require secure server-side authentication. *
 ********************************************************/

/* 
  We'll store users in localStorage under the key 'users' as an array of objects:
  [
    {
      email: "someone@example.com",
      password: "plaintextOrHashedPassword"
    },
    ...
  ]
*/

/*******************************************************
 *  Helper function to get users array from localStorage
 *******************************************************/
function getUsersFromLocalStorage() {
    const usersJSON = localStorage.getItem('users');
    if (usersJSON) {
      return JSON.parse(usersJSON);
    }
    return [];
  }
  
  /*******************************************************
   *  Helper function to save users array to localStorage
   *******************************************************/
  function saveUsersToLocalStorage(users) {
    localStorage.setItem('users', JSON.stringify(users));
  }
  
  /***********************************
   *  Sign Up form event listener    *
   ***********************************/
  const signupForm = document.getElementById('signupForm');
  if (signupForm) {
    signupForm.addEventListener('submit', (event) => {
      event.preventDefault();
      const email = document.getElementById('signupEmail').value.trim();
      const password = document.getElementById('signupPassword').value;
  
      const signupMessage = document.getElementById('signupMessage');
      signupMessage.textContent = ''; // Clear old messages
  
      // Basic validation
      if (!email || !password) {
        signupMessage.textContent = 'Please fill out all fields.';
        return;
      }
  
      // Retrieve existing users
      const users = getUsersFromLocalStorage();
  
      // Check if user already exists
      const existingUser = users.find(user => user.email.toLowerCase() === email.toLowerCase());
      if (existingUser) {
        signupMessage.textContent = 'User already exists. Please login or use another email.';
        return;
      }
  
      // Create new user object
      const newUser = {
        email,
        // In production, NEVER store plaintext passwords. Use a hash!
        password
      };
  
      // Add to array and save
      users.push(newUser);
      saveUsersToLocalStorage(users);
  
      signupMessage.textContent = 'Signup successful! You can now log in.';
      signupForm.reset();
    });
  }
  
  /***********************************
   *  Login form event listener      *
   ***********************************/
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', (event) => {
      event.preventDefault();
      const email = document.getElementById('loginEmail').value.trim();
      const password = document.getElementById('loginPassword').value;
  
      const loginMessage = document.getElementById('loginMessage');
      loginMessage.textContent = ''; // Clear old messages
  
      // Basic validation
      if (!email || !password) {
        loginMessage.textContent = 'Please fill out all fields.';
        return;
      }
  
      // Retrieve existing users
      const users = getUsersFromLocalStorage();
  
      // Check if user with this email + password exists
      const foundUser = users.find(user =>
        user.email.toLowerCase() === email.toLowerCase() && user.password === password
      );
  
      if (foundUser) {
        loginMessage.textContent = 'Login successful!';
        // Typically, you'd redirect to a dashboard or main app page:
        // window.location.href = "index.html";
        loginForm.reset();
      } else {
        loginMessage.textContent = 'Invalid email or password. Please try again.';
      }
    });
  }
  