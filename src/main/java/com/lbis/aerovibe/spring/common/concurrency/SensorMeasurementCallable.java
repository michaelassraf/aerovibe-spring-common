/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.concurrency;

import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.spring.common.couchbase.executors.SensorMeasurementActions;
import com.lbis.aerovibe.utils.AerovibeUtils;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class SensorMeasurementCallable implements Callable<SensorMeasurement> {

    private String sensorId;
    
    @Autowired
    SensorMeasurementActions sensorMeasurementActions;
    
    public SensorMeasurementCallable buildSensorMeasurementCallable(String sensorId){
        this.sensorId = sensorId;
        return this;
    }
    
    @Override
    public SensorMeasurement call() throws Exception {
        return sensorMeasurementActions.get(sensorId);
    }

}
