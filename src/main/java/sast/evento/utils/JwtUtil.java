package sast.evento.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final RedisUtil redisUtil;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    public Integer expiration;

    public JwtUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public String generateToken(Map<String, String> map) {
        if (map == null) {
            throw new LocalRunTimeException(ErrorEnum.TOKEN_ERROR);
        }
        Calendar time = Calendar.getInstance();
        time.add(Calendar.HOUR, expiration);
        JWTCreator.Builder builder = JWT.create();
        for (Map.Entry<String, String> element : map.entrySet()) {
            builder.withClaim(element.getKey(), element.getValue());
        }
        builder.withExpiresAt(time.getTime());
        return builder.sign(Algorithm.HMAC256(secret));
    }

    public Map<String, Claim> getClaims(String token) {
        if (token == null) {
            throw new LocalRunTimeException(ErrorEnum.TOKEN_ERROR, "Token is null.");
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaims();
        } catch (JWTVerificationException e) {
            throw new LocalRunTimeException(ErrorEnum.TOKEN_ERROR, e.getMessage());
        }
    }

    public Boolean isExpired(String userId, String token) {
        Long expire = redisUtil.getExpire("TOKEN:" + userId);
        String oriToken = (String) redisUtil.hget("TOKEN:", userId);
        return (expire <= 0) && oriToken.equals(token);
    }

    public void reFreshToken(String userId) {
        redisUtil.expire("TOKEN:" + userId, 3, TimeUnit.DAYS);
    }


}
