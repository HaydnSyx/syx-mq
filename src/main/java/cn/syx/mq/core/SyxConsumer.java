package cn.syx.mq.core;

import cn.syx.mq.model.SyxMessage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class SyxConsumer<T> {

    private String id;
    private final SyxBroker broker;
    private String topic;
    private SyxMq<T> mq;

    private static AtomicLong aId = new AtomicLong(0);

    public SyxConsumer(SyxBroker broker) {
        this.broker = broker;
        this.id = "CID" + aId.getAndIncrement();
    }

    public void subscribe(String topic) {
        this.topic = topic;
        mq = broker.find(topic);
        if (Objects.isNull(mq)) {
            throw new RuntimeException("topic not found");
        }
    }

    public SyxMessage<T> poll(long timeout) {
        try {
            return mq.poll(timeout);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void listen(SyxMqListener<T> listener) {
        mq.listen(listener);
    }
}
