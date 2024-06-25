package cn.syx.mq.core;

import java.util.Objects;

public class SyxProducer {

    private final SyxBroker broker;

    public SyxProducer(SyxBroker broker) {
        this.broker = broker;
    }

    public <T> boolean send(String topic, SyxMessage<T> msg) {
        // 通过topic查找mq队列
        SyxMq<T> mq = broker.find(topic);
        // 不存在则报错
        if (Objects.isNull(mq)) {
            throw new RuntimeException("topic not found");
        }
        // 发送
        return mq.send(msg);
    }
}
