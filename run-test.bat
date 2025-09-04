@echo off
chcp 65001 >nul

echo ==========================================
echo AI Agent Station 配置化改造测试
echo ==========================================

REM 检查Java环境
echo 1. 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装Java
    pause
    exit /b 1
)
echo Java环境检查通过

REM 检查Maven环境
echo 2. 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请先安装Maven
    pause
    exit /b 1
)
echo Maven环境检查通过

REM 检查数据库连接
echo 3. 检查数据库连接...
echo 请确保MySQL数据库已启动，并且已执行以下SQL脚本：
echo   - database/sql/ai-agent-station-configurable.sql
echo   - database/sql/init-config-data.sql
echo.

REM 编译项目
echo 4. 编译项目...
cd ..
if exist "pom.xml" (
    echo 找到项目根目录，开始编译...
    mvn clean compile -DskipTests
    if %errorlevel% equ 0 (
        echo 编译成功！
    ) else (
        echo 编译失败，请检查代码错误
        pause
        exit /b 1
    )
) else (
    echo 错误: 未找到项目根目录的pom.xml文件
    pause
    exit /b 1
)

REM 运行测试
echo 5. 运行测试...
mvn test -Dtest=*ConfigurableTest
if %errorlevel% equ 0 (
    echo 测试通过！
) else (
    echo 测试失败，请检查配置和代码
    pause
    exit /b 1
)

echo ==========================================
echo 配置化改造测试完成！
echo ==========================================
pause
