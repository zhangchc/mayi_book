#!/bin/bash

###############################################################################
# 蚂蚁记账后端服务停止脚本
# 适用于 CentOS 7.9
###############################################################################

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目配置
APP_NAME="mayi-book"
APP_DIR=$(cd "$(dirname "$0")"; pwd)
PID_FILE="${APP_DIR}/${APP_NAME}.pid"

# 停止服务
stop_service() {
    echo -e "${YELLOW}========================================${NC}"
    echo -e "${YELLOW}停止 ${APP_NAME} 服务${NC}"
    echo -e "${YELLOW}========================================${NC}"
    
    if [ ! -f "$PID_FILE" ]; then
        echo -e "${YELLOW}警告: PID 文件不存在，服务可能未运行${NC}"
        
        # 尝试通过进程名查找
        PID=$(ps aux | grep "${APP_NAME}" | grep -v grep | awk '{print $2}' | head -1)
        if [ -z "$PID" ]; then
            echo -e "${YELLOW}服务未运行${NC}"
            exit 0
        else
            echo -e "${YELLOW}找到进程 PID: $PID${NC}"
        fi
    else
        PID=$(cat "$PID_FILE")
        echo -e "${YELLOW}从 PID 文件读取: $PID${NC}"
    fi
    
    # 检查进程是否存在
    if ! ps -p "$PID" > /dev/null 2>&1; then
        echo -e "${YELLOW}进程不存在，可能已经停止${NC}"
        rm -f "$PID_FILE"
        exit 0
    fi
    
    # 优雅停止
    echo -e "${YELLOW}正在停止进程 $PID...${NC}"
    kill "$PID"
    
    # 等待进程停止（最多等待 30 秒）
    for i in {1..30}; do
        if ! ps -p "$PID" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ 服务已停止${NC}"
            rm -f "$PID_FILE"
            exit 0
        fi
        sleep 1
        echo -n "."
    done
    echo ""
    
    # 如果还在运行，强制杀死
    if ps -p "$PID" > /dev/null 2>&1; then
        echo -e "${YELLOW}进程未响应，强制停止...${NC}"
        kill -9 "$PID"
        sleep 2
        
        if ! ps -p "$PID" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ 服务已强制停止${NC}"
            rm -f "$PID_FILE"
        else
            echo -e "${RED}✗ 无法停止服务，请手动检查${NC}"
            exit 1
        fi
    fi
}

# 主函数
main() {
    stop_service
}

# 执行主函数
main
