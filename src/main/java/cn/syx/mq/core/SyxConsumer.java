package cn.syx.mq.core;

import cn.syx.mq.model.SyxMessage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class SyxConsumer<T> {

    private String id;
    private final SyxBroker broker;
//    private String topic;
    @Getter
    private SyxMqListener listener;
    private static AtomicLong aId = new AtomicLong(0);


    public SyxConsumer(SyxBroker broker) {
        this.broker = broker;
        this.id = "CID" + aId.getAndIncrement();
    }

    public void subscribe(String topic) {
//        this.topic = topic;
        broker.sub(topic, id);
    }

    public void unsubscribe(String topic) {
        broker.unsub(topic, id);
    }

    public SyxMessage<T> recv(String topic) {
        return broker.recv(topic, id);
    }

    public boolean ack(String topic, SyxMessage<?> msg) {
        return broker.ack(topic, id, msg.offset());
    }

    public void listen(String topic, SyxMqListener listener) {
        this.listener = listener;
        broker.addConsumer(topic, this);
    }
}
