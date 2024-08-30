#!/bin/zsh
docker build -t local-postgres-db-with-volume ./

docker run --name deepfij-pgsql -v /Users/fijimf/db:/var/lib/postgresql/data -p 5432:5432 local-postgres-db-with-volume -c synchronous_commit=off -c default_transaction_isolation='read uncommitted'

docker rm deepfij-pgsql
