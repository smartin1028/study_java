package org.tao.utils;

import org.tao.utils.msg.DevelopmentMessageSender;
import org.tao.utils.msg.LocalMessageSender;
import org.tao.utils.msg.MessageSender;
import org.tao.utils.msg.ProductionMessageSender;

/**
 * 환경 변수에 따라서 메세지 보낼 객체 생성
 */
public class MessageSenderFactory {
    private MessageSenderFactory() {
        System.out.println("외부에서 생성하지 않고 사용하세요");
    }

    public static MessageSender createMessageSender(String environment) {
        switch (environment.toUpperCase()) {
            case "PROD":
                return new ProductionMessageSender();
            case "DEV":
                return new DevelopmentMessageSender();
            case "LOCAL":
                return new LocalMessageSender();
            default:
                throw new IllegalArgumentException("유효하지 않은 환경입니다: " + environment);
        }
    }
}
