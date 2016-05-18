/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.couchbase;

import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.mapping.GsonBean;
import com.lbis.database.model.KeyObjectIfc;
import com.lbis.database.model.ValueObjectIfc;
import java.io.IOException;
import java.util.LinkedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Development User
 * @param <HANDELOBJECTTYPE>
 */
@Component
@Scope(AerovibeUtils.PROTOTYPE)
public abstract class CouchbaseExecuteAbs<HANDELOBJECTTYPE extends KeyObjectIfc & ValueObjectIfc<HANDELOBJECTTYPE>> {

    @Autowired
    protected CouchbaseConnectionPool couchbaseConnectionPool;
    @Autowired
    GsonBean gsonBean;

    public HANDELOBJECTTYPE get(String key) throws IOException {
        Object jsonSerialized = couchbaseConnectionPool.getClient().get(key);
        if (jsonSerialized == null) {
            return null;
        }
        return gsonBean.getGson().fromJson(jsonSerialized.toString(), getClassType());
    }

    public void put(HANDELOBJECTTYPE value) throws IOException {
        couchbaseConnectionPool.getClient().set(value.getObjectKey(), gsonBean.getGson().toJson(value));
    }

    public void putAll(LinkedList<HANDELOBJECTTYPE> values) throws IOException {
        values.stream().forEach((value) -> {
            couchbaseConnectionPool.getClient().set(value.getObjectKey(), gsonBean.getGson().toJson(value));
        });
    }

    public abstract Class<HANDELOBJECTTYPE> getClassType();
}
