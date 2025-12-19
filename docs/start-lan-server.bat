@echo off
echo ============================================
echo    FFmpeg-Java Docsify LAN Server
echo ============================================
echo.

REM 检查 Node.js 是否安装
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 Node.js，请先安装 Node.js
    echo 下载地址: https://nodejs.org/
    pause
    exit /b 1
)

REM 获取当前目录
cd /d "%~dp0"

REM 停止现有的服务器
echo 🔄 停止现有服务器...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000 ^| findstr LISTENING') do (
    echo 正在终止进程 %%a...
    taskkill /PID %%a /F >nul 2>&1
)

REM 启动新服务器
echo 🚀 启动局域网服务器...
echo.
echo 📍 本地访问: http://localhost:3000
echo 🌐 局域网访问: http://10.150.91.84:3000
echo.
echo 按 Ctrl+C 停止服务器
echo ============================================
echo.

node server.js

pause