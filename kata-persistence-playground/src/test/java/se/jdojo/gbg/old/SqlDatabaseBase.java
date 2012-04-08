package se.jdojo.gbg.old;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

public class SqlDatabaseBase {

	protected DataSource dataSource;
	// keeping a connection is an easy way of controlling db life cycle 
	private Connection connection;

	protected void initializeDatabase(String sqlInitFile) throws ClassNotFoundException, SQLException, IOException {
		Class.forName("org.h2.Driver");
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:mem:test"); 
		ds.setUser("sa");
		ds.setPassword("sa");
		
		connection = ds.getConnection();
		Statement statement = connection.createStatement();
		String initialization = loadFileContentAsString(findTestResource(sqlInitFile));
		statement.execute(initialization);
		statement.close();
		dataSource = ds;
	}
	
	protected void tearDownDatabase() throws Exception {
		connection.close();
	}

	private File findTestResource(String string) {
		URL resource = this.getClass().getClassLoader().getResource(string);
		return new File(resource.getPath());
	}

	private String loadFileContentAsString(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		byte[] b = new byte[(int) file.length()];
		int len = b.length;
		int total = 0;

		while (total < len) {
			int result = in.read(b, total, len - total);
			if (result == -1) {
				break;
			}
			total += result;
		}

		return new String(b, Charset.forName("UTF-8"));
	}

}
