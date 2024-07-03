package cn.syx.mq.demo;

import cn.syx.mq.core.SyxBroker;
import cn.syx.mq.core.SyxConsumer;
import cn.syx.mq.model.SyxMessage;
import cn.syx.mq.core.SyxProducer;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class SyxMqDemo {

    @SneakyThrows
    public static void main(String[] args) {

        long ids = 0;

        String topic = "cn.syx.test";
        SyxBroker broker = SyxBroker.getDefault();
//        broker.createTopic(topic);

        SyxProducer producer = broker.createProducer();
        SyxConsumer<Order> consumer = broker.createConsumer();
        consumer.subscribe(topic);
        consumer.listen(topic, message -> log.warn("onMessage => {}", JSON.toJSONString(message)));

//        Order order = new Order(ids, "item" + ids, 100);
//        producer.send(topic, new SyxMessage<>(123, order, null));

        for (int i = 0; i < 10; i++) {
            Order order = new Order(ids, "item" + ids, 100 * ids);
            producer.send(topic, new SyxMessage<>(ids++, order, null));
        }

//        for (int i = 0; i < 10; i++) {
//            SyxMessage<Order> message = consumer.recv(topic);
//            System.out.println(message);
//            if (Objects.nonNull(message)) {
//                consumer.ack(topic, message);
//            }
//        }

        while (true) {
            char c = (char) System.in.read();
            if (c == 'q' || c == 'e') {
                consumer.unsubscribe(topic);
                break;
            }
            if (c == 'p') {
                Order order = new Order(ids, "item" + ids, 100 * ids);
                producer.send(topic, new SyxMessage<>(ids++, order, null));
                System.out.println("send ok => " + order);
            }
            if (c == 'c') {
                SyxMessage<Order> message = consumer.recv(topic);
                System.out.println("poll ok => " + message);
                if (Objects.nonNull(message)) {
                    consumer.ack(topic, message);
                }
            }
            if (c == 'a') {
                for (int i = 0; i < 10; i++) {
                    Order order = new Order(ids, "item" + ids, 100 * ids);
                    producer.send(topic, new SyxMessage<>(ids++, order, null));
                }
                System.out.println("send 10 orders...");
            }
        }
    }
}
