package com.database.connection.detabaseconnection;

import dto.CustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class DetabaseconnectionApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DetabaseconnectionApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DetabaseconnectionApplication.class, args);
	}

	//makes it easy to work with SQL relational databases and JDBC. Most JDBC code is mired in resource
	// acquisition, connection management, exception handling, and general error checking that is wholly
	// unrelated to what the code is meant to achieve. The JdbcTemplate takes care of all of that for you
	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 *
	 * database connection manual process
	 * @param args
	 * @throws Exception
	 */

	@Override
	public void run(String... args) throws Exception {
		log.info("creating Tables");
		jdbcTemplate.execute("DROP TABLE CUSTOMERS IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE CUSTOMERS(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name -> name.split(" ")).collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

		// Uses JdbcTemplate's batchUpdate operation to bulk load data
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

		jdbcTemplate.query(
						"SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
						(rs, rowNum) -> new CustomerDTO(rs.getLong("id"), rs.getString("first_name"),
								rs.getString("last_name")), "Josh")
				.forEach(customer -> {
					log.info(String.valueOf(customer));
				});

	}
}
