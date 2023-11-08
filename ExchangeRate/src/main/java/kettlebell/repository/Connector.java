package kettlebell.repository;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {

	public Connection getConnection() throws SQLException {
		URL resource = Connector.class.getClassLoader().getResource("db/speculator.db");
		String path = "";
		try {
			path = new File(resource.toURI()).getAbsolutePath();
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 

		Connection connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", path));
		return connection;

	}

}
