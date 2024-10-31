package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


@Slf4j
public class CheckedTest {


    @Test
    public void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw()throws MyCheckedException{
        Service service = new Service();
        service.callThrow();
        Assertions.assertThatThrownBy(()->service.callThrow())
                .isInstanceOf(MyCheckedException.class);

    }


    //Exception 을 상소받은 예외는 체크 예외가 된다
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }


    static class Service {
        Repository repository = new Repository();


        //예외를 잡아서 처리하는 코드

        public void callCatch() {
            try {
                repository.call(); //MyCheckedExceptin 예외 객체 생성됌
            } catch (MyCheckedException e) {
                log.info("예외 처리, message={}", e.getMessage(), e); //로그에 마지막 예외 객체를 전달해주면 해당 예외의 스택트레이스를 추가로 출력해줌
            }
        }

        public void callThrow() throws MyCheckedException{
            repository.call();
        }

        static class Repository {
            public void call() throws MyCheckedException {
                throw new MyCheckedException("예외발생!");
            }
        }
    }
}