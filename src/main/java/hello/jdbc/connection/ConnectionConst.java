package hello.jdbc.connection;

public abstract class ConnectionConst {


    //데이터베이스에 접속하는데 필요한 기본 정보를 편리하게 사용할수 있도록 상수로만듬 ( 디비 설정파일)?
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
