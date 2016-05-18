/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.controllers;

import com.lbis.aerovibe.enums.HTTPHeaders;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.location.geoip.GetLocationFromGeoIP;
import com.lbis.aerovibe.spring.common.location.geoip2.GetLocationFromGeoIP2;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.PROTOTYPE)
public class LocationController {

    Logger logger = Logger.getLogger(LocationController.class);

    @Autowired
    GetLocationFromGeoIP getLocationFromGeoIP;

    @Autowired
    GetLocationFromGeoIP2 getLocationFromGeoIP2;

    public Double[] getUserLocationForIP(String ip) {
        Double[] location = getLocationFromGeoIP2.getLocationForIP(ip);
        if (location == null) {
            location = getUserLocationForIP(ip);
        }
        return location;

    }

    public void addLocationToResponseHeader(String ip, HttpServletResponse response) {
        if (ip != null && (ip.contains("192.168.0.") || ip.contains("127.0.0.1"))) {
            response.addHeader(HTTPHeaders.UserLatitude.gethTTPHeaderValue(), "0.0");
            response.addHeader(HTTPHeaders.UserLongitude.gethTTPHeaderValue(), "0.0");
            return;
        }
        Double[] location = getUserLocationForIP(ip);
        if (location == null || location.length < 1) {
            return;
        }
        response.addHeader(HTTPHeaders.UserLatitude.gethTTPHeaderValue(), String.valueOf(location[0]));
        response.addHeader(HTTPHeaders.UserLongitude.gethTTPHeaderValue(), String.valueOf(location[1]));
    }

}
