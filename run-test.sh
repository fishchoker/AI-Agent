#!/bin/bash

# AI Agent Station 配置化改造测试脚本

echo "=========================================="
echo "AI Agent Station 配置化改造测试"
echo "=========================================="

# 检查Java环境
echo "1. 检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java"
    exit 1
fi
echo "Java版本: $(java -version 2>&1 | head -n 1)"

# 检查Maven环境
echo "2. 检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请先安装Maven"
    exit 1
fi
echo "Maven版本: $(mvn -version | head -n 1)"

# 检查数据库连接
echo "3. 检查数据库连接..."
echo "请确保MySQL数据库已启动，并且已执行以下SQL脚本："
echo "  - database/sql/ai-agent-station-configurable.sql"
echo "  - database/sql/init-config-data.sql"

# 编译项目
echo "4. 编译项目..."
cd ..
if [ -f "pom.xml" ]; then
    echo "找到项目根目录，开始编译..."
    mvn clean compile -DskipTests
    if [ $? -eq 0 ]; then
        echo "编译成功！"
    else
        echo "编译失败，请检查代码错误"
        exit 1
    fi
else
    echo "错误: 未找到项目根目录的pom.xml文件"
    exit 1
fi

# 运行测试
echo "5. 运行测试..."
mvn test -Dtest=*ConfigurableTest
if [ $? -eq 0 ]; then
    echo "测试通过！"
else
    echo "测试失败，请检查配置和代码"
    exit 1
fi

echo "=========================================="
echo "配置化改造测试完成！"
echo "=========================================="
