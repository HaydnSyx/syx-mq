package cn.syx.mq.core;

import cn.syx.mq.model.SyxMessage;

import java.util.Objects;

public class SyxProducer {

    private final SyxBroker broker;

    public SyxProducer(SyxBroker broker) {
        this.broker = broker;
    }

    public boolean send(String topic, SyxMessage<?> msg) {
        // 发送
        return broker.send(topic, msg);
    }
}
