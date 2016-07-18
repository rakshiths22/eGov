build:
	cd egov;mvn clean package  -pl '!egov-database' -DskipTests
	docker build -f infra/app/Dockerfile --tag=egovernments/egov .

run:
	cd infra;docker-compose up
