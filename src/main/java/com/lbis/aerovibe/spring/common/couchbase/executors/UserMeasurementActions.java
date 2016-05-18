/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.couchbase.executors;

import com.couchbase.client.protocol.views.ComplexKey;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.UserMeasurement;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.couchbase.CouchbaseExecuteAbs;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Development User
 */
@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class UserMeasurementActions extends CouchbaseExecuteAbs<UserMeasurement> {

    private static final String DESIGN_DOC_NAME = "user_measurement";
    private static final String USER_MEASUREMENTS_FOR_USER_ID_VIEW_NAME = "get_user_measurement_for_user_id";
    Logger logger = Logger.getLogger(UserMeasurementActions.class);

    public List<UserMeasurement> getUserMeasurementsForUser(String userId, Long from, Long to) {
        View view = couchbaseConnectionPool.getClient().getView(DESIGN_DOC_NAME, USER_MEASUREMENTS_FOR_USER_ID_VIEW_NAME);
        Query query = new Query().setRange(ComplexKey.of(userId, from), ComplexKey.of(userId, to));
        ViewResponse viewResponse = couchbaseConnectionPool.getClient().query(view, query);

        List<UserMeasurement> userMeasurements = new ArrayList<>();

        ExecutorService service = Executors.newFixedThreadPool(20);

        List<Future<UserMeasurement>> futures = new ArrayList<>();

        for (ViewRow row : viewResponse) {
            try {
                futures.add(service.submit(new UserMeasurementCallable(row)));
            } catch (Throwable th) {
                logger.error("Can't get user measurement from DB.", th);
            }
        }
        
        futures.stream().forEach((future) -> {
            try {
                UserMeasurement userMeasurement = future.get();
                if (userMeasurement != null) {
                    userMeasurements.add(userMeasurement);
                }
            } catch (Throwable th) {
                logger.error("Failed to get response from callable.", th);
            }
        });
        
        service.shutdown();
        
        return userMeasurements;
    }

    @Override
    public Class<UserMeasurement> getClassType() {
        return UserMeasurement.class;
    }

    class UserMeasurementCallable implements Callable<UserMeasurement> {

        ViewRow viewRow;

        public UserMeasurementCallable(ViewRow viewRow) {
            this.viewRow = viewRow;
        }

        @Override
        public UserMeasurement call() throws Exception {
            try {
                return get(viewRow.getValue());
            } catch (Throwable th) {
                logger.error("Can't get " + viewRow.getKey() + " from database", th);
                return null;
            }
        }
    }
}
