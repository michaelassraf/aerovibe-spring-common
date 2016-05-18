/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.couchbase.executors;

import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.spring.common.concurrency.SensorMeasurementCallable;
import com.lbis.aerovibe.spring.common.controllers.TokenController;
import com.lbis.aerovibe.spring.common.couchbase.CouchbaseExecuteAbs;
import com.lbis.aerovibe.utils.AerovibeUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Development User
 */
@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class SensorMeasurementActions extends CouchbaseExecuteAbs<SensorMeasurement> {

    String designDocName = "sensor_measurement";
    String getLatestSensorMeasurementForSensorIdQueryName = "get_latest_sensor_measurement";

    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    ApplicationContext applicationContext;

    public SensorMeasurement getLatestSensorMeasurementForSensorId(String sensorId) throws IOException {
        Query query = new Query().setKey(sensorId).setDescending(true).setIncludeDocs(false).setLimit(1);
        View view = couchbaseConnectionPool.getClient().getView(designDocName, getLatestSensorMeasurementForSensorIdQueryName);
        ViewResponse result = couchbaseConnectionPool.getClient().query(view, query);
        String latestSensorMeasurementId = null;
        for (ViewRow row : result) {
            latestSensorMeasurementId = row.getValue();
        }
        if (latestSensorMeasurementId == null) {
            return null;
        }
        return get(latestSensorMeasurementId);

    }

    public List<SensorMeasurement> getLatestSensorMeasurementsForSensorId(String sensorId, int noOfMeasurements) throws IOException {
        Query query = new Query().setKey(sensorId).setDescending(true).setIncludeDocs(false).setLimit(noOfMeasurements);
        View view = couchbaseConnectionPool.getClient().getView(designDocName, getLatestSensorMeasurementForSensorIdQueryName);
        ViewResponse result = couchbaseConnectionPool.getClient().query(view, query);
        List<SensorMeasurement> latestSensorMeasurements = new ArrayList<>();

        List<Future<SensorMeasurement>> sensorMeasurementFutures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(noOfMeasurements);

        for (ViewRow row : result) {
            sensorMeasurementFutures.add(executorService.submit(applicationContext.getBean(SensorMeasurementCallable.class).buildSensorMeasurementCallable(row.getValue())));
        }

        sensorMeasurementFutures.stream().forEach((sensorMeasurementFuture) -> {
            try {
                latestSensorMeasurements.add(sensorMeasurementFuture.get(10, TimeUnit.SECONDS));
            } catch (Throwable th) {
                logger.error("Can't get result for sensor Id", th);
            }
        });
        
        executorService.shutdown();

        if (latestSensorMeasurements.size() < 1) {
            return null;
        }
        return latestSensorMeasurements;

    }

    @Override
    public Class<SensorMeasurement> getClassType() {
        return SensorMeasurement.class;
    }

}
