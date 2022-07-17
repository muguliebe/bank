#!/bin/bash
docker build -t bank:0.1 .
docker run -d -p 8080:8080 bank:0.1
