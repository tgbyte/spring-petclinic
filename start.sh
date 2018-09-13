#!/bin/sh

# echo "Xms: ${JAVA_XMS}"
# echo "Xmx: ${JAVA_XMX}"
# echo "MaxMetaspaceSize: ${JAVA_MAX_METASPACE_SIZE}"
# echo "JAVA_OPTS: ${JAVA_OPTS}"
# echo "Arguments: $@"

COMMON_JAVA_OPTS="-server
  -XX:+ExitOnOutOfMemoryError
  -Djava.security.egd=file:/dev/./urandom"

if [ -n "$JAVA_MAX_METASPACE_SIZE" ]; then
  COMMON_JAVA_OPTS="$COMMON_JAVA_OPTS -XX:MaxMetaspaceSize=${JAVA_MAX_METASPACE_SIZE}"
fi

if [ -n "$JAVA_INITIAL_RAM_PERCENTAGE" ]; then
  COMMON_JAVA_OPTS="$COMMON_JAVA_OPTS -XX:InitialRAMPercentage=${JAVA_INITIAL_RAM_PERCENTAGE}"
fi

if [ -n "$JAVA_MIN_RAM_PERCENTAGE" ]; then
  COMMON_JAVA_OPTS="$COMMON_JAVA_OPTS -XX:MinRAMPercentage=${JAVA_MIN_RAM_PERCENTAGE}"
fi

if [ -n "$JAVA_MAX_RAM_PERCENTAGE" ]; then
  COMMON_JAVA_OPTS="$COMMON_JAVA_OPTS -XX:MaxRAMPercentage=${JAVA_MAX_RAM_PERCENTAGE}"
fi

if [ -n "$JAVA_XMS" ]; then
  COMMON_JAVA_OPTS="$COMMON_JAVA_OPTS -Xms${JAVA_XMS}"
fi

if [ -n "$JAVA_XMX" ]; then
  COMMON_JAVA_OPTS="$COMMON_JAVA_OPTS -Xmx${JAVA_XMX}"
fi

if [ -n "$ELASTIC_APM_SERVER_URL" ]; then
  COMMON_JAVA_OPTS="$COMMON_JAVA_OPTS -javaagent:/elastic-apm-agent.jar"
fi

exec java \
  ${COMMON_JAVA_OPTS} \
  ${JAVA_OPTS} \
  -jar /app.jar \
  "$@"
