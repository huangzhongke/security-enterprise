/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.common.redis.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;


/**
 * Redis序列化
 *
 * @author Mark sunlightcs@gmail.com
 */
public class JsonRedisSerializer<T> implements RedisSerializer<T> {
    private static ParserConfig defaultRedisConfig = new ParserConfig();
    static {
        defaultRedisConfig.setAutoTypeSupport(true);
    }

    private Class<T> type;

    public JsonRedisSerializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(IOUtils.UTF8);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, IOUtils.UTF8);

        return JSON.parseObject(str, type, defaultRedisConfig);
    }
}