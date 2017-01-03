FROM openjdk:8-jdk

MAINTAINER Sonny Garcia <sonnygarcia@icloud.com>

# install maven
ENV MAVEN_VERSION 3.3.9
RUN curl -O http://www-us.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
 && tar xzf apache-maven-$MAVEN_VERSION-bin.tar.gz -C /usr/local/lib
ENV PATH $PATH:/usr/local/lib/apache-maven-$MAVEN_VERSION/bin

# copy src code into image
COPY . /usr/local/src/eGov/egovrest

# build out all of the egov-*.jar files
RUN cd /usr/local/src/eGov/egovrest/egov-pgr \
 && mvn clean install -s settings.xml -DskipTest

RUN cd /usr/local/src/eGov/egovrest \
 && mvn clean package -s settings.xml -DskipTests
