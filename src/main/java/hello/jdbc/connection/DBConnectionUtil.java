package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); //커넥션 얻어오는 작업
            log.info("get connection = {} , class={}", connection,connection.getClass() );
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

    }
}
