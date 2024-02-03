FROM gradle:latest AS BUILD
ENV APP_HOME=/gatling
WORKDIR $APP_HOME
COPY src/ $APP_HOME/src
COPY gradle/ $APP_HOME/gradle
COPY build.gradle settings.gradle gradlew  $APP_HOME/
RUN gradle build -x test || return 0