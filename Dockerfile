FROM egov-jboss:latest

MAINTAINER Sonny Garcia <sonnygarcia@icloud.com>

# copy src code into image
COPY . /usr/local/src/eGov/egovrest

# build out all of the egov-*.jar files
RUN cd /usr/local/src/eGov/egovrest/egov-pgr \
 && mvn install -s settings.xml -DskipTest

RUN cd /usr/local/src/eGov/egovrest \
 && mvn package -s settings.xml -DskipTests
