package hello.jdbc.service;


/**
* 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
* */
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor //생성자 자동 주입 final 이 붙거나 @NotNULL 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DriverManagerDataSource dataSource;

    //계좌 이체
    public void accountTransfer(String fromId , String toId , int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false); //트랜잭션 시작
            //비지니스 로직
            bizLogic(con,fromId,toId,money);
            con.commit();
        }catch (Exception e){
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            release(con); //커넥션 종료 되는게 아니라 풀에 반납
        }
    }


    private void bizLogic(Connection con , String fromId , String toId , int money)throws SQLException{
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con,fromId,fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con,toId,toMember.getMoney()+money);
    }


    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void release(Connection con){
        if (con != null) {
            try{
                con.setAutoCommit(true); //현재 수동 커밋모드로 동작하기떄문에 다시 자동커밋으로 변경해주는것이 안전
                con.close();
            } catch (Exception e) {
                log.info("error" , e);
            }
        }
    }
}
