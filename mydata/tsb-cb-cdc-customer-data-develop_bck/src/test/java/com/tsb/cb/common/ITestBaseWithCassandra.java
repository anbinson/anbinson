package com.tsb.cb.common;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.junit.Before;
import org.junit.ClassRule;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.CassandraContainer;

@ContextConfiguration(initializers = ITestBaseWithCassandra.Initializer.class)
@EnableConfigurationProperties
public class ITestBaseWithCassandra extends ITestBase {
    public static final String KEY_SPACE = "testkeyspace";

    @ClassRule
    public static CassandraContainer cassandra =
            (CassandraContainer) new CassandraContainer("cassandra:3")
                    .withExposedPorts(9042);

    @Before
    public void setupDatabase() {
        Cluster cluster = cassandra.getCluster();

        try (Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEY_SPACE + " WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};");
            session.execute("USE " + KEY_SPACE + ";");
            session.execute("DROP TABLE IF EXISTS user;");
            session.execute("DROP TABLE IF EXISTS profile;");
            session.execute("DROP TABLE IF EXISTS device;");
            session.execute("DROP TABLE IF EXISTS hmac;");
            session.execute("CREATE TABLE user(user_id text, das_user text, numpersona text, hmac text, hmac_created_at text, hmac_device_id text, PRIMARY KEY (user_id));");
            session.execute("CREATE TABLE profile(numpersona text, first_name text, last_name text,  PRIMARY KEY (numpersona));");
            session.execute("CREATE TABLE device(das_user text, device_id text, PRIMARY KEY (das_user));");
            session.execute("CREATE TABLE hmac(hmac text, user_id text, device_id text, hmac_created_at text, PRIMARY KEY (hmac));");
        }
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.cassandra.contact-points=" + cassandra.getContainerIpAddress(),
                    "spring.data.cassandra.port=" + cassandra.getMappedPort(9042)
            );
            values.applyTo(configurableApplicationContext);

            Cluster cluster = cassandra.getCluster();

            try (Session session = cluster.connect()) {
                session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEY_SPACE + " WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};");
            }
        }
    }
}
