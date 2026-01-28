FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY lumos-web/target/lumos-web-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
# 使用 Shell 格式以支持环境变量替换
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]