/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.couchbase.executors;

import com.couchbase.client.protocol.views.Paginator;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.Stale;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.spring.common.couchbase.CouchbaseExecuteAbs;
import com.lbis.aerovibe.utils.AerovibeUtils;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Development User
 */
@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class SensorActions extends CouchbaseExecuteAbs<Sensor> {

    String designDocName = "sensor";
    String getAllSensorsViewName = "get_all_sensors";

    Logger logger = Logger.getLogger(SensorActions.class);

    public LinkedList<Sensor> getAllSensors() throws IOException {
        LinkedList<Sensor> sensors = new LinkedList<>();
        View view = couchbaseConnectionPool.getClient().getView(designDocName, getAllSensorsViewName);
        ViewResponse result = couchbaseConnectionPool.getClient().query(view, new Query().setStale(Stale.FALSE));

        ExecutorService service = Executors.newFixedThreadPool(20);

        List<Future<Sensor>> futures = new ArrayList<>();

        for (ViewRow row : result) {
            futures.add(service.submit(new SensorCallable(row)));
        }

        futures.stream().forEach((future) -> {
            try {
                Sensor sensor = future.get();
                if (sensor != null) {
                    sensors.add(sensor);
                }
            } catch (Throwable th) {
                logger.error("Failed to get response from callable.", th);
            }
        });
        
        service.shutdown();

        if (sensors.size() < 1) {
            return null;
        }
        return sensors;
    }

    public LinkedList<Sensor> getAllSensorsChunked() throws IOException {
        LinkedList<Sensor> sensors = new LinkedList<>();
        View view = couchbaseConnectionPool.getClient().getView(designDocName, getAllSensorsViewName);
        ViewResponse result = couchbaseConnectionPool.getClient().query(view, new Query().setStale(Stale.FALSE).setIncludeDocs(true));
        Collection<Object> collection = result.getMap().values();
        Object[] objects = collection.toArray();
        int chunkSize = 100;

        List<ArrayList<String>> listOfLists = new ArrayList<>();

        int i = 0;
        for (int t = 0; t < objects.length; t++) {
            if (t % chunkSize == 0) {
                i++;
                listOfLists.add(new ArrayList<>());
            }
            listOfLists.get(t).add((String) objects[i]);
        }

        logger.info("We have sum of " + listOfLists.size() + " lists of sensors.");

        ExecutorService service = Executors.newFixedThreadPool(listOfLists.size());

        List<Future<List<Sensor>>> futures = new ArrayList<>();

        for (ArrayList<String> stringList : listOfLists) {
            futures.add(service.submit(() -> {
                List<Sensor> partialSensors = new ArrayList<>();
                for (String currentKey : stringList) {
                    sensors.add(get(currentKey));
                }
                return partialSensors;
            }));
        }

        for (Future<List<Sensor>> future : futures) {
            try {
                sensors.addAll(future.get());
                logger.debug("Added " + chunkSize + " more to big list.");
            } catch (Throwable th) {
                logger.error("Can't add partial list to big list.", th);
            }
        }

        service.shutdown();

        if (sensors.size() < 1) {
            return null;
        }
        return sensors;
    }

    @Override
    public Class<Sensor> getClassType() {
        return Sensor.class;
    }

    class SensorCallable implements Callable<Sensor> {

        ViewRow viewRow;

        public SensorCallable(ViewRow viewRow) {
            this.viewRow = viewRow;
        }

        @Override
        public Sensor call() throws Exception {
            try {
                return get(viewRow.getKey());
            } catch (Throwable th) {
                logger.error("Can't get " + viewRow.getKey() + " from database", th);
                return null;
            }
        }
    }
}
