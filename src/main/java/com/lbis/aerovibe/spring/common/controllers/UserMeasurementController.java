/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.controllers;

import com.couchbase.client.protocol.views.ViewRow;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.model.UserLocation;
import com.lbis.aerovibe.model.UserMeasurement;
import com.lbis.aerovibe.spring.common.couchbase.executors.UserMeasurementActions;
import com.lbis.aerovibe.spring.common.location.geoip2.GetLocationFromGeoIP2;
import com.lbis.aerovibe.spring.common.services.LatestDataService;
import com.lbis.aerovibe.utils.AerovibeUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class UserMeasurementController {

    @Autowired
    UserMeasurementActions userMeasurementActions;

    @Autowired
    LatestDataService latestDataService;

    @Autowired
    GetLocationFromGeoIP2 getLocationFromGeoIP2;

    Logger logger = Logger.getLogger(UserMeasurementController.class);
    
    public void addNewUserMeasurement(UserMeasurement userMeasurement, Logger logger) {
        try {
            userMeasurementActions.put(userMeasurement);
            logger.info("Successfully added user measurement to DB.");
        } catch (Throwable th) {
            logger.error("Failed to add user measurement to DB.", th);
        }
    }

    public UserLocation addNewUserMeasurement(UserLocation userLocation, String ip, Logger logger) {
        if (userLocation == null) {
            logger.error("Bad user location object sent wiil exit.");
            return userLocation;
        }

        if (userLocation.getUserLocationLatitude() == null || userLocation.getUserLocationLongitude() == null) {
            logger.info("Didn't got location in user location object, will try to get from IP.");
            userLocation = getLocationFromGeoIP2.getUserLocationForIP(userLocation.getUserLocationUserId(), ip);
            if (userLocation == null) {
                logger.error("Couldn't recieve location form IP, IP was " + ip);
                return userLocation;
            }
        }
        SensorMeasurement closestSensorMeasurement = latestDataService.getClosestSensorMeasurement(userLocation.getUserLocationLatitude(), userLocation.getUserLocationLongitude());
        userLocation.setUserLocationClosestSensorId(closestSensorMeasurement.getSensorMeasurementSensorId());
        if (closestSensorMeasurement == null) {
            logger.error("Bad closest sensor measurement recieved. Will exit.");
            return userLocation;
        }
        addNewUserMeasurement(new UserMeasurement(userLocation, closestSensorMeasurement, System.currentTimeMillis()), logger);

        return userLocation;
    }

    public List<UserMeasurement> getUserMeasurementsForUser(String userId, Long from, Long to, Logger logger) {
        List<UserMeasurement> userMeasurements = userMeasurementActions.getUserMeasurementsForUser(userId, from, to);
        if (userMeasurements == null){
            return new LinkedList<>();
        }
        return userMeasurements;
    }

}
