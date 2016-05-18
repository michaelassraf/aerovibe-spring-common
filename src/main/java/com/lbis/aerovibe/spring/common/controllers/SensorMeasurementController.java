/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.controllers;

import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.spring.common.couchbase.executors.SensorMeasurementActions;
import com.lbis.aerovibe.spring.common.properties.PropertiesFieldNames;
import com.lbis.aerovibe.utils.AerovibeUtils;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class SensorMeasurementController {

    @Autowired
    SensorMeasurementActions sensorMeasurementActions;

    @Value(PropertiesFieldNames.sensorMeasurementTimeToLive)
    Long sensorMeasurementTimeToLive;

    Logger logger = Logger.getLogger(SensorMeasurementController.class);

    public SensorMeasurement getLatestSensorMeasurementForSensorId(String sensorId) {
        try {
            SensorMeasurement sensorMeasurement = sensorMeasurementActions.getLatestSensorMeasurementForSensorId(sensorId);
            if (sensorMeasurement == null) {
                logger.info("Empty object recieved from the DB for sensor Id " + sensorId);
                return null;
            }

            if (sensorMeasurement.getSensorMeasurementTimeStamp() == null || sensorMeasurement.getSensorMeasurementTimeStamp().compareTo(System.currentTimeMillis() - sensorMeasurementTimeToLive) < 0) {
                logger.info("Sensor measurment for  " + sensorId + " is too old, won't insert it to map.");
                return null;
            }
            return sensorMeasurement;
        } catch (Throwable th) {
            logger.error("No suitable sensor measurement was found for sensor " + sensorId, th);
        }
        return null;
    }

    public List<SensorMeasurement> getLatestSensorMeasurementForSensorId(String sensorId, int noOfMeasurements) {
        try {
            List<SensorMeasurement> sensorMeasurements = sensorMeasurementActions.getLatestSensorMeasurementsForSensorId(sensorId, noOfMeasurements);
            if (sensorMeasurements == null) {
                logger.info("Empty object recieved from the DB for sensor Id " + sensorId);
                return null;
            }
            
            return sensorMeasurements;
        } catch (Throwable th) {
            logger.error("No suitable sensor measurement was found for sensor " + sensorId, th);
        }
        return null;
    }

}
