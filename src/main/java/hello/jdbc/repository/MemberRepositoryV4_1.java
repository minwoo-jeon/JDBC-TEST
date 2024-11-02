package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/*
*  예외 누수 문제 해결
* 체크 예외를 런터임 예외로 변경
* MemberRepository 인터페이스 사용
* throws SQLException 제거
* */
@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository{

    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource){
        this.dataSource = dataSource;
    }


    //맴버를 저장하는 메서드
    @Override
    public Member save(Member member){
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
           throw new MyDbException(e);
        }finally {
            close(con, pstmt, null);
        }
    }


    //맴버 조회
    @Override
    public Member findById(String memberId){
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
            throw new MyDbException(e);
        }finally {
            close(con,pstmt,rs);
        }
    }





    //회원 수정
    @Override
    public void update(String memberId , int money){
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
            throw new MyDbException(e);
        }finally {
            close(con, pstmt, null);
        }
    }




    //맴버 삭제
    @Override
    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
             pstmt.executeUpdate(); //쿼리를 실행하고 영향받은 row수를 반환
        } catch (SQLException e) {
            throw new MyDbException(e);
        }finally {
            close(con, pstmt, null);
        }
    }





    //커넥션 열결 메서드
    private Connection getConnection() throws SQLException {
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야한다
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("getConnection={}, class={}",con , con.getClass());
        return con;
    }


    //자원 해제
    private void close(Connection con, Statement stmt , ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //주의 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con,dataSource);
//        JdbcUtils.closeConnection(con);
    }
}
