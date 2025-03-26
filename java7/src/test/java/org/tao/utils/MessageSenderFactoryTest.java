package org.tao.utils;

import org.junit.Assert;
import org.junit.Test;
import org.tao.utils.msg.DevelopmentMessageSender;
import org.tao.utils.msg.LocalMessageSender;
import org.tao.utils.msg.MessageSender;
import org.tao.utils.msg.ProductionMessageSender;

import static org.junit.Assert.*;

/**
 * 메세지 센더의 생성 및 테스트
 */
public class MessageSenderFactoryTest {

    @Test
    public void createMessageSender() {
        // given
        MessageSender sender = MessageSenderFactory.createMessageSender("PROD");

        // when
        try {
            sender.send("테스트 메시지");
        } catch (Exception e) {
            Assert.fail("메시지 전송 중 예외 발생: " + e.getMessage());
        }

        // then
        Assert.assertTrue(sender instanceof ProductionMessageSender);
    }


    @Test
    public void testDevelopmentMessageSender() {
        // given
        MessageSender sender = MessageSenderFactory.createMessageSender("DEV");

        // when
        try {
            sender.send("테스트 메시지");
        } catch (Exception e) {
            Assert.fail("메시지 전송 중 예외 발생: " + e.getMessage());
        }

        // then
        Assert.assertTrue(sender instanceof DevelopmentMessageSender);
    }

    @Test
    public void testLocalMessageSender() {
        // given
        MessageSender sender = MessageSenderFactory.createMessageSender("LOCAL");

        // when
        try {
            sender.send("테스트 메시지");
        } catch (Exception e) {
            Assert.fail("메시지 전송 중 예외 발생: " + e.getMessage());
        }

        // then
        Assert.assertTrue(sender instanceof LocalMessageSender);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEnvironment() {
        MessageSenderFactory.createMessageSender("INVALID");
    }

}