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
		String jdbcDatabaseUrl = System.getenv("JDBC_DATABASE_URL");

		if (jdbcDatabaseUrl != null) {
			HikariConfig config = new HikariConfig();
			String username = System.getenv("JDBC_DATABASE_USERNAME");
			String password = System.getenv("JDBC_DATABASE_PASSWORD");

			config.setDataSourceClassName(dataSourceProperties
					.getDriverClassName());
			config.addDataSourceProperty("url", jdbcDatabaseUrl);
			config.addDataSourceProperty("user", username);
			config.addDataSourceProperty("password", password);

			return new HikariDataSource(config);
		} else {
			throw new ApplicationContextException(
					"Heroku database URL is not configured, you must set $DATABASE_URL");
		}
	}
}
