#!/bin/bash

###############################################################################
# 蚂蚁记账后端服务重启脚本
# 适用于 CentOS 7.9
###############################################################################

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目配置
APP_DIR=$(cd "$(dirname "$0")"; pwd)
STOP_SCRIPT="${APP_DIR}/stop.sh"
START_SCRIPT="${APP_DIR}/start.sh"

# 重启服务
restart_service() {
    echo -e "${YELLOW}========================================${NC}"
    echo -e "${YELLOW}重启服务${NC}"
    echo -e "${YELLOW}========================================${NC}"
    
    # 获取环境参数（如果有）
    PROFILE=${1:-prod}
    
    # 停止服务
    echo -e "${YELLOW}正在停止服务...${NC}"
    bash "$STOP_SCRIPT"
    
    # 等待 2 秒
    sleep 2
    
    # 启动服务
    echo -e "${YELLOW}正在启动服务...${NC}"
    bash "$START_SCRIPT" "$PROFILE"
}

# 主函数
main() {
    restart_service "$@"
}

# 执行主函数
main "$@"
