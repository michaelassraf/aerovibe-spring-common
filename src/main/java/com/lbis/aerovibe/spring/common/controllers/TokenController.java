/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.controllers;

import com.lbis.aerovibe.enums.HTTPHeaders;
import com.lbis.aerovibe.model.Token;
import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.couchbase.executors.TokenActions;
import com.lbis.aerovibe.spring.common.token.TokenGenerator;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class TokenController {

    @Autowired
    TokenActions tokenActions;

    @Autowired
    TokenGenerator tokenGenerator;

    Logger logger = Logger.getLogger(TokenController.class);

    public Token getToken(String tokenValue) {
        try {
            return tokenActions.get(tokenValue);
        } catch (Throwable th) {
            logger.error("Failed to get token.", th);
            return null;
        }
    }

    public Token addToken(Token token) {
        try {
            tokenActions.put(token);
            Thread.sleep(1000L);
            return tokenActions.get(token.getObjectKey());
        } catch (Throwable th) {
            logger.error("Failed to add token.", th);
            return null;
        }
    }

    public Token generateAndStoreNewToken(String userAgent, User user) {
        String stringToken = tokenGenerator.getToken(user.toString() + userAgent, System.currentTimeMillis());
        Token token = new Token(stringToken, user.getUserId());
        Token addedToken = addToken(token);
        return addedToken;
    }

    public Boolean validateToken(String tokenValue, String tokenValueUserId, HttpServletResponse response) {
        boolean isValid = true;
        if (getToken(new Token(tokenValue, tokenValueUserId).getObjectKey()) == null) {
            try {
                response.sendError(HttpStatus.SC_FORBIDDEN, "Please refresh your token.");
                isValid = false;
            } catch (Throwable th) {
                logger.error("Can't return FORBIDDEN", th);
                isValid = false;
            }
        }
        return isValid;
    }

    public void addTokenToReponseHeader(String tokenValue, String tokenValueUserId, HttpServletResponse response) {
        response.addHeader(HTTPHeaders.TokenHeader.gethTTPHeaderValue(), tokenValue);
        response.addHeader(HTTPHeaders.UserHeader.gethTTPHeaderValue(), tokenValueUserId);
    }

}
