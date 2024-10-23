package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource){
        this.dataSource = dataSource;
    }


    //맴버를 저장하는 메서드
    public Member save(Member member)throws SQLException{
        String sql = "insert into member(member_id , money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);  //데이터베이스에 전달할 sql과 파라미터로 전달할 데이터들을 준비한다.
            pstmt.setString(1, member.getMemberId()); //sql 첫번쨰 ? 에 값을 지정
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // Statment를 통해 준비된 sql 커넥션을 통해 실제 데이터베이스에 전달한다
            return member;
        } catch (SQLException e) {
            log.info("db error" , e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }


    //맴버 조회
    public Member findById(String memberId)throws SQLException{
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 데이터베이스와 커넥션연결한다
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery(); // executeQuery()는 결과를 ResultSet에 담아서 반환한다

            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        }catch (SQLException e){
            log.error("db error", e);
            throw  e;
        }finally {
            close(con,pstmt,rs);
        }
    }


    //회원 수정
    public void update(String memberId , int money)throws SQLException {
        String sql = "update member set money =? where member_id =?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); //쿼리를 실행하고 영향받은 row수를 반환
            log.info("resultSize={}",resultSize);

        } catch (SQLException e) {
            log.info("error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }


    //맴버 삭제
    public void delete(String memberId)throws SQLException{
        String sql = "delete from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
             pstmt.executeUpdate(); //쿼리를 실행하고 영향받은 row수를 반환
        } catch (SQLException e) {
            log.info("error", e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }





    //커넥션 열결 메서드
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("getConnection={}, class={}",con , con.getClass());
        return con;
    }


    //자원 해제
    private void close(Connection con, Statement stmt , ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
}
