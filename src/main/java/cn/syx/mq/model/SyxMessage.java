package cn.syx.mq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyxMessage<T> {

    private static AtomicLong aId = new AtomicLong(0);

    private long id;
    private T data;
    private Map<String, String> headers = new HashMap<>();

    public static SyxMessage<String> of(String data) {
        return of(data, new HashMap<>());
    }

    public static SyxMessage<String> of(String data, Map<String, String> headers) {
        return new SyxMessage<>(aId.getAndIncrement(), data, headers);
    }

    public void wrapperOffset(int offset) {
        this.headers.put("x-offset", offset + "");
    }

    public int offset() {
        return Integer.parseInt(headers.get("x-offset"));
    }
}
