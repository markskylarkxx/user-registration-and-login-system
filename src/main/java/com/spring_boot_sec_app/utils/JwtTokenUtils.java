package com.spring_boot_sec_app.utils;

import com.spring_boot_sec_app.security.UserPrincipal;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

@Configuration
@Slf4j
public class JwtTokenUtils {

    @Value("${app.jwtSecret}")
    private  String jwtSecret= "jwtSecretjhhhhhhhhhhjnsnnajcjndvs" +
           "nDVJJVsvgsvcgvsVVBSVACGAVGSVGCGSAGVSGASAGSCSGGVNwtYgsaYuuaiaiaijjshsggcsg";

    @Value("${app.jwtExpirationInMs}")
    private  int jwtExpirationInMs;// = 180003839;

    public String generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getDetails();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);




        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

    }


    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());

    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
//        } catch (ExpiredJwtException ex) {
//            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

}
