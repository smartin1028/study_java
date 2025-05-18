package org.example.db;


public class DBConnectionVo {
    private String ip = "localhost";
    private int port = 1521;
    private String username = "username";
    private String password = "password";
    private String etc1 = ":XE";


    public DBConnectionVo() {
        this.ip = System.getProperty("db_ip", "localhost");
        this.port = Integer.parseInt(System.getProperty("db_port", "1521"));
        this.username = System.getProperty("db_id", "id");
        this.password = System.getProperty("db_password", "password");

        System.out.println(this);
    }

    /**
     * url 생성 함수
     * @param dbConnectionVo 기본 db 연결 정보
     * @param dbType 연결하는 db
     * @return jdbc에서 사용하는 url 문자열
     */
    public static String getJdbcUrl(DBConnectionVo dbConnectionVo, DBType dbType) {
        String jdbcUrl = dbType.getJdbcUrl();
        String format = String.format(jdbcUrl, dbConnectionVo.getIp(), dbConnectionVo.getPort());
        if(dbConnectionVo.getEtc1() != null) {
            format = format + dbConnectionVo.getEtc1();
        }
        System.out.println("@@@@@ jdbcurl = " + format);
        System.out.println("@@@@@ username = " + dbConnectionVo.getUsername());
        System.out.println("@@@@@ ip = " + dbConnectionVo.getIp());
        System.out.println("@@@@@ port = " + dbConnectionVo.getPort());
        return format;
    }

    @Override
    public String toString() {
        return "DBConnectionVo{" +
                "username='" + username + '\'' +
                ", port=" + port +
                ", ip='" + ip + '\'' +
                ", etc1='" + etc1 + '\'' +
                '}';
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEtc1() {
        return etc1;
    }

    public void setEtc1(String etc1) {
        this.etc1 = etc1;
    }

}
