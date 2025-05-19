#!/bin/sh
#
# docker-entrypoint for docker application

export JAVA_OPTIONS="${JAVA_OPTIONS} ${JAVA_OPTS} -Dfile.encoding=UTF-8 -Xms256M -Xmx256M -Duser.timezone=UTC ${APP_OPTS}"

if [ "$1" = "startapp" ]; then
    echo exec java -jar /opt/app.jar
    echo "---------------------------------------------"
    exec java -jar /opt/app.jar
else
    echo exec "$@"
    echo "---------------------------------------------"
    exec "$@"
fi
