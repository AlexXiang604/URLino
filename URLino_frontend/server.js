const express = require('express');
const path = require('path');
const app = express();

// 让 public/ 下的静态文件可被访问
app.use(express.static(path.join(__dirname, 'public')));

// 如果是多页面应用，你可以直接访问不同的 html
// 如果是单页面应用(SPA)，你可做一个通配路由fallback
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'login.html'));
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}...`);
});
