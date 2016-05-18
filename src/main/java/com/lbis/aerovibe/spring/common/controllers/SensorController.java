/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.controllers;

import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.couchbase.executors.SensorActions;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class SensorController {

    @Autowired
    SensorActions sensorActions;

    Logger logger = Logger.getLogger(SensorController.class);

    public LinkedList<Sensor> getAllSensors() {

        try {
            LinkedList<Sensor> sensors = sensorActions.getAllSensors();
            if (sensors == null || sensors.size() < 1) {
                throw new Throwable("Empty object was recieved from the DB.");
            }
            return sensors;
        } catch (Throwable th) {
            logger.error("No sensors recieved from DB.", th);
        }
        return null;
    }

    public Sensor getSensorById(String sensorId) {

        try {
            return sensorActions.get(sensorId);
        } catch (Throwable th) {
            logger.error("No sensor recieved from DB.", th);
        }
        return null;
    }

}
