package cn.syx.mq.core;

import cn.kimmking.utils.HttpUtils;
import cn.kimmking.utils.ThreadUtils;
import cn.syx.mq.model.Result;
import cn.syx.mq.model.SyxMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@Slf4j
public class SyxBroker {

    @Getter
    private static final SyxBroker Default = new SyxBroker();
    @Getter
    private final MultiValueMap<String, SyxConsumer<?>> consumers = new LinkedMultiValueMap<>();

    private static final String URL = "http://localhost:8765/syxmq";


    static {
        init();
    }

    public static void init() {
        ThreadUtils.getDefault().init(1);
        ThreadUtils.getDefault().schedule(() -> {
            MultiValueMap<String, SyxConsumer<?>> consumers = getDefault().getConsumers();
            consumers.forEach((topic, consumer) -> {
                consumer.forEach(con -> {
                    // 没有设置监听器则不对该consumer处理
                    SyxMqListener listener = con.getListener();
                    if (Objects.isNull(listener)) {
                        return;
                    }

                    // 未拉取到消息则忽略
                    SyxMessage<?> message = con.recv(topic);
                    if (Objects.isNull(message)) {
                        return;
                    }

                    try {
                        // 处理消息
                        listener.onMessage(message);
                        con.ack(topic, message);
                    } catch (Exception ex) {
                        // TODO
                    }
                });
            });

        }, 100, 100);
    }


    public SyxProducer createProducer() {
        return new SyxProducer(this);
    }

    public <T> SyxConsumer<T> createConsumer() {
        return new SyxConsumer<>(this);
    }

    public void addConsumer(String topic, SyxConsumer<?> consumer) {
        consumers.add(topic, consumer);
    }

    public void sub(String topic, String id) {
        log.info("===> [start] subscribe topic:{}, id:{}", topic, id);
        Result<String> re = HttpUtils.httpGet(URL + "/sub?t=" + topic + "&cid=" + id,
                new TypeReference<Result<String>>() {
                });
        log.info("===> [end] subscribe topic:{}, id:{}, re:{}", topic, id, JSON.toJSONString(re));
    }

    public void unsub(String topic, String id) {
        log.info("===> [start] unsubscribe topic:{}, id:{}", topic, id);
        Result<String> re = HttpUtils.httpGet(URL + "/unsub?t=" + topic + "&cid=" + id,
                new TypeReference<Result<String>>() {
                });
        log.info("===> [end] unsubscribe topic:{}, id:{}, re:{}", topic, id, JSON.toJSONString(re));
    }

    public boolean send(String topic, SyxMessage<?> msg) {
        log.info("===> [start] send message to topic:{}, msg:{}", topic, JSON.toJSONString(msg));
        Result<String> re = HttpUtils.httpPost(JSON.toJSONString(msg), URL + "/send?t=" + topic,
                new TypeReference<>() {
                });
        log.info("===> [end] send message to topic:{}, re: {}", topic, JSON.toJSONString(re));
        return re.getCode() == 1;
    }

    public <T> SyxMessage<T> recv(String topic, String id) {
        log.info("===> [start] recv message to topic:{}", topic);
        Result<SyxMessage<T>> re = HttpUtils.httpGet(URL + "/recv?t=" + topic + "&cid=" + id,
                new TypeReference<Result<SyxMessage<T>>>() {
                });
        log.info("===> [end] recv message to topic:{}, re: {}", topic, JSON.toJSONString(re));
        return re.getData();
    }

    public boolean ack(String topic, String id, int offset) {
        log.info("===> [start] ack message to topic:{}, id:{}, offset:{}", topic, id, offset);
        Result<String> re = HttpUtils.httpGet(URL + "/ack?t=" + topic + "&cid=" + id + "&offset=" + offset,
                new TypeReference<Result<String>>() {
                });
        log.info("===> [end] ack message to topic:{}, id:{}, offset:{}, re: {}", topic, id, offset, JSON.toJSONString(re));
        return re.getCode() == 1;
    }


}
