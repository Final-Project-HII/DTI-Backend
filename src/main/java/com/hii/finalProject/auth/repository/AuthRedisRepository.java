package com.hii.finalProject.auth.repository;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class AuthRedisRepository {
    private static final String STRING_KEY_PREFIX = "HiiMart:jwt:strings:" ;
    private static final String STRING_BLACKLIST_KEY_PREFIX = "HiiMart:blacklist-jwt:strings:" ;

    private static final String STRING_LINK_VERIFICATION_KEY_PREFIX = "HiiMart:link:strings:";

    private static final String STRING_LINK_RESET_PASSWORD_KEY_PREFIX = "HiiMart:reset-password:strings:";
    private final ValueOperations<String, String> valueOps  ;

    public AuthRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOps = redisTemplate.opsForValue();
    }

    public void saveJwtKey(String email, String jwtKey) {
        valueOps.set(STRING_KEY_PREFIX+email, jwtKey, 1, TimeUnit.HOURS);
    }

    public void saveVerificationLink(String email, String token){
        valueOps.set(STRING_LINK_VERIFICATION_KEY_PREFIX+email,token,1,TimeUnit.HOURS);
    }

    public void saveResetPasswordLink(String email, String token){
        valueOps.set(STRING_LINK_RESET_PASSWORD_KEY_PREFIX+email,token,1,TimeUnit.HOURS);
    }
    public Boolean isVerificationLinkValid(String email) {
        return valueOps.get(STRING_LINK_VERIFICATION_KEY_PREFIX+email) != null;
    }
    public String getVerificationLink(String email) {
        return valueOps.get(STRING_LINK_VERIFICATION_KEY_PREFIX+email);
    }
    public String getJwtKey(String email) {
        return valueOps.get(STRING_KEY_PREFIX+email);
    }

    public void deleteJwtKey(String email) {
        valueOps.getOperations().delete(STRING_KEY_PREFIX+email);
    }


    public void deleteVerificationLink(String email){
        valueOps.getOperations().delete(STRING_LINK_VERIFICATION_KEY_PREFIX+email);
    }

    public void blackListJwt(String email, String jwt) {
        valueOps.set(STRING_BLACKLIST_KEY_PREFIX+jwt, email, 1, TimeUnit.HOURS);
    }

    public Boolean isKeyBlacklisted(String jwt) {
        return valueOps.get(STRING_BLACKLIST_KEY_PREFIX + jwt) != null;
    }
}
