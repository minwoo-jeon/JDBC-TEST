package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={} , class={}", con1, con1.getClass());
        log.info("connection={} , class={}", con2, con2.getClass());
    }


    @Test
        //스프링이 제공하는 DataSource 가 적용된 DriverManager 인 DriverManagerDataSource
      void dataSourceDriverManager() throws SQLException{
            DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }


     private void useDataSource(DataSource dataSource)throws SQLException{
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={} , class={}", con1, con1.getClass());
        log.info("connection={} , class={}", con2, con2.getClass());
    }

    @Test
    //데이터소스를 통해 커넥션 풀을 사용하는 예제
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //커넥션 풀링: HikariProxyConnection -> JdbcConnection(Target)
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");
        useDataSource(dataSource);
        useDataSource(dataSource);
        Thread.sleep(1000); //커넥션 풀에서 커넥션 생성 대기 시간
    }

}
