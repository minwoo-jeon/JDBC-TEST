package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;
import java.util.Objects;

/*
*  JdbcTemplate 사용
* */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository{

    private final JdbcTemplate template;


    public MemberRepositoryV5(DataSource dataSource){
      this.template = new JdbcTemplate(dataSource);
    }


    //맴버를 저장하는 메서드
    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id , money) values(?,?)";
        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }


    //맴버 조회
    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        Member member = template.queryForObject(sql, memberRowMapper(), memberId);
        return member;
    }
        private RowMapper<Member> memberRowMapper(){
        return (rs,rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }





    //회원 수정
    @Override
    public void update(String memberId , int money){
        String sql = "update member set money =? where member_id =?";
        template.update(sql,money,memberId);
    }




    //맴버 삭제
    @Override
    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";
        template.update(sql, memberId);
    }

}
