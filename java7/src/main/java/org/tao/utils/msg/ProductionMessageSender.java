package org.tao.utils.msg;

public class ProductionMessageSender implements MessageSender {
    @Override
    public void send(String message) {
        // 운영 환경 메시지 전송 로직
        // 예: 실제 메시지 큐 서버로 전송
        System.out.println("[PROD] Sending message: " + message);
    }
}