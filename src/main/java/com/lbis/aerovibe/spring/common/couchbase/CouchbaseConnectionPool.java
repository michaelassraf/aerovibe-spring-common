/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.couchbase;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import com.couchbase.client.vbucket.config.CouchbaseConfig;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.properties.PropertiesFieldNames;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import net.spy.memcached.HashAlgorithm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Development User
 */
@Component
@Scope(AerovibeUtils.SINGLETON)
public class CouchbaseConnectionPool {

    @Value(PropertiesFieldNames.dBFieldNameBucketName)
    private String dBFieldNameBucketName;

    @Value(PropertiesFieldNames.dBFieldNamePoolSize)
    private Integer dBFieldNamePoolSize;

    @Value(PropertiesFieldNames.dBFieldNamePassword)
    private String dBFieldNamePassword;

    @Value(PropertiesFieldNames.dBFieldNameServersList)
    private List<URI> dBFieldNameServersList;

    public HashMap<Integer, CouchbaseClient> pool;
    AtomicInteger counter;
    Logger logger = Logger.getLogger(CouchbaseConnectionPool.class);

    @PostConstruct
    void initPool() {
        
        CouchbaseConnectionFactoryBuilder cfb = new CouchbaseConnectionFactoryBuilder();
        cfb.setOpQueueMaxBlockTime(60000);
        cfb.setViewTimeout(50000);
        cfb.setAuthWaitTime(10000);
        cfb.setOpTimeout(60000);
        cfb.setTimeoutExceptionThreshold(70000);
        counter = new AtomicInteger(0);
        pool = new HashMap();
        while (counter.get() < dBFieldNamePoolSize) {
            try {
                pool.put(counter.getAndIncrement(), new CouchbaseClient(cfb.buildCouchbaseConnection(dBFieldNameServersList, dBFieldNameBucketName, dBFieldNamePassword)));
            } catch (Throwable th) {
                logger.error("Can't build connection pool.", th);
            }
        }
    }

    public CouchbaseClient getClient() {
        if (counter.get() >= pool.size()) {
            counter.set(0);
        }
        return pool.get(counter.getAndIncrement());
    }

    @PreDestroy
    private void shutdownPool() {
        for (CouchbaseClient couchbaseClient : pool.values()) {
            couchbaseClient.shutdown();
        }
    }
}
