/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.mapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lbis.aerovibe.utils.AerovibeUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Development User
 */
@Component
@Scope(AerovibeUtils.SINGLETON)
public class GsonEnumBean {

    Gson gson =new GsonBuilder().registerTypeAdapterFactory(new EnumAdapterFactory()).create();

    public Gson getGson() {
        return gson;
    }
}
