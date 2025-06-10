FROM openjdk:21-jdk
COPY entrypoint.sh /opt/entrypoint.sh
COPY target/datadog-large-trace-0.0.1-SNAPSHOT.jar /opt/app.jar
COPY ddprof /opt/ddprof
RUN chmod 777 /opt/entrypoint.sh /opt/ddprof
ENTRYPOINT ["/opt/entrypoint.sh"]
CMD ["startapp"]
