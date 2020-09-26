package com.webapp.mytiktokdata;


import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MapPlotter extends AbstractImageProcess {


  private String username;

  private List<String> data;
  private List<String> ipList;
  private final String API_KEY = "AIzaSyBVJzkb29J7SFH-DP0MKgVJEuKZm7P_G7M";

  OkHttpClient client = new OkHttpClient();

  public MapPlotter(List<String> data, String username) {
    this.data = data;
    this.username = username;
  }

  public void plotIPs() throws IOException, GeoIp2Exception {
    ipList = new ArrayList<>();

    for (String line : data) {
      if (line.indexOf("IP: ") == 0) {
        ipList.add(line.substring(4));
      }
    }

    Set<String> set = new LinkedHashSet<>(); // remove dupes
    set.addAll(ipList);
    ipList.clear();
    ipList.addAll(set);

    this.requestLocData(ipList);
  }

  private void requestLocData(List<String> ipAddress) throws IOException, GeoIp2Exception {

    InputStream in = getClass().getResourceAsStream("/GeoLite2-City.mmdb"); // get database file
    File database = new File("GeoLite2-City.mmdb");
    FileUtils.copyInputStreamToFile(in, database);


    DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

    List<Location> locations = new ArrayList<>();

    for (String ip : ipAddress) {
      CityResponse response = dbReader.city(InetAddress.getByName(ip));
      locations.add(response.getLocation());
    }


    this.plotOnMap(locations);

  }

  private void plotOnMap(List<Location> locations) throws IOException {

    String locationmarkers = "";


    for (Location loc : locations) {
      locationmarkers += "&markers=color:red%7C" + loc.getLatitude() + "," + loc.getLongitude();
    }

    String url = "https://maps.googleapis.com/maps/api/staticmap?parameters"
            + "&size=2000x400"
            + locationmarkers
            + "&scale=2"
            + "&key=" + API_KEY;


    Request request = new Request.Builder()
            .url(url)
            .build();


    File targetFile = new File("IPMap.png");

    try (Response response = client.newCall(request).execute()) {
      FileUtils.copyInputStreamToFile(response.body().byteStream(), targetFile);
    }

    this.upload("IPMap.png", username);

  }


}
