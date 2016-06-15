package ar.edu.utn.frro.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile(Constants.SPRING_PROFILE_HEROKU)
public class HerokuDatabaseConfiguration {

	private final Logger log = LoggerFactory
			.getLogger(HerokuDatabaseConfiguration.class);

	@Bean
	public DataSource dataSource(DataSourceProperties dataSourceProperties,
			JHipsterProperties jHipsterProperties) throws URISyntaxException {
		log.debug("Configuring Heroku Datasource");

		String herokuDatabaseUrl = System.getenv("DATABASE_URL");
		if (herokuDatabaseUrl != null) {
			HikariConfig config = new HikariConfig();
			java.net.URI databaseURI = new URI(herokuDatabaseUrl);
			String jdbcDatabaseURL = "jdbc:postgresql://"
					+ databaseURI.getHost() + ':' + databaseURI.getPort()
					+ databaseURI.getPath();
			String username = databaseURI.getUserInfo().split(":")[0];
			String password = databaseURI.getUserInfo().split(":")[1];

			config.setDataSourceClassName(dataSourceProperties
					.getDriverClassName());
			config.addDataSourceProperty("url", jdbcDatabaseURL);
			config.addDataSourceProperty("user", username);
			config.addDataSourceProperty("password", password);

			return new HikariDataSource(config);
		} else {
			throw new ApplicationContextException(
					"Heroku database URL is not configured, you must set $DATABASE_URL");
		}
	}
}
