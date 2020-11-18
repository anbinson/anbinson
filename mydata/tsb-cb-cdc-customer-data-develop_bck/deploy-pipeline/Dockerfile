FROM openjdk:11-jre-slim

RUN groupadd --gid 1000 tsb && \
    useradd --system --no-log-init -u 1000 -g 1000 tsb

WORKDIR /opt/tsb

COPY target/*.jar ./app.jar

RUN chown -R tsb:tsb /opt/tsb

USER tsb:tsb

CMD [ "java", \
      "-jar", \
      "app.jar" ]
