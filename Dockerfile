# 使用官方JDK基础镜像（建议使用openjdk:8-jdk-alpine更轻量）
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 拷贝Spring Boot应用jar包（假设你使用的是Maven打包后的jar）
COPY target/huinong-platform.jar app.jar

# 设置时区为东八区（可选）
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]