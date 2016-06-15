package ar.edu.utn.frro.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

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
			java.net.URI databaseURI = new URI(herokuDatabaseUrl);
			String jdbcDatabaseURL = "jdbc:postgresql://" + databaseURI.getHost() + ':' + databaseURI.getPort() + databaseURI.getPath();
			log.info("Web application on Heroku, using jdbc database url: {}", jdbcDatabaseURL);
			HikariConfig config = new HikariConfig();
			config.setDataSourceClassName(dataSourceProperties
					.getDriverClassName());
			config.addDataSourceProperty("url", jdbcDatabaseURL);
			return new HikariDataSource(config);
		} else {
			throw new ApplicationContextException(
					"Heroku database URL is not configured, you must set $DATABASE_URL");
		}
	}
}
