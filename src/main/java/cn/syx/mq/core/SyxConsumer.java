package cn.syx.mq.core;

import java.util.Objects;

public class SyxConsumer<T> {

    private final SyxBroker broker;
    private String topic;
    private SyxMq<T> mq;

    public SyxConsumer(SyxBroker broker) {
        this.broker = broker;
    }

    public void subscribe(String topic) {
        this.topic = topic;
        mq = (SyxMq<T>) broker.find(topic);
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
