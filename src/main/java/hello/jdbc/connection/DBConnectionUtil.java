package hello.jdbc.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import hello.jdbc.connection.ConnectionConst;
import lombok.extern.slf4j.Slf4j;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection(){
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); //디비에 연결하려면 이 메서드를 호출하면된다.
            //이렇게 하면 해당 라이브러리에 등록되있는 드라이버를 찾아서 커넥션을 제공해준다.
            log.info("get Connection ={} , class={}",connection,connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
