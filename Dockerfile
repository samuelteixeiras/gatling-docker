FROM gradle:latest AS BUILD
ENV HOME_DIR=/gatling
WORKDIR $HOME_DIR
COPY src/ $HOME_DIR/src
COPY gradle/ $HOME_DIR/gradle
COPY build.gradle settings.gradle gradlew $HOME_DIR/
RUN gradle build -x test || return 0