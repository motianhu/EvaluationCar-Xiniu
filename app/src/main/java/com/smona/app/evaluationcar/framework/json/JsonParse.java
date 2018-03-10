package com.smona.app.evaluationcar.framework.json;

import com.alibaba.fastjson.JSON;

/**
 * Created by motianhu on 3/23/17.
 */

public class JsonParse {
    public static <T> T parseJson(String content, Class<T> clazz) {
        return JSON.parseObject(content, clazz);
    }
}
