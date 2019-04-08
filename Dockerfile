FROM hub.furycloud.io/mercadolibre/java-gradle:jdk8

# Default value in image is "build".
# Override the following env variable to call another task for packaging your app
ENV GRADLE_PACKAGE="bootRepackage"

ENV GRADLE_PACKAGE_PATH="build/libs/*.war"

# Default value in image is "test jacocoTestReport".
# Override the following env variable to call another task for testing your app
ENV GRADLE_TEST="test jacocoTestReport"


# Default value in image is "check".
# Override the following env variable to call another task for running your app when using "fury run"
ENV GRADLE_RUN="bootRun"


# Make sure to add your CodeCov token!
##### CODECOV #####
ENV CODECOV_TOKEN=""

