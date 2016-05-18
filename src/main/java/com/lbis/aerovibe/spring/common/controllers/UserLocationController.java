/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.controllers;

import com.lbis.aerovibe.model.UserLocation;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.couchbase.executors.UserLocationActions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class UserLocationController {

    @Autowired
    UserLocationActions userLocationActions;

    private void addNewUserLocation(UserLocation userLocation, Logger logger) {
        try {
            userLocationActions.put(userLocation);
            logger.info("Successfully added " + userLocation.toString() + "to DB");
        } catch (Throwable th) {
            logger.info("Failed to add " + userLocation.toString() + "to DB");
        }
    }
}
