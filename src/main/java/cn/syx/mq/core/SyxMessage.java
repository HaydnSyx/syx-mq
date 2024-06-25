package cn.syx.mq.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyxMessage<T> {

    private long id;
    private T data;
    private Map<String, String> headers;
}
