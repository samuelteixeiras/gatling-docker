FROM ubuntu:latest
LABEL authors="steixeira"

ENTRYPOINT ["top", "-b"]