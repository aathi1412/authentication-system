FROM ubuntu:latest
LABEL authors="aathi"

ENTRYPOINT ["top", "-b"]