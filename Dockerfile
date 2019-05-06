FROM hub.furycloud.io/mercadolibre/java-gradle:jdk8

# Gradle Tasks
ENV GRADLE_PACKAGE="clean installDist -Pproduction"
ENV GRADLE_RUN="clean run -Pproduction"

ENV CODECOV_TOKEN=""