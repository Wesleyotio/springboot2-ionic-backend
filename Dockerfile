

FROM maven:3.6.3-jdk-11


COPY . /app
COPY --chown=www:www . /app