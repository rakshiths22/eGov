FROM jboss/wildfly:10.0.0.Final
ADD https://jdbc.postgresql.org/download/postgresql-9.4.1208.jar /tmp/postgresql-9.4.1208.jar
USER root
RUN chown jboss:jboss /tmp/postgresql-9.4.1208.jar
USER jboss
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --command="module add --name=org.postgres --resources=/tmp/postgresql-9.4.1208.jar --dependencies=javax.api,javax.transaction.api"
ADD http://repo1.maven.org/maven2/javax/jcr/jcr/2.0/jcr-2.0.jar /tmp/jcr-2.0.jar
USER root
RUN chown jboss:jboss /tmp/jcr-2.0.jar
USER jboss
ENV JAVA_OPTS "-server -Xms512m -Xmx2g -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=512m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true"
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --command="module add --name=javax.jcr --resources=/tmp/jcr-2.0.jar --dependencies=javax.transaction.api"
ADD deployment/modules/egov-settings.zip /opt/jboss/wildfly/modules
ADD infra/app/standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
ADD egov/egov-ear/target/egov-ear-*.ear /opt/jboss/wildfly/standalone/deployments
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-Dspring.profiles.active=production", "-Djboss.as.management.blocking.timeout=1400", "-DXms512m","-DXmx2g"]
