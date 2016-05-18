/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.location.geoip2;

import com.lbis.aerovibe.model.UserLocation;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.spring.common.properties.PropertiesFieldNames;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import java.io.File;
import java.net.InetAddress;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(AerovibeUtils.SINGLETON)
public class GetLocationFromGeoIP2 {

    @Value(PropertiesFieldNames.getLocationFromGeoIP2FilePath)
    String getLocationFromGeoIPFilePath;
    File getLocationFromGeoIP2BinaryFile;
    Logger logger = Logger.getLogger(GetLocationFromGeoIP2.class);
    DatabaseReader getLocationFromGeoIP2DatabaseReader;

    @PostConstruct
    private void loadGeoIP2FileFromResource() {
        try {
            getLocationFromGeoIP2BinaryFile = new File(getLocationFromGeoIPFilePath);
            getLocationFromGeoIP2DatabaseReader = new DatabaseReader.Builder(getLocationFromGeoIP2BinaryFile).build();
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
        CityResponse cityResponse = null;

        try {
            cityResponse = getLocationFromGeoIP2DatabaseReader.city(InetAddress.getByName(ip));
        } catch (Throwable th) {
            logger.error("Can't get location from ip " + ip, th);
        }

        return new Double[]{cityResponse.getLocation().getLatitude(), cityResponse.getLocation().getLongitude()};
    }
}
