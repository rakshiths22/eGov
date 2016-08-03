build:
	cd egov;mvn clean package  -pl '!egov-database' -DskipTests
	docker build -f infra/app/Dockerfile --tag=egovernments/egov .

run:
	cd infra;docker-compose up

stop:
	cd infra; docker-compose kill; docker-compose rm;

dev:
	cd infra; docker-compose -f dev.yml up
