package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//트랜잭션 - 트랜잭션 메니져

public class MemberServiceV3_1Test {


    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);
         memberRepository = new MemberRepositoryV3(dataSource);

         PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
         memberService = new MemberServiceV3_1(memberRepository,transactionManager);
    }

    @AfterEach
    void after() throws SQLException{
        memberRepository.delete("memberA");
        memberRepository.delete("memberB");
        memberRepository.delete("ex");
    }

    @Test
    @DisplayName("계좌 이체")
    public void accountTransfer() throws SQLException {

        //given
        Member memberA = new Member("memberA", 10000);
        Member memberB = new Member("memberB", 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);


        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }


    @Test
    @DisplayName("이체중 예외 발생")
    public void accountTransferEx()throws SQLException{

        //given - 다음 데이터를 저장해서 테스트를 준비한다
        Member memberA = new Member("memberA", 10000);
        Member memberC = new Member("ex", 10000);
        Member member1 = memberRepository.save(memberA);
        Member member2 = memberRepository.save(memberC);

        //when 계좌이체 로직을 실행한다
        assertThatThrownBy(()->
        memberService.accountTransfer(member1.getMemberId(),member2.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);


        //then: 계좌이체 실패
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberC.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);

    }
}
