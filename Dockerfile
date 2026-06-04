FROM ubuntu:latest
LABEL authors="erick"

ENTRYPOINT ["top", "-b"]