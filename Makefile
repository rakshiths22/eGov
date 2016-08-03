build:
	cd egov;mvn clean package  -pl '!egov-database' -DskipTests
	docker build -f infra/app/Dockerfile --tag=egovernments/egov .

up:
	cd infra;docker-compose up

stop:
	cd infra; docker-compose stop

start:
	cd infra; docker-compose start

dev:
	cd infra; docker-compose -f dev.yml up
