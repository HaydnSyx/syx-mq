package cn.syx.mq.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer {

    private static final MultiValueMap<String, Entry> indexMap = new LinkedMultiValueMap<>();
    private static final Map<String, Map<Integer, Entry>> mappings = new HashMap<>();

    @Data
    @AllArgsConstructor
    public static class Entry {

        //        private long id;
        private int offset;
        private int length;
    }

    public static void addEntry(String topic, int offset, int length) {
        System.out.println("addEntry: " + topic + " " + offset + " " + length);
        Entry entity = new Entry(offset, length);
        indexMap.add(topic, entity);
        mappings.computeIfAbsent(topic, k -> new HashMap<>()).put(offset, entity);
    }

    public static List<Entry> getEntries(String topic) {
        return indexMap.get(topic);
    }

    public static Entry getEntry(String topic, int offset) {
        Map<Integer, Entry> map = mappings.get(topic);
        return map == null ? null : map.get(offset);
    }
}
