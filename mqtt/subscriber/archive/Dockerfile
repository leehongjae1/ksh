FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY option.yml .
COPY CmsPcubscriber.jar .
ENTRYPOINT ["java","-jar","CmsPcSubscriber.jar","-yml"]