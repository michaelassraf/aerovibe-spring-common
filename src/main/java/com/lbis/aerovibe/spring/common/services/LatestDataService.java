/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.services;

import com.lbis.aerovibe.enums.DataProvidorsEnums;
import com.lbis.aerovibe.model.PartialList;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.controllers.SensorController;
import com.lbis.aerovibe.spring.common.controllers.SensorMeasurementController;
import com.lbis.aerovibe.spring.common.properties.PropertiesFieldNames;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LatestDataService {

    @Autowired
    SensorController sensorContrroller;
    @Autowired
    SensorMeasurementController sensorMeasurementContrroller;

    Logger logger = Logger.getLogger(LatestDataService.class);

    @Value(PropertiesFieldNames.latestDataSinglePageSize)
    Integer latestDataSinglePageSize;

    private final int latestSensorsAndSensorMeasurements = 4 * 60 * 1000;
    
    volatile LinkedList<SensorMeasurement> latestMeasurements = new LinkedList<SensorMeasurement>();
    volatile LinkedList<Sensor> activeSensors = new LinkedList<Sensor>();
    volatile HashMap<String, SensorMeasurement> latestMeasurementsHashMap = new HashMap<String, SensorMeasurement>();

    volatile HashMap<DataProvidorsEnums, LinkedList<Sensor>> activeSensorsByProvider = new HashMap<DataProvidorsEnums, LinkedList<Sensor>>();
    volatile HashMap<DataProvidorsEnums, LinkedList<SensorMeasurement>> latestMeasurementsByProvider = new HashMap<DataProvidorsEnums, LinkedList<SensorMeasurement>>();

    volatile LinkedList<PartialList<SensorMeasurement>> latestMeasurementsChunked = new LinkedList<PartialList<SensorMeasurement>>();
    volatile LinkedList<PartialList<Sensor>> activeSensorsChunked = new LinkedList<PartialList<Sensor>>();

    
    @Async
    @Scheduled(fixedDelay = latestSensorsAndSensorMeasurements)
    public void getSensorsMeasurements() {
        Thread.currentThread().setName(LatestDataService.class.getSimpleName() + " refresh interval");
        logger.info("** Starting to build latest results Hash Map **");

        LinkedList<SensorMeasurement> latestMeasurements = new LinkedList<>();
        LinkedList<Sensor> activeSensors = new LinkedList<>();

        HashMap<String, SensorMeasurement> latestMeasurementsHashMap = new HashMap<>();

        HashMap<DataProvidorsEnums, LinkedList<Sensor>> activeSensorsByProvider = new HashMap<DataProvidorsEnums, LinkedList<Sensor>>();
        HashMap<DataProvidorsEnums, LinkedList<SensorMeasurement>> latestMeasurementsByProvider = new HashMap<DataProvidorsEnums, LinkedList<SensorMeasurement>>();

        LinkedList<PartialList<SensorMeasurement>> latestMeasurementsChunked = new LinkedList<PartialList<SensorMeasurement>>();
        LinkedList<PartialList<Sensor>> activeSensorsChunked = new LinkedList<PartialList<Sensor>>();

        if (this.latestMeasurements.size() < 1) {
            latestMeasurements = this.latestMeasurements;
        }
        if (this.activeSensors.size() < 1) {
            activeSensors = this.activeSensors;
        }
        if (this.latestMeasurementsHashMap.size() < 1) {
            latestMeasurementsHashMap = this.latestMeasurementsHashMap;
        }
        LinkedList<Sensor> sensors = sensorContrroller.getAllSensors();
        
        logger.info("Successfully got " + sensors.size() + " sensors.");

        int numberOfMeasurments = 0;
        int numberOfPages = 0;
        for (Sensor sensor : sensors) {
            SensorMeasurement sensorMeasurement = sensorMeasurementContrroller.getLatestSensorMeasurementForSensorId(sensor.getObjectKey());
            if (sensorMeasurement == null) {
                continue;
            }
            activeSensors.add(sensor);
            latestMeasurements.add(sensorMeasurement);
            latestMeasurementsHashMap.put(sensor.getObjectKey(), sensorMeasurement);

            if (numberOfMeasurments % latestDataSinglePageSize == 0) {
                PartialList sensorMeasurementPartialList = new PartialList<SensorMeasurement>();
                PartialList sensorPartialList = new PartialList<Sensor>();

                sensorMeasurementPartialList.setPartialListList(new LinkedList());
                sensorPartialList.setPartialListList(new LinkedList());

                sensorMeasurementPartialList.setPartialListCount(numberOfPages);
                sensorPartialList.setPartialListCount(numberOfPages);

                latestMeasurementsChunked.add(sensorMeasurementPartialList);
                activeSensorsChunked.add(sensorPartialList);

                numberOfPages++;
            }

            latestMeasurementsChunked.get(latestMeasurementsChunked.size() - 1).getPartialListList().add(sensorMeasurement);
            activeSensorsChunked.get(activeSensorsChunked.size() - 1).getPartialListList().add(sensor);

            if (activeSensorsByProvider.get(sensor.getSensorDataProvidor()) == null) {
                activeSensorsByProvider.put(sensor.getSensorDataProvidor(), new LinkedList<>());
            }

            activeSensorsByProvider.get(sensor.getSensorDataProvidor()).add(sensor);

            if (latestMeasurementsByProvider.get(sensorMeasurement.getSensorMeasurementDataProvidor()) == null) {
                latestMeasurementsByProvider.put(sensorMeasurement.getSensorMeasurementDataProvidor(), new LinkedList<>());
            }
            latestMeasurementsByProvider.get(sensorMeasurement.getSensorMeasurementDataProvidor()).add(sensorMeasurement);
            numberOfMeasurments++;
        }
        this.activeSensors = activeSensors;
        this.latestMeasurements = latestMeasurements;
        this.latestMeasurementsHashMap = latestMeasurementsHashMap;
        this.activeSensorsByProvider = activeSensorsByProvider;
        this.latestMeasurementsByProvider = latestMeasurementsByProvider;

        latestMeasurementsChunked.stream().forEach((currrntPartialList) -> {
            currrntPartialList.setPartialListSumOfLists(latestMeasurementsChunked.size() - 1);
        });

        activeSensorsChunked.stream().forEach((currrntPartialList) -> {
            currrntPartialList.setPartialListSumOfLists(activeSensorsChunked.size() - 1);
        });

        this.latestMeasurementsChunked = latestMeasurementsChunked;
        this.activeSensorsChunked = activeSensorsChunked;

        logger.info("** Finished to build latest results Lists ! **");
    }

    public LinkedList<SensorMeasurement> getLatestMeasurements() {
        return latestMeasurements;
    }

    public LinkedList<Sensor> getActiveSensors() {
        return activeSensors;
    }

    public SensorMeasurement getClosestSensorMeasurement(double latitude, double longitude) {
        SensorMeasurement closestSensorMeasurement = null;
        double closestDistance = Double.MAX_VALUE;
        for (Sensor currentSensor : activeSensors) {
            double currentDistance = AerovibeUtils.getInstance().distanceBetween2Points(latitude, longitude, currentSensor.getSensorLatitude(), currentSensor.getSensorLongitude(), 'M');
            if (Double.compare(currentDistance, closestDistance) < 0) {
                if (latestMeasurementsHashMap.containsKey(currentSensor.getObjectKey())) {
                    closestDistance = currentDistance;
                    closestSensorMeasurement = latestMeasurementsHashMap.get(currentSensor.getObjectKey());
                }
            }
        }
        return closestSensorMeasurement;
    }

    public LinkedList<SensorMeasurement> getLatestMeasurementsByProvider(DataProvidorsEnums provider) {
        return latestMeasurementsByProvider.get(provider);
    }

    public LinkedList<Sensor> getActiveSensorsByProvider(DataProvidorsEnums provider) {
        return activeSensorsByProvider.get(provider);
    }

    public LinkedList<PartialList<SensorMeasurement>> getLatestMeasurementsChunked() {
        return latestMeasurementsChunked;
    }

    public void setLatestMeasurementsChunked(LinkedList<PartialList<SensorMeasurement>> latestMeasurementsChunked) {
        this.latestMeasurementsChunked = latestMeasurementsChunked;
    }

    public LinkedList<PartialList<Sensor>> getActiveSensorsChunked() {
        return activeSensorsChunked;
    }

    public void setActiveSensorsChunked(LinkedList<PartialList<Sensor>> activeSensorsChunked) {
        this.activeSensorsChunked = activeSensorsChunked;
    }

}
