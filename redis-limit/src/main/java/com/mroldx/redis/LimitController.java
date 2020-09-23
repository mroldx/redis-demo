package com.mroldx.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author: moli
 * @Email: 974751082@qq.com
 * @qq: 974751082
 * @Date: 2020/9/23 21:50
 */
@RequiredArgsConstructor
@RestController
public class LimitController {

    private final StringRedisTemplate redisTemplate;


    @ResponseBody
    @RequestMapping("/limitout")
    public CommonResult get_1oo() {

        if (redisTemplate.hasKey("limit")) {
            int limit = Integer.parseInt(redisTemplate.opsForValue().get("limit").toString());
            long forExpire = redisTemplate.getExpire("limit");
            if (forExpire > -1) {
                if (limit >= 50) {
                    return CommonResult.error("一分钟之内超过50此访问");
                } else {
                    increValue("limit");
                    return CommonResult.ok("访问成功");
                }
            } else {
                redisTemplate.delete("limit");
            }
        }
        redisTemplate.opsForValue().set("limit", "0", 60, TimeUnit.SECONDS);
        return CommonResult.ok("访问成功");
    }

    public void increValue(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        try {
            valueOperations.increment(key, 1);
            System.out.println("一分钟之内访问人数=" + valueOperations.get(key));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
