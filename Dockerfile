FROM --platform=linux/amd64 mcr.microsoft.com/playwright:v1.45.0-noble
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk maven && \
    apt-get clean

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:resolve -B

COPY src ./src

CMD ["mvn", "test", "-B"]