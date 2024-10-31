package hello.jdbc.service;


/**
* 트랜잭션 - 트랜잭션 매니저
* */
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor //생성자 자동 주입 final 이 붙거나 @NotNULL 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
public class MemberServiceV3_1 {

    private final MemberRepositoryV3 memberRepository;
    private final PlatformTransactionManager transactionManager;
//    private final DriverManagerDataSource dataSource;

    //계좌 이체
    public void accountTransfer(String fromId , String toId , int money) throws SQLException {
        //트랜잭션 시작
        TransactionStatus stauts = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            //비지니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(stauts);
        } catch (Exception e) {
            transactionManager.rollback(stauts); //실패시 롤백
            throw new IllegalStateException(e);
        }
    }


    private void bizLogic( String fromId , String toId , int money)throws SQLException{
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById( toId);

        memberRepository.update(fromId,fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId,toMember.getMoney()+money);
    }


    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
