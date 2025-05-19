FROM openjdk:21-jdk
COPY entrypoint.sh /opt/entrypoint.sh
COPY target/datadog-large-trace-0.0.1-SNAPSHOT.jar /opt/app.jar
RUN chmod 555 /opt/entrypoint.sh
ENTRYPOINT ["/opt/entrypoint.sh"]
CMD ["startapp"]
