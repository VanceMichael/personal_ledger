@echo off
chcp 65001 >nul
echo ==================================
echo   个人记账本应用启动脚本
echo ==================================
echo.
echo 请选择启动方式：
echo 1. Docker Compose 一键启动（推荐）
echo 2. 停止所有服务
echo 3. 查看服务状态
echo.
set /p choice=请输入选项 (1-3): 

if "%choice%"=="1" (
    echo.
    echo 正在使用 Docker Compose 启动所有服务...
    docker-compose up -d
    echo.
    echo 服务启动完成！
    echo 前端地址: http://localhost:3000
    echo 后端地址: http://localhost:8080
    echo 数据库端口: 3306
) else if "%choice%"=="2" (
    echo.
    echo 正在停止所有服务...
    docker-compose down
    echo 服务已停止
) else if "%choice%"=="3" (
    echo.
    echo 当前服务状态：
    docker-compose ps
) else (
    echo 无效选项
)

pause
