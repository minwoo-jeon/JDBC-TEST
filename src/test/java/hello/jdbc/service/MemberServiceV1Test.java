package hello.jdbc.service;


import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_C = "ex";

    private  MemberRepositoryV2 memberRepository;
    private MemberServiceV1 memberService;

    @BeforeEach
    void before(){
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);

         memberRepository = new MemberRepositoryV2(dataSource);
         memberService = new MemberServiceV1(memberRepository);
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
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        Member findMember = memberRepository.save(memberA);
        Member toMember = memberRepository.save(memberB);

        //when
        memberService.accountTransfer(findMember.getMemberId(),toMember.getMemberId(),2000);


        //then
        Member member1 = memberRepository.findById(findMember.getMemberId());
        Member member2 = memberRepository.findById(toMember.getMemberId());
        assertThat(member1.getMoney()).isEqualTo(8000);
        assertThat(member2.getMoney()).isEqualTo(12000);
    }


    @Test
    @DisplayName("이체중 예외 발생")
    public void accountTransferEx()throws SQLException{

        //given - 다음 데이터를 저장해서 테스트를 준비한다
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberC = new Member(MEMBER_C, 10000);
        Member member1 = memberRepository.save(memberA);
        Member member2 = memberRepository.save(memberC);

        //when 계좌이체 로직을 실행한다
        assertThatThrownBy(()->
        memberService.accountTransfer(member1.getMemberId(),member2.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);


        //then: 계좌이체 실패
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberC.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);

    }
}
