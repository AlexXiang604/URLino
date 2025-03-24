/********************************************
 * THEME TOGGLE WITH CHECKBOX
 ********************************************/
const themeToggleCheckbox = document.getElementById("themeToggleCheckbox");
// If the checkbox is present, default is Dark Mode (checked).
// If unchecked => Light Mode
if (themeToggleCheckbox) {
  themeToggleCheckbox.addEventListener("change", () => {
    document.body.classList.toggle("light-mode", !themeToggleCheckbox.checked);
  });
}

/****************************************************
 *  In-memory + localStorage "database" for URLs
 ****************************************************/
/**
 * The urlDatabase object has this structure:
 * {
 *   shortCode: {
 *     longUrl: "...",
 *     clicks: number,
 *     createdAt: "2025-01-01T12:34:56.789Z",
 *   },
 *   ...
 * }
 */
const urlDatabase = {};
let counter = 1000;

/********************************************
 *  Also store support messages if you want
 ********************************************/
let supportMessages = [];

/********************************************
 *  localStorage Keys
 ********************************************/
const URL_DB_KEY = "urlDatabase";
const SUPPORT_MSG_KEY = "supportMessages";

/********************************************
 *  LocalStorage Persistence Functions
 ********************************************/
function saveUrlDatabase() {
  try {
    localStorage.setItem(URL_DB_KEY, JSON.stringify(urlDatabase));
  } catch (err) {
    console.error("Error saving urlDatabase to localStorage:", err);
  }
}

function loadUrlDatabase() {
  try {
    const stored = localStorage.getItem(URL_DB_KEY);
    if (stored) {
      const parsed = JSON.parse(stored);
      // Merge parsed data into our in-memory urlDatabase
      Object.keys(parsed).forEach(shortCode => {
        urlDatabase[shortCode] = parsed[shortCode];
      });
    }
  } catch (err) {
    console.error("Error loading urlDatabase from localStorage:", err);
  }
}

function saveSupportMessages() {
  try {
    localStorage.setItem(SUPPORT_MSG_KEY, JSON.stringify(supportMessages));
  } catch (err) {
    console.error("Error saving supportMessages to localStorage:", err);
  }
}

function loadSupportMessages() {
  try {
    const stored = localStorage.getItem(SUPPORT_MSG_KEY);
    if (stored) {
      supportMessages = JSON.parse(stored);
    }
  } catch (err) {
    console.error("Error loading supportMessages from localStorage:", err);
  }
}

/********************************************
 *  On script load, retrieve data from localStorage
 ********************************************/
loadUrlDatabase();
loadSupportMessages();

/********************************************
 *  Base62 encoding function (for shortCode)
 ********************************************/
function encodeBase62(num) {
  const chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const base = 62;
  let encoded = '';
  if (num === 0) return '0';
  while (num > 0) {
    let remainder = num % base;
    encoded = chars[remainder] + encoded;
    num = Math.floor(num / base);
  }
  return encoded;
}

/********************************************
 *  Generate a short URL code
 ********************************************/
function generateShortCode(customAlias) {
  if (customAlias) {
    return customAlias.trim();
  } else {
    const code = encodeBase62(counter);
    counter++;
    return code;
  }
}

/********************************************
 *  Helper function: create a new URL record
 ********************************************/
function createUrlRecord(longUrl) {
  return {
    longUrl,
    clicks: 0,
    createdAt: new Date().toISOString(),
  };
}

/****************************************************
 *  DOM references for the Shorten & Expand page
 ****************************************************/
const longUrlInput = document.getElementById('longUrlInput');
const customAliasInput = document.getElementById('customAliasInput');
const generateBtn = document.getElementById('generateBtn');
const shortenResult = document.getElementById('shortenResult');
const qrCodeContainer = document.getElementById('qrcode');

const shortUrlInput = document.getElementById('shortUrlInput');
const retrieveBtn = document.getElementById('retrieveBtn');
const retrieveResult = document.getElementById('retrieveResult');

/********************************************
 *  Generate Short URL (future.html)
 ********************************************/
if (generateBtn) {
  generateBtn.addEventListener('click', () => {
    if (!longUrlInput || !shortenResult) return;

    const longUrl = longUrlInput.value.trim();
    const customAlias = customAliasInput.value.trim();
    shortenResult.textContent = '';

    if (!longUrl) {
      shortenResult.textContent = 'Please enter a valid long URL.';
      return;
    }

    // Generate short code
    const shortCode = generateShortCode(customAlias);

    // Check if custom alias is already taken
    if (customAlias && urlDatabase[shortCode]) {
      shortenResult.textContent = `Alias "${shortCode}" is already taken. Try another.`;
      return;
    }

    // Store in our in-memory database
    urlDatabase[shortCode] = createUrlRecord(longUrl);
    saveUrlDatabase(); // Persist changes

    // Construct short URL
    const shortUrl = `https://short.ly/${shortCode}`;

    // Display result + copy button
    shortenResult.innerHTML = `
      Short URL: <strong>${shortUrl}</strong>
      <button id="copyShortUrlBtn" class="copy-btn">
        <span class="copy-icon">📋</span>
        Copy
      </button>
    `;

    // Generate QR Code, if container is present
    if (qrCodeContainer) {
      qrCodeContainer.innerHTML = ""; // Clear any previous QR code
      new QRCode(qrCodeContainer, {
        text: shortUrl,
        width: 128,
        height: 128,
        colorDark: "#000000",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.H
      });
    }

    // Copy button event
    const copyBtn = document.getElementById('copyShortUrlBtn');
    if (copyBtn) {
      copyBtn.addEventListener('click', () => {
        navigator.clipboard.writeText(shortUrl)
          .then(() => {
            alert('Copied to clipboard!');
          })
          .catch(err => {
            console.error('Failed to copy text: ', err);
          });
      });
    }
  });
}

/********************************************
 *  Retrieve Original URL (future.html)
 ********************************************/
if (retrieveBtn) {
  retrieveBtn.addEventListener('click', () => {
    if (!shortUrlInput || !retrieveResult) return;

    const shortUrl = shortUrlInput.value.trim();
    retrieveResult.textContent = '';

    if (!shortUrl) {
      retrieveResult.textContent = 'Please enter a short URL.';
      return;
    }

    // Extract the short code from e.g. "https://short.ly/xyz"
    const shortCode = shortUrl.replace(/^https?:\/\/short\.ly\//i, '').trim();
    if (!shortCode) {
      retrieveResult.textContent = 'Invalid short URL format.';
      return;
    }

    const record = urlDatabase[shortCode];
    if (record) {
      record.clicks += 1;        // increment usage
      saveUrlDatabase();         // persist

      // Show original URL + copy
      retrieveResult.innerHTML = `
        <div class="result-label">Original URL:</div>
        <div class="retrieve-box">
          <input type="text" class="retrieve-input" value="${record.longUrl}" readonly />
          <button id="copyOriginalUrlBtn" class="copy-btn">
            <span class="copy-icon">📋</span>
            Copy
          </button>
        </div>
      `;

      const copyOriginalUrlBtn = document.getElementById('copyOriginalUrlBtn');
      if (copyOriginalUrlBtn) {
        copyOriginalUrlBtn.addEventListener('click', () => {
          navigator.clipboard.writeText(record.longUrl)
            .then(() => {
              alert('Copied original URL to clipboard!');
            })
            .catch(err => {
              console.error('Failed to copy text: ', err);
            });
        });
      }
    } else {
      retrieveResult.textContent = 'No record found for this short URL.';
    }
  });
}

/********************************************
 *  Support Form Logic (support.html)
 ********************************************/
const supportForm = document.getElementById("supportForm");
const supportEmailInput = document.getElementById("supportEmail");
const supportMessageTextarea = document.getElementById("supportMessage");

if (supportForm) {
  supportForm.addEventListener("submit", (event) => {
    event.preventDefault();
    if (!supportEmailInput || !supportMessageTextarea) return;

    const email = supportEmailInput.value.trim();
    const message = supportMessageTextarea.value.trim();

    if (!email || !message) {
      alert("Please fill out all fields.");
      return;
    }

    // Optionally store the message in localStorage
    const newSupportEntry = {
      email,
      message,
      submittedAt: new Date().toISOString(),
    };
    supportMessages.push(newSupportEntry);
    saveSupportMessages();

    alert("Thank you! Your support request has been submitted. (Demo Only)");
    supportForm.reset();
  });
}

/********************************************
 *  Profile Settings (settings.html)
 ********************************************/
const profileSettingsForm = document.getElementById("profileSettingsForm");
const displayNameInput = document.getElementById("displayNameInput");
const emailInput = document.getElementById("emailInput");

if (profileSettingsForm) {
  profileSettingsForm.addEventListener("submit", (event) => {
    event.preventDefault();
    if (!displayNameInput || !emailInput) return;

    const newDisplayName = displayNameInput.value.trim();
    const newEmail = emailInput.value.trim();

    if (!newDisplayName && !newEmail) {
      alert("Please fill at least one field to update your profile.");
      return;
    }

    // In a real app, you'd store these changes securely.
    // For this demo, just show a quick message:
    alert(`Profile updated!\nName: ${newDisplayName}\nEmail: ${newEmail}\n(Demo Only)`);
    profileSettingsForm.reset();
  });
}

/********************************************
 *  Clear All Data (settings.html)
 ********************************************/
const clearAllDataBtn = document.getElementById("clearAllDataBtn");
if (clearAllDataBtn) {
  clearAllDataBtn.addEventListener("click", () => {
    if (!confirm("Are you sure you want to clear all local data? This action cannot be undone.")) {
      return;
    }
    // Clears *all* localStorage keys
    localStorage.clear();

    // Also clear our in-memory data
    for (const key in urlDatabase) delete urlDatabase[key];
    supportMessages = [];

    alert("All local data has been cleared! Refresh the page or revisit future.html to see changes.");
  });
}
