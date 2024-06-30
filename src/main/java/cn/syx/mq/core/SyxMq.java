package cn.syx.mq.core;

import cn.syx.mq.model.SyxMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SyxMq<T> {

    private final String topic;
    private final LinkedBlockingQueue<SyxMessage<T>> queue = new LinkedBlockingQueue<>();
    private final List<SyxMqListener<T>> listeners = new ArrayList<>();

    public SyxMq(String topic) {
        this.topic = topic;
    }

    public boolean send(SyxMessage<T> msg) {
        boolean offer = queue.offer(msg);
        listeners.forEach(l -> l.onMessage(msg));
        return offer;
    }

    public SyxMessage<T> poll(long timeout) throws InterruptedException {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    public void listen(SyxMqListener<T> listen) {
        listeners.add(listen);
    }
}
