/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.location.geoip;

import com.lbis.aerovibe.model.UserLocation;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.properties.PropertiesFieldNames;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import java.io.File;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.SINGLETON)
public class GetLocationFromGeoIP {

    @Value(PropertiesFieldNames.getLocationFromGeoIPFilePath)
    String getLocationFromGeoIPFilePath;

    File getLocationFromGeoIPBinaryFile;

    Logger logger = Logger.getLogger(GetLocationFromGeoIP.class);

    LookupService getLocationFromGeoIPLookupService;

    @PostConstruct
    private void loadGeoIPFileFromResource() {
        try {
            getLocationFromGeoIPBinaryFile = new File(getLocationFromGeoIPFilePath);
            getLocationFromGeoIPLookupService = new LookupService(getLocationFromGeoIPBinaryFile, LookupService.GEOIP_MEMORY_CACHE);
            logger.debug("Successfully built geo ip lookup service");
        } catch (Throwable th) {
            logger.error("Can't build geo ip lookup service", th);
        }
    }

    public UserLocation getUserLocationForIP(String userId, String ip) {

        Double[] location = getLocationForIP(ip);
        if (location == null || location.length < 1 || Double.compare(location[0], 0.0f) == 0 || Double.compare(location[1], 0.0f) == 0) {
            return null;
        }
        return new UserLocation(location[0], location[1], userId, null);
    }

    public Double[] getLocationForIP(String ip) {
        Location location = getLocationFromGeoIPLookupService.getLocation(ip);
        return new Double[]{(double) location.latitude, (double) location.longitude};
    }
}
