#!/bin/bash

# Lumos AI Platform 管理脚本

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

usage() {
    echo "用法: $0 {start|stop|restart|status|logs}"
    exit 1
}

if [ $# -lt 1 ]; then
    usage
fi

ACTION=$1

case "$ACTION" in
    start)
        echo -e "${GREEN}正在编译项目...${NC}"
        mvn clean package -DskipTests
        if [ $? -ne 0 ]; then
            echo -e "${RED}编译失败，请检查代码！${NC}"
            exit 1
        fi
        echo -e "${GREEN}正在启动 Docker 容器...${NC}"
        docker-compose up -d --build
        echo -e "${GREEN}服务启动成功！${NC}"
        ;;
    stop)
        echo -e "${RED}正在停止 Docker 容器...${NC}"
        docker-compose down
        ;;
    restart)
        $0 stop
        $0 start
        ;;
    status)
        docker-compose ps
        ;;
    logs)
        docker-compose logs -f lumos-app
        ;;
    *)
        usage
        ;;
esac
