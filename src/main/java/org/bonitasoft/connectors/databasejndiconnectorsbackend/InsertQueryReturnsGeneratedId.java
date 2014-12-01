package org.bonitasoft.connectors.databasejndiconnectorsbackend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Back-end class for a Bonita BPM connector to run a SQL query and return the primary key of the newly inserted row.
 * Database connection is obtained using a data source (get using JNDI lookup).<br>
 * Note that connector has been tested only on PostgreSQL.
 *
 * @author Antoine Mottier
 *
 */
public class InsertQueryReturnsGeneratedId {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(InsertQueryReturnsGeneratedId.class);

    /** JNDI of the data source to use to get a connection to run the SQL INSERT query. */
    private String jndiDataSourceName;

    /** Query input data: first name */
    private String firstName;

    /** Query input data: last name */
    private String lastName;

    /** Primary key of the newly inserted row. Query run in this example use integer for primary key. */
    private Integer insertedRowPrimarykey;

    public void setJndiDataSourceName(String jndiDataSourceName) {
	this.jndiDataSourceName = jndiDataSourceName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public Integer getInsertedRowPrimarykey() {
	return insertedRowPrimarykey;
    }

    /**
     * Run a SQL INSERT query and update the insertedRowPrimarykey attribute. Important: all setter (including
     * setJndiDataSourceName) need to be called before executeInsertQuery.
     *
     * @throws NamingException
     *             in case of error when looking up for data source
     * @throws SQLException
     *             in case of error: when we try to get the database connection, at preparation of SQL query, in the SQL
     *             query execution or when getting the primary key value
     */
    public void executeInsertQuery() throws NamingException, SQLException {

	LOGGER.debug("Trying to insert person: {} {}", firstName, lastName);

	// Lookup for data source
	Context initialContext = new InitialContext();
	DataSource datasource = (DataSource) initialContext.lookup(jndiDataSourceName);

	// Try to get a connection
	try (Connection connection = datasource.getConnection()) {

	    // Try to prepare the SQL query
	    try (PreparedStatement statement = connection.prepareStatement(
		    "INSERT INTO person (firstname, lastname) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);) {

		statement.setString(1, firstName);
		statement.setString(2, lastName);

		statement.executeUpdate();

		// Try to get the result set to
		try (ResultSet resultSet = statement.getGeneratedKeys();) {
		    if (resultSet.next()) {
			insertedRowPrimarykey = Integer.valueOf(resultSet.getInt(1));
		    } else {
			LOGGER.error("Insert query doesn't return any result (no primary key returned)");
		    }
		}
	    }
	}
    }
}
