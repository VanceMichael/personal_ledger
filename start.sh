#!/bin/bash

echo "=================================="
echo "  个人记账本应用启动脚本"
echo "=================================="

echo ""
echo "请选择启动方式："
echo "1. Docker Compose 一键启动（推荐）"
echo "2. 本地开发模式"
echo "3. 停止所有服务"
echo "4. 查看服务状态"
read -p "请输入选项 (1-4): " choice

case $choice in
    1)
        echo ""
        echo "正在使用 Docker Compose 启动所有服务..."
        docker-compose up -d
        echo ""
        echo "服务启动完成！"
        echo "前端地址: http://localhost:3000"
        echo "后端地址: http://localhost:8080"
        echo "数据库端口: 3306"
        ;;
    2)
        echo ""
        echo "本地开发模式"
        echo "请确保已安装 MySQL 8.0 并创建数据库"
        echo ""
        echo "启动后端服务..."
        cd backend && mvn spring-boot:run &
        BACKEND_PID=$!
        
        sleep 10
        
        echo "启动前端服务..."
        cd ../frontend && npm run dev &
        FRONTEND_PID=$!
        
        echo ""
        echo "服务启动中..."
        echo "后端PID: $BACKEND_PID"
        echo "前端PID: $FRONTEND_PID"
        echo ""
        echo "按 Ctrl+C 停止所有服务"
        wait
        ;;
    3)
        echo ""
        echo "正在停止所有服务..."
        docker-compose down
        echo "服务已停止"
        ;;
    4)
        echo ""
        echo "当前服务状态："
        docker-compose ps
        ;;
    *)
        echo "无效选项"
        exit 1
        ;;
esac
