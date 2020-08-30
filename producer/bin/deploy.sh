#!/usr/bin/env bash
set -eoux pipefail

gw :producer:distTar
scp producer/build/distributions/producer.tar rtl-433:
ssh rtl-433 'tar xf producer.tar'
ssh rtl-433 'systemctl restart data-producer.service'
