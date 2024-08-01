package com.cruise.parkinglotto.service.redisService;

import java.time.Duration;
import java.util.Map;

public interface RedisService {

    // 만료기간이 없이 저장하는 메서드
    void setValues(String key, String data);

    // 만료기간과 함께 저장하는 메서드
    void setValues(String key, String data, Duration duration);

    // 키 값으로 값을 불러오는 메서드
    String getValues(String key);

    // 키 값으로 키-값을 삭제하는 메서드
    void deleteValues(String key);

    // 해당하는 값이 있는지 체크하는 메서드
    boolean checkExistsValue(String value);

    // 블랙리스트를 만드는 메서드
    void setBlackList(String key, String data);

    // 블랙리스트를 가져오는 메서드
    String getBlackList(String key);

    // 블랙리스트를 삭제하는 메서드
    boolean deleteBlackList(String key);

    // 블랙리스트에 해당 키 값이 있는지 체크하는 메서드
    boolean hasKeyBlackList(String key);
}
