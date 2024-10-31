package hello.jdbc.service;


/**
* 트랜잭션 - 트랜잭션 탬플릿
* */
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j

public class MemberServiceV3_2 {

    private final MemberRepositoryV3 memberRepository;
    private final TransactionTemplate txTemplate;

    public MemberServiceV3_2(MemberRepositoryV3 memberRepository, PlatformTransactionManager transactionManager){
        this.memberRepository = memberRepository;
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

//    private final PlatformTransactionManager transactionManager;
//    private final DriverManagerDataSource dataSource;

    //계좌 이체
    public void accountTransfer(String fromId , String toId , int money) throws SQLException {

        txTemplate.executeWithoutResult((status)-> {
          try {
              bizLogic(fromId,toId,money);
          }catch (SQLException e){
              throw new IllegalStateException(e);
          }
        });
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