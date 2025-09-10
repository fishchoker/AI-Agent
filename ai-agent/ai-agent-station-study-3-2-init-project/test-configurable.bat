@echo off
echo 开始测试配置化功能...
echo.

echo 1. 清理并编译项目...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)
echo 编译成功！

echo.
echo 2. 运行配置化测试...
call mvn test -Dtest=ConfigurableTest -q
if %errorlevel% neq 0 (
    echo 测试失败！请检查数据库连接配置。
    echo 确保MySQL数据库已启动并创建了ai-agent-station数据库。
    pause
    exit /b 1
)
echo 测试成功！

echo.
echo 3. 生成测试报告...
call mvn surefire-report:report -q

echo.
echo 配置化功能测试完成！
echo 测试报告位置: target/site/surefire-report.html
pause
