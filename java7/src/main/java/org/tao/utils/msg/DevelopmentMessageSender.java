package org.tao.utils.msg;

public class DevelopmentMessageSender implements MessageSender {
    @Override
    public void send(String message) {
        // 개발 환경 메시지 전송 로직
        // 예: 개발용 메시지 큐로 전송
        System.out.println("[DEV] Sending message: " + message);
    }
}
