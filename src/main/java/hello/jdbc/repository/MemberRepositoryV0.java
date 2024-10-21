package hello.jdbc.repository;


import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/*
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public void update(String member , int money)throws Exception{
        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection(); //DB와 연결하고 h2데이터베이스 커넥션 객체를 반환받는다
            pstmt = con.prepareStatement(sql); // SQL쿼리를 넘겨주고, 해당 쿼리를 관리할 PreparedStatement 객체를 받아온다/
            pstmt.setInt(1, money); // 첫번쨰 ? 에 파라미터를 동적으로 바인딩해준다
            pstmt.setString(2,member);
            int resultSize = pstmt.executeUpdate(); //바인딩된 sql쿼리를  요청
            log.info("resultSize={}" , resultSize);
        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }


    public Member save(Member member)throws Exception{
        String sql = "insert into member(member_id , money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate();
            return member;
        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }

    public void delete(String memberId) throws Exception{
        String sql = "delete from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }

    private  void close(Connection con , Statement stmt, ResultSet rs){
        if(rs != null){
            try{
                rs.close();
            }catch (SQLException e) {
                log.info("error",e);
            }
        }

        if(stmt != null){
           try{
            stmt.close();
        }catch (SQLException e) {
                log.info("error",e);
           }
        }

        if(con != null){
            try{
                con.close();
            }catch (SQLException e) {
                log.info("error",e);
            }
        }

    }

    public Member findById(String memberId) throws  Exception{
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if(rs.next()){
                Member member= new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId =" + memberId);
            }

        }catch (SQLException e){
            log.error("db error" , e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }
    }



    private Connection getConnection(){
        return DBConnectionUtil.getConnection();
    }
}
