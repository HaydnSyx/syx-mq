package cn.syx.mq.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyxBroker {

    Map<String, SyxMq<?>> mqMap = new ConcurrentHashMap<>(128);

    public <T> SyxMq<T> find(String topic) {
        return (SyxMq<T>) mqMap.get(topic);
    }

    public SyxMq<?> createTopic(String topic) {
        return mqMap.putIfAbsent(topic, new SyxMq<>(topic));
    }

    public SyxProducer createProducer() {
        return new SyxProducer(this);
    }

    public <T> SyxConsumer<T> createConsumer() {
        return new SyxConsumer<>(this);
    }
}
