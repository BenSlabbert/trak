#!/usr/bin/env bash

docker-compose up --scale worker=4 worker logstash engine rds rabbit redis es es-slave-1 search
