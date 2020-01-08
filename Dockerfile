# alpine:3.11.2
FROM alpine@sha256:3983cc12fb9dc20a009340149e382a18de6a8261b0ac0e8f5fcdf11f8dd5937e AS jvm

ARG JDK_VERSION=zulu11.35.15-ca-jdk11.0.5

RUN apk add ca-certificates && rm -rf /var/cache/apk/*

RUN mkdir -p /usr/local/zulu \
    && wget https://cdn.azul.com/zulu/bin/${JDK_VERSION}-linux_musl_x64.tar.gz -qO - \
    | tar -xz -f - -C /usr/local/zulu

ENV JAVA_HOME=/usr/local/zulu/${JDK_VERSION}-linux_musl_x64
ENV PATH $PATH:$JAVA_HOME/bin

FROM jvm AS build

COPY /gradle ./gradle
COPY /gradlew ./

RUN ./gradlew --version

COPY /build.gradle.kts /settings.gradle.kts ./
RUN ./gradlew tasks > /dev/null

COPY /src ./src

RUN ./gradlew distTar

RUN tar xf build/distributions/data-gatherer.tar

FROM jvm

ADD config /data-gatherer/config
COPY --from=build /data-gatherer /data-gatherer/

WORKDIR /data-gatherer/
ENTRYPOINT ["bin/data-gatherer", "config"]