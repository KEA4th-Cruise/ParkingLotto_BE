package com.cruise.parkinglotto.service.redisService;

import java.time.Duration;

public interface RedisService {

    void setValues(String key, String data);

    void setValues(String key, String data, Duration duration);

    String getValues(String key);

    void deleteValues(String key);

    String getHashOps(String key, String hashKey);

    void deleteHashOps(String key, String hashKey);

    boolean checkExistsValue(String value);
}
