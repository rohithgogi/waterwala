package com.waterwala.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;

@Slf4j
@Component
public class SharedJwtTokenProvider {
    /*private final SecretKey key;
    private final int jwtExpirationInMs;

    public SharedJwtTokenProvider(@Value("${jwt.secret}") String jwtSecret,
                                  @Value("${jwt.expiration:86400000}") int jwtExpirationInMs){
        this.key= Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationInMs=jwtExpirationInMs;
    }*/

    //public String generateToken(Long userId,String email,UserRole role);
    public static void main(String[] args) {
        int[] data={1,2,3,4};
        int[] datasource=Arrays.copyOfRange(data,2,4);
        for(int x : datasource){
            System.out.println("data is "+x);
        }

    }
    /*private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);


    }*/
}
