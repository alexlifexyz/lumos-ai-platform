# 使用更轻量、通用的镜像
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 拷贝本地已打包好的 jar
COPY lumos-web/target/lumos-web-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
