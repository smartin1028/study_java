package org.study.green.tibrv;

import java.util.concurrent.Flow;

public class TibrvSubscriber implements TibrvMsgCallback {
    public static void main(String[] args) {
        // TIBCO Rendezvous 초기화
        Tibrv.open(Tibrv.IMPL_NATIVE);

        // 전송 객체 생성
        TibrvTransport transport = new TibrvRvdTransport("7500");

        // 리스너 생성 및 구독 시작
        new TibrvListener(
                Tibrv.defaultQueue(),
                new TibrvSubscriber(),
                transport,
                "TEST.SUBJECT",
                null
        );

        // 메시지 대기
        while (true) {
            Tibrv.defaultQueue().dispatch();
        }

    }

    // 메시지 수신 콜백
    public void onMsg(TibrvListener listener, TibrvMsg msg) {
        System.out.println("Received message: " + msg.get("data"));
    }
}
