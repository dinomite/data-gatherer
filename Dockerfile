# alpine:3.11.2
FROM alpine@sha256:3983cc12fb9dc20a009340149e382a18de6a8261b0ac0e8f5fcdf11f8dd5937e

ARG JDK_VERSION=zulu11.35.15-ca-jdk11.0.5

RUN apk add ca-certificates && rm -rf /var/cache/apk/*

RUN mkdir -p /usr/local/zulu \
    && wget https://cdn.azul.com/zulu/bin/${JDK_VERSION}-linux_musl_x64.tar.gz -qO - \
    | tar -xz -f - -C /usr/local/zulu

ENV JAVA_HOME=/usr/local/zulu/${JDK_VERSION}-linux_musl_x64
ENV PATH $PATH:$JAVA_HOME/bin

COPY ./docker-entrypoint.sh /data-gatherer/

ADD config /data-gatherer/config
ADD build/distributions/data-gatherer.tar /data-gatherer/

WORKDIR /data-gatherer/
ENTRYPOINT ["./docker-entrypoint.sh", "config"]