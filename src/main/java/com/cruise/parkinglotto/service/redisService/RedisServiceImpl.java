package com.cruise.parkinglotto.service.redisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> redisBlackListTemplate;

    // 일반 저장소에 값 저장
    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    // 일반 저장소에서 값 가져오기
    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        return (String) values.get(key); // 값이 없으면 null 반환
    }

    // 일반 저장소에서 값 삭제
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    // 값이 존재하는지 확인
    public boolean checkExistsValue(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 블랙리스트에 값 저장
    public void setBlackList(String key, String data) {
        ValueOperations<String, Object> values = redisBlackListTemplate.opsForValue();
        values.set(key, data);
    }

    // 블랙리스트에서 값 가져오기
    public String getBlackList(String key) {
        ValueOperations<String, Object> values = redisBlackListTemplate.opsForValue();
        return (String) values.get(key); // 값이 없으면 null 반환
    }

    // 블랙리스트에서 값 삭제
    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.delete(key));
    }

    // 블랙리스트에 값이 존재하는지 확인
    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }
}