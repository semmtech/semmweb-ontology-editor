package com.semmtech.plugin.semmweb.core;


import java.util.Map;

import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.preferences.PreferencesUtil;


public class PreferenceUtilTest {
    public static void main(String[] args) {
        Map<String, String> input = Maps.newLinkedHashMap();
        input.put("A", "a");
        input.put("One", "een");
        input.put("http://www.google.com/test", "file:D:/test.ttl");
        input.put("temp", "temp");

        for (String key : input.keySet()) {
            String value = input.get(key);
            System.out.println(String.format("[%s] = %s", key, value));
        }

        String encoded = PreferencesUtil.encodeMap(input);
        System.out.println(encoded);

        Map<String, String> decoded = PreferencesUtil.decodeMap(encoded);
        for (String key : decoded.keySet()) {
            String value = decoded.get(key);
            System.out.println(String.format("[%s] = %s", key, value));
        }
    }
}
