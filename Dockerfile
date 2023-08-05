# 第一阶段：使用Gradle镜像构建Jar文件
FROM gradle:7.4.2-jdk11 AS builder

# 设置工作目录
WORKDIR /app

# 复制项目文件到容器的工作目录中
COPY . /app

# 构建项目
RUN gradle build

# 第二阶段：使用JRE镜像运行应用程序
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 从第一阶段复制构建好的Jar文件到当前镜像
COPY --from=builder /app/build/libs/unidbg-fetch-qsign-1.1.7-all.jar .
COPY --from=builder /app/txlib .

# 暴露项目运行的端口号
EXPOSE 8080

# 启动项目
CMD ["java", "-jar", "unidbg-fetch-qsign-1.1.7-all.jar", "--basePath=/app/txlib/8.9.70"]
