/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.couchbase.executors;

import com.lbis.aerovibe.model.Token;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.couchbase.CouchbaseExecuteAbs;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Development User
 */
@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class TokenActions extends CouchbaseExecuteAbs<Token> {

    @Override
    public Class<Token> getClassType() {
        return Token.class;
    }
}
