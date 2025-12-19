@echo off
echo ============================================
echo    配置防火墙规则 - Docsify Server
echo ============================================
echo.

REM 检查管理员权限
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 需要管理员权限来配置防火墙
    echo 请右键点击此文件，选择"以管理员身份运行"
    pause
    exit /b 1
)

echo 🛡️ 正在配置防火墙规则...

REM 删除现有规则
netsh advfirewall firewall delete rule name="Docsify" >nul 2>&1

REM 添加新规则
netsh advfirewall firewall add rule name="Docsify" dir=in action=allow protocol=TCP localport=3000

if %errorlevel% equ 0 (
    echo ✅ 防火墙规则配置成功！
    echo.
    echo 现在局域网内的设备可以访问:
    echo 🌐 http://10.150.91.84:3000
) else (
    echo ❌ 防火墙规则配置失败
)

echo.
echo 按任意键退出...
pause >nul