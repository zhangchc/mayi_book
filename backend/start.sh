#!/bin/bash

###############################################################################
# 蚂蚁记账后端服务启动脚本
# 适用于 CentOS 7.9
# 支持环境区分：dev, test, prod（默认）
###############################################################################

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目配置
APP_NAME="mayi-book"
APP_VERSION="1.0.0"
JAR_NAME="${APP_NAME}-${APP_VERSION}.jar"
APP_DIR=$(cd "$(dirname "$0")"; pwd)
JAR_PATH="${APP_DIR}/${JAR_NAME}"
PID_FILE="${APP_DIR}/${APP_NAME}.pid"
LOG_DIR="${APP_DIR}/logs"
LOG_FILE="${LOG_DIR}/${APP_NAME}.log"
ERROR_LOG="${LOG_DIR}/${APP_NAME}-error.log"

# 默认环境为 prod
PROFILE=${1:-prod}

# 支持的环境列表
VALID_PROFILES=("dev" "test" "prod")

# 检查环境参数是否有效
is_valid_profile() {
    for valid_profile in "${VALID_PROFILES[@]}"; do
        if [ "$1" == "$valid_profile" ]; then
            return 0
        fi
    done
    return 1
}

# 显示使用说明
show_usage() {
    echo -e "${YELLOW}使用方法:${NC}"
    echo "  $0 [环境]"
    echo ""
    echo -e "${YELLOW}环境参数:${NC}"
    echo "  dev   - 开发环境"
    echo "  test  - 测试环境"
    echo "  prod  - 生产环境（默认）"
    echo ""
    echo -e "${YELLOW}示例:${NC}"
    echo "  $0          # 使用生产环境启动"
    echo "  $0 prod     # 使用生产环境启动"
    echo "  $0 test     # 使用测试环境启动"
    echo "  $0 dev      # 使用开发环境启动"
}

# 检查环境参数
if [ "$1" != "" ] && ! is_valid_profile "$1"; then
    echo -e "${RED}错误: 无效的环境参数 '$1'${NC}"
    echo ""
    show_usage
    exit 1
fi

# 检查 Java 环境
check_java() {
    if ! command -v java &> /dev/null; then
        echo -e "${RED}错误: 未找到 Java 环境，请先安装 Java 8 或更高版本${NC}"
        exit 1
    fi
    
    # 获取完整版本号
    JAVA_VERSION_FULL=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    JAVA_VERSION=$(echo $JAVA_VERSION_FULL | cut -d'.' -f1-2)
    
    # 解析主版本号
    # Java 8 及之前版本格式：1.8.0 -> 主版本号是 8
    # Java 9 及之后版本格式：9, 10, 11, 17 -> 主版本号直接是数字
    JAVA_MAJOR=$(echo $JAVA_VERSION | cut -d'.' -f1)
    JAVA_MINOR=$(echo $JAVA_VERSION | cut -d'.' -f2)
    
    if [ "$JAVA_MAJOR" = "1" ]; then
        # Java 8 及之前版本，使用第二位数字作为主版本号
        JAVA_MAJOR_VERSION=$JAVA_MINOR
    else
        # Java 9 及之后版本，直接使用第一位数字
        JAVA_MAJOR_VERSION=$JAVA_MAJOR
    fi
    
    if [ "$JAVA_MAJOR_VERSION" -lt 8 ]; then
        echo -e "${RED}错误: Java 版本过低，需要 Java 8 或更高版本，当前版本: $JAVA_VERSION_FULL${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ Java 环境检查通过，版本: $JAVA_VERSION_FULL${NC}"
}

# 检查 JAR 文件是否存在
check_jar() {
    if [ ! -f "$JAR_PATH" ]; then
        echo -e "${RED}错误: JAR 文件不存在: $JAR_PATH${NC}"
        echo -e "${YELLOW}提示: 请先执行 mvn clean package 构建项目${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ JAR 文件检查通过: $JAR_PATH${NC}"
}

# 检查进程是否已运行
check_running() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null 2>&1; then
            echo -e "${YELLOW}警告: 服务已在运行中，PID: $PID${NC}"
            echo -e "${YELLOW}如需重启，请先执行: $0 stop${NC}"
            exit 1
        else
            # PID 文件存在但进程不存在，删除旧的 PID 文件
            rm -f "$PID_FILE"
        fi
    fi
}

# 创建日志目录
create_log_dir() {
    if [ ! -d "$LOG_DIR" ]; then
        mkdir -p "$LOG_DIR"
        echo -e "${GREEN}✓ 创建日志目录: $LOG_DIR${NC}"
    fi
}

# 启动服务
start_service() {
    echo -e "${YELLOW}========================================${NC}"
    echo -e "${YELLOW}启动 ${APP_NAME} 服务${NC}"
    echo -e "${YELLOW}环境: ${PROFILE}${NC}"
    echo -e "${YELLOW}========================================${NC}"
    
    # 执行检查
    check_java
    check_jar
    check_running
    create_log_dir
    
    # JVM 参数配置（可根据服务器配置调整）
    JVM_OPTS="-Xms512m -Xmx1024m"
    JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
    JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=200"
    JVM_OPTS="$JVM_OPTS -XX:+HeapDumpOnOutOfMemoryError"
    JVM_OPTS="$JVM_OPTS -XX:HeapDumpPath=${LOG_DIR}/heap_dump.hprof"
    JVM_OPTS="$JVM_OPTS -Dfile.encoding=UTF-8"
    JVM_OPTS="$JVM_OPTS -Duser.timezone=Asia/Shanghai"
    
    # Spring Boot 参数
    SPRING_OPTS="--spring.profiles.active=${PROFILE}"
    SPRING_OPTS="$SPRING_OPTS --server.port=9091"
    
    # 启动命令
    nohup java $JVM_OPTS -jar "$JAR_PATH" $SPRING_OPTS > "$LOG_FILE" 2> "$ERROR_LOG" &
    
    # 保存 PID
    PID=$!
    echo $PID > "$PID_FILE"
    
    # 等待服务启动
    echo -e "${YELLOW}等待服务启动...${NC}"
    sleep 3
    
    # 检查进程是否还在运行
    if ps -p "$PID" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ 服务启动成功！${NC}"
        echo -e "${GREEN}  PID: $PID${NC}"
        echo -e "${GREEN}  环境: ${PROFILE}${NC}"
        echo -e "${GREEN}  日志文件: $LOG_FILE${NC}"
        echo -e "${GREEN}  错误日志: $ERROR_LOG${NC}"
        echo ""
        echo -e "${YELLOW}查看日志: tail -f $LOG_FILE${NC}"
        echo -e "${YELLOW}停止服务: $0 stop${NC}"
    else
        echo -e "${RED}✗ 服务启动失败，请查看错误日志: $ERROR_LOG${NC}"
        rm -f "$PID_FILE"
        exit 1
    fi
}

# 主函数
main() {
    start_service
}

# 执行主函数
main
