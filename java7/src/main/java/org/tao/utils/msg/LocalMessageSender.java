package org.tao.utils.msg;

public class LocalMessageSender implements MessageSender {
    @Override
    public void send(String message) {
        // 로컬 환경 메시지 전송 로직
        // 예: 로그로만 출력
        System.out.println("[LOCAL] Message logged: " + message);
    }
}
