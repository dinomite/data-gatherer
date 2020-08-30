#!/usr/bin/env bash
set -eoux pipefail

./gradlew :producer:distTar
scp producer/build/distributions/producer.tar rtl-433:
ssh rtl-433 'tar xf producer.tar && systemctl restart data-producer.service'
