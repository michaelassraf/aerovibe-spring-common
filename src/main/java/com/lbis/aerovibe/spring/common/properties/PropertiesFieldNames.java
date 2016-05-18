/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lbis.aerovibe.spring.common.properties;

/**
 *
 * @author Development User
 */
public class PropertiesFieldNames {

    public static final String sensorMeasurementTimeToLive = "${sensorMeasurementTimeToLive}";
                                                        
    public static final String dBFieldNameServersList = "#{'${dBFieldNameServersList}'.split(',')}";
    public static final String dBFieldNameBucketName = "${dBFieldNameBucketName}";
    public static final String dBFieldNamePoolSize = "${dBFieldNamePoolSize}";
    public static final String dBFieldNamePassword = "${dBFieldNamePassword}";

    public static final String iMEPWebServiceStationsURL = "${iMEPWebServiceStationsURL}";
    public static final String iMEPWebServiceSingleStationValuesURL = "${iMEPWebServiceSingleStationValuesURL}";

    public static final String elmWebServiceStationsURL = "${elmWebServiceStationsURL}";
    public static final String elmWebServiceSingleStationValuesURL = "${elmWebServiceSingleStationValuesURL}";

    public static final String airQualityEggWebServiceStationsURL = "${airQualityEggWebServiceStationsURL}";
    public static final String airQualityEggWebServiceStationsURLAuthToken = "${airQualityEggWebServiceStationsURLAuthToken}";

    public static final String geoNamesURL = "${geoNamesURL}";
    public static final String geoNamesAccountList = "${geoNamesAccountList}";

    public static final String flickerBaseURL = "${flickerBaseURL}";
    public static final String flickerPhotoSearchMethod = "${flickerPhotoSearchMethod}";
    public static final String flickerTagsPrefix = "${flickerTagsPrefix}";
    public static final String flickerSafeSearchPrefix = "${flickerSafeSearchPrefix}";
    public static final String flickerFormatPrefix = "${flickerFormatPrefix}";
    public static final String flickerAPIKeyPrefix = "${flickerAPIKeyPrefix}";
    public static final String flickerPerPagePrefix = "${flickerPerPagePrefix}";
    public static final String flickerSortPrefix = "${flickerSortPrefix}";
    public static final String flickerLonPrefix = "${flickerLonPrefix}";
    public static final String flickerLatPrefix = "${flickerLatPrefix}";
    public static final String flickerPlaceSearchMethod = "${flickerPlaceSearchMethod}";
    public static final String flickerWOEIdPrefix = "${flickerWOEIdPrefix}";
    public static final String flickerAccuracyPrefix = "${flickerAccuracyPrefix}";
    public static final String flickerContentTypePrefix = "${flickerContentTypePrefix}";
    public static final String flickerAPIKey = "${flickerAPIKey}";
    public static final String flickerTags = "${flickerTags}";
    public static final String flickerMediaPrefix = "${flickerMediaPrefix}";
    public static final String flickerGeoContextPrefix = "${flickerGeoContextPrefix}";
    public static final String flickerPlaceIdPrefix = "${flickerPlaceIdPrefix}";

    public static final String panoramioBaseURL = "${panoramioBaseURL}";
    public static final String panoramioFromPrefix = "${panoramioFromPrefix}";
    public static final String panoramioToPrefix = "${panoramioToPrefix}";
    public static final String panoramioMinLonPrefix = "${panoramioMinLonPrefix}";
    public static final String panoramioMaxLonPrefix = "${panoramioMaxLonPrefix}";
    public static final String panoramioMinLatPrefix = "${panoramioMinLatPrefix}";
    public static final String panoramioMaxLatPrefix = "${panoramioMaxLatPrefix}";
    public static final String panoramioSizePrefix = "${panoramioSizePrefix}";
    public static final String panoramioMapFilterPrefix = "${panoramioMapFilterPrefix}";

    public static final String googleTranslateBaseURL = "${googleTranslateBaseURL}";

    public static final String aqiCNTempFolderPath = "${aqiCNTempFolderPath}";
    public static final String aqiCNExecFile = "${aqiCNExecFile}";
    public static final String aqiCNLinuxExecCommand = "${aqiCNLinuxExecCommand}";
    public static final String aqiCNWindowsExecCommand = "${aqiCNWindowsExecCommand}";
    public static final String aqiCNWindowsKillCommand = "${aqiCNWindowsKillCommand}";
    public static final String aqiCNLinuxKillCommand = "${aqiCNLinuxKillCommand}";
    public static final String aqiCNWindowsListCommand = "${aqiCNWindowsListCommand}";
    public static final String aqiCNLinuxListCommand = "${aqiCNLinuxListCommand}";
    
    public static final String aqiCNScrapingServiceURLs = "#{'${aqiCNScrapingServiceURLs}'.split(',')}";
    public static final String aqiCNScrapingGetAllSensorsURL = "${aqiCNScrapingGetAllSensorsURL}";

    public static final String getLocationFromGeoIPFilePath = "${getLocationFromGeoIPFilePath}";
    public static final String getLocationFromGeoIP2FilePath = "${getLocationFromGeoIP2FilePath}";
    
    public static final String latestDataSinglePageSize = "${latestDataSinglePageSize}";
    
    public static final String fileViewPathPrefix = "${fileViewPathPrefix}";
}
