FROM postgres:15.1-alpine
VOLUME /var/lib/postgresql/data
ENV POSTGRES_PASSWORD=p@ssw0rd

COPY init.sql /docker-entrypoint-initdb.d/