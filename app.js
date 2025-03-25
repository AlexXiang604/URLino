/********************************************
 * THEME TOGGLE WITH CHECKBOX
 ********************************************/
const themeToggleCheckbox = document.getElementById("themeToggleCheckbox");
if (themeToggleCheckbox) {
  themeToggleCheckbox.addEventListener("change", () => {
    document.body.classList.toggle("light-mode", !themeToggleCheckbox.checked);
  });
}

/********************************************
 * LocalStorage Persistence for Support Messages
 ********************************************/
let supportMessages = [];
const SUPPORT_MSG_KEY = "supportMessages";
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
loadSupportMessages();

/********************************************
 * Short URL Generation and Retrieval
 ********************************************/
const BASE_URL = 'http://localhost:8080/service';

// DOM references for Shorten URL widget
const longUrlInput = document.getElementById('longUrlInput');
const customAliasInput = document.getElementById('customAliasInput');
const generateBtn = document.getElementById('generateBtn');
const shortenResult = document.getElementById('shortenResult');
const qrCodeContainer = document.getElementById('qrcode');

// DOM references for Retrieve URL widget
const shortUrlInput = document.getElementById('shortUrlInput');
const retrieveBtn = document.getElementById('retrieveBtn');
const retrieveResult = document.getElementById('retrieveResult');

/********************************************
 * Generate Short URL
 ********************************************/
if (generateBtn) {
  generateBtn.addEventListener('click', async () => {
    if (!longUrlInput || !shortenResult) return;
    const longUrl = longUrlInput.value.trim();
    // 将自定义别名作为 alias 传递给后端（注意字段名称必须和后端一致）
    const aliasValue = customAliasInput ? customAliasInput.value.trim() : "";
    const alias = aliasValue === "" ? null : aliasValue;
    
    if (!longUrl) {
      shortenResult.textContent = 'Please enter a valid long URL.';
      return;
    }
    
    try {
      // 从 localStorage 中获取 token
      const token = localStorage.getItem("token") || "";
      console.log("Token:", token);
      const response = await fetch(`${BASE_URL}/shorten`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        // 请求体发送 longUrl 和 alias（注意 key 名称与后端一致）
        body: JSON.stringify({ longUrl, alias })
      });
      
      if (response.ok) {
        // 后端返回的是完整短链接（纯文本格式）
        const shortUrl = await response.text();
        shortenResult.innerHTML = `
          Short URL: <strong>${shortUrl}</strong>
          <button id="copyShortUrlBtn" class="copy-btn">
            <span class="copy-icon">📋</span> Copy
          </button>
        `;
        
        // 生成二维码（需要引入二维码库，例如 QRCode.js）
        if (qrCodeContainer) {
          qrCodeContainer.innerHTML = "";
          new QRCode(qrCodeContainer, {
            text: shortUrl,
            width: 128,
            height: 128,
            colorDark: "#000000",
            colorLight: "#ffffff",
            correctLevel: QRCode.CorrectLevel.H
          });
        }
        
        // 设置复制按钮事件
        const copyBtn = document.getElementById('copyShortUrlBtn');
        if (copyBtn) {
          copyBtn.addEventListener('click', () => {
            navigator.clipboard.writeText(shortUrl)
              .then(() => alert('Copied to clipboard!'))
              .catch(err => console.error('Failed to copy text: ', err));
          });
        }
      } else {
        const errorText = await response.text();
        shortenResult.textContent = errorText || 'Error generating short URL.';
      }
    } catch (err) {
      console.error(err);
      shortenResult.textContent = 'Something went wrong. Please try again.';
    }
  });
}

/********************************************
 * Retrieve Original URL
 ********************************************/
if (retrieveBtn) {
  retrieveBtn.addEventListener('click', async () => {
    if (!shortUrlInput || !retrieveResult) return;
    const shortUrl = shortUrlInput.value.trim();
    retrieveResult.textContent = '';
    
    if (!shortUrl) {
      retrieveResult.textContent = 'Please enter a short URL.';
      return;
    }
    
    try {
      // 对短链接进行 URL 编码
      const encodedShortUrl = encodeURIComponent(shortUrl);
      const response = await fetch(`${BASE_URL}/retrieve/${encodedShortUrl}`, {
        method: 'GET'
      });
      
      if (response.ok) {
        // 后端返回原始长链接（纯文本格式）
        const longUrl = await response.text();
        retrieveResult.innerHTML = `
          <div class="result-label">Original URL:</div>
          <div class="retrieve-box">
            <input type="text" class="retrieve-input" value="${longUrl}" readonly />
            <button id="copyOriginalUrlBtn" class="copy-btn">
              <span class="copy-icon">📋</span> Copy
            </button>
          </div>
        `;
        
        // 设置复制按钮事件
        const copyOriginalUrlBtn = document.getElementById('copyOriginalUrlBtn');
        if (copyOriginalUrlBtn) {
          copyOriginalUrlBtn.addEventListener('click', () => {
            navigator.clipboard.writeText(longUrl)
              .then(() => alert('Copied original URL to clipboard!'))
              .catch(err => console.error('Failed to copy text: ', err));
          });
        }
      } else {
        const errorText = await response.text();
        retrieveResult.textContent = errorText || 'No record found for this short URL.';
      }
    } catch (err) {
      console.error(err);
      retrieveResult.textContent = 'Something went wrong. Please try again.';
    }
  });
}

/********************************************
 * Support Form Logic (support.html)
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
 * Profile Settings (settings.html)
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
    
    alert(`Profile updated!\nName: ${newDisplayName}\nEmail: ${newEmail}\n(Demo Only)`);
    profileSettingsForm.reset();
  });
}

/********************************************
 * Clear All Data (settings.html)
 ********************************************/
const clearAllDataBtn = document.getElementById("clearAllDataBtn");
if (clearAllDataBtn) {
  clearAllDataBtn.addEventListener("click", () => {
    if (!confirm("Are you sure you want to clear all local data? This action cannot be undone.")) {
      return;
    }
    localStorage.clear();
    alert("All local data has been cleared! Refresh the page to see changes.");
  });
}
