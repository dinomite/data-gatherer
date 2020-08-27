#!/usr/bin/env bash

while true; do
  echo '{"time" : "2020-08-23 14:12:52", "model" : "Test-Sensor", "id" : 1, "channel" : "A", "battery_ok" : 1, "temperature_C" : 23.200, "humidity" : 54, "mic" : "CHECKSUM"}'
  sleep 5
  echo '{"time" : "2020-08-23 14:13:01", "brand" : "OS", "model" : "Other-Test-Sensor", "id" : 2, "channel" : 3, "battery_ok" : 1, "temperature_C" : 33.000}'
  sleep 5
  echo '{"time" : "2020-08-23 14:12:52", "model" : "Test-Sensor", "id" : 1, "channel" : "A", "battery_ok" : 1, "temperature_C" : 36.000, "humidity" : 54, "mic" : "CHECKSUM"}'
  sleep 5
  echo '{"time" : "2020-08-23 14:13:01", "brand" : "OS", "model" : "Other-Test-Sensor", "id" : 2, "channel" : 3, "battery_ok" : 1, "temperature_C" : 47.000}'
  sleep 5
done
