package com.tsb.ods.config;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.datastax.driver.core.RemoteEndpointAwareJdkSSLOptions;
import javax.net.ssl.SSLContext;
import org.apache.http.ssl.SSLContextBuilder;
import com.datastax.driver.core.SSLOptions;
@Configuration
@EnableCassandraRepositories(basePackages = "com.tsb.ods.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(CassandraConfig.class.getName());
	@Value("${spring.data.cassandra.contact-points:placeholder}")
	private String contactPoints;
	@Value("${spring.data.cassandra.port:0000}")
	private int port;
	@Value("${spring.data.cassandra.keyspace:placeholder}")
	private String keySpace;
	@Value("${spring.data.cassandra.username}")
	private String username;
	@Value("${spring.data.cassandra.password}")
	private String password;
	@Value("${spring.data.cassandra.ssl:false}")
	private String sslEnabled;
	@Value("${scylla.ssl.truststore.location:}")
	private String sslTrustStoreLocation;
	@Value("${scylla.ssl.truststore.password:}")
	private String sslTrustStorePassword;
	@Override
	protected String getKeyspaceName() {
		return this.keySpace;
	}
	@Override
	protected String getContactPoints() {
		return this.contactPoints;
	}
	@Override
	protected int getPort() {
		return this.port;
	}
	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.NONE;
	}
	@Override
	protected AuthProvider getAuthProvider() {
		return new PlainTextAuthProvider(this.username, this.password);
	}
	private SSLContext createSslContextWithTruststore() {
		try {
			return new SSLContextBuilder()
					.loadTrustMaterial(new File(sslTrustStoreLocation), sslTrustStorePassword.toCharArray())
					//.loadKeyMaterial(
							//KeyStore.getInstance(new File(sslKeyStoreLocation), sslKeyStorePassword.toCharArray()),
							//sslKeyStorePassword.toCharArray())
					.build();
		} catch (Exception e) {
			throw new SecurityException("Could not create SSL context", e);
		}
	}
	private SSLOptions createSslOptions() {
		return RemoteEndpointAwareJdkSSLOptions.builder().withSSLContext(createSslContextWithTruststore())
				// .withCipherSuites(cipherSuites)
				.build();
	}
	@Override
	@Bean
	public CassandraClusterFactoryBean cluster() {
		CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
		cluster.setContactPoints(this.contactPoints);
		cluster.setPort(this.port);
		logger.info(sslEnabled);
		if (sslEnabled.equalsIgnoreCase("true")) {
			logger.info("inside if block");
			cluster.setSslEnabled(true);
			cluster.setSslOptions(createSslOptions());
		}
		cluster.setAuthProvider(getAuthProvider());
		cluster.setJmxReportingEnabled(false);
		return cluster;
    }
	@Override
	public String[] getEntityBasePackages() {
		return new String[] { "com.tsb.cb.model" };
	}
	@Override
	@Bean
	public CassandraSessionFactoryBean session() {
		CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
		session.setCluster(cluster().getObject());
		session.setKeyspaceName(this.getKeyspaceName());
		try {
			session.setConverter(converter());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		session.setSchemaAction(this.getSchemaAction());
		return session;
    }
	@Bean
	public CassandraConverter converter() throws ClassNotFoundException {
		return new MappingCassandraConverter(mappingContext());
    }
	@Bean
	public CassandraMappingContext mappingContext() throws ClassNotFoundException {
		CassandraMappingContext mappingContext = new CassandraMappingContext();
		mappingContext.setInitialEntitySet(getInitialEntitySet());
		return mappingContext;
    }
}