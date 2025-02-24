package org.study.green.tibrv;

public class TibrvPublisher {
    public static void main(String[] args) throws TibrvException {
        // TIBCO Rendezvous 초기화
        Tibrv.open(Tibrv.IMPL_NATIVE);

        // 전송 객체 생성
        TibrvTransport transport = new TibrvRvdTransport("7500");

        // 메시지 생성
        TibrvMsg msg = new TibrvMsg();
        msg.add("data", "Hello TIBCO Rendezvous!");

        // 메시지 발행
        transport.send("TEST.SUBJECT", msg);

        // 종료
        Tibrv.close();

    }
}
