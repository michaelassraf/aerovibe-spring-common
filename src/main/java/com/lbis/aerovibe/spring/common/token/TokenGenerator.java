/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.token;

import com.lbis.aerovibe.utils.AerovibeUtils;
import java.security.MessageDigest;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class TokenGenerator {

    Logger logger = Logger.getLogger(TokenGenerator.class);

    public String getToken(String userAgent, Long timeInMiliSeconds) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String text = userAgent + timeInMiliSeconds;
            messageDigest.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                stringBuilder.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        } catch (Throwable th) {
            logger.error("Can't generate token !", th);
        }
        return null;
    }

}
