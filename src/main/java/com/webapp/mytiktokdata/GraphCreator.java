package com.webapp.mytiktokdata;


import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Creates the graphs for the data analysis.
 */
public class GraphCreator extends AbstractImageProcess {

  private final List<String> data;
  private final String username;
  private CategoryChart chart;

  /**
   * Constructs the GraphCreator.
   *
   * @param data The data in a list of strings
   * @param username the username
   */
  public GraphCreator(List<String> data, String username) {
    this.username = username;
    this.data = data;
  }

  /**
   * Sets up the style for the charts.
   */
  private void setupStyle() {
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setChartBackgroundColor(Color.WHITE);
    Font f = new Font("sanserif", Font.BOLD, 20);
    chart.getStyler().setChartTitleVisible(false);
    chart.getStyler().setAxisTitleFont(f);
    chart.getStyler().setSeriesColors(new Color[]{Color.BLUE});

  }

  /**
   * Creates the History graph.
   *
   * @throws IOException if the file is not found
   */
  public String buildHistory(String type) throws IOException {

    int fileLength = data.size();

    Date startDate = null;
    Date endDate = null;

    try {
      startDate = new SimpleDateFormat("yyyy-MM-dd").parse((data.get(fileLength - 3).substring(6, 16)));
      endDate = new SimpleDateFormat("yyyy-MM-dd").parse((data.get(0).substring(6, 16)));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    List<Date> dates = new ArrayList<>();
    Calendar calendar = new GregorianCalendar();
    assert startDate != null;
    calendar.setTime(startDate);

    while (calendar.getTime().before(endDate))
    {
      Date result = calendar.getTime();
      dates.add(result);
      calendar.add(Calendar.DATE, 1);
    }

    List<String> xData = new ArrayList<>();
    List<Integer> yData = new ArrayList<>();

    for (Date date : dates) {
      xData.add(new SimpleDateFormat("M/dd/yyyy").format(date));
    }


    Map<String, Integer> hash = new HashMap<>();

    for (String line : data) {  // count the videos
      if (line.indexOf("Date: ") == 0) {


        Date d = null;
        try {
          d = new SimpleDateFormat("yyyy-MM-dd").parse(line.substring(6, 16));
        } catch (ParseException e) {
          e.printStackTrace();
        }

        hash.put(new SimpleDateFormat("M/dd/yyyy").format(d), hash.getOrDefault(new SimpleDateFormat("M/dd/yyyy").format(d), 0) + 1);

      }
    }



    for (Date date : dates) {
      yData.add(hash.getOrDefault(new SimpleDateFormat("M/dd/yyyy").format(date), 0));
    }


    int chartWidth = 640;
    int xDataSize = xData.size();


    if (xDataSize > chartWidth / 10) {
      chartWidth = xDataSize * 8;
    }


    String axisStr = "Videos Watched";

    if (type.equals("likes")) {
      axisStr = "Videos Liked";
    }


    chart = new CategoryChartBuilder().width(chartWidth).height(180)
            .xAxisTitle("Date").yAxisTitle(axisStr).build();
    this.setupStyle();
    chart.getStyler().setXAxisLabelRotation(90);
    Font f = new Font("sanserif", Font.PLAIN, 6);
    Font f2 = new Font("sanserif", Font.BOLD, 12);
    chart.getStyler().setAxisTickLabelsFont(f).setAxisTitleFont(f2).setPlotMargin(3)
            .setChartTitlePadding(5).setChartPadding(4).setPlotContentSize(1);
    chart.addSeries("test", xData, yData);



    int maxVal = Collections.max(yData); // max val found

    List<Integer> yDataMaxVal = new ArrayList<>();
    String maxViewDate = "";

    for (int i = 0; i < yData.size(); i++) {
      if (yData.get(i) < maxVal) {
        yDataMaxVal.add(0);
      }
      else {
        yDataMaxVal.add(yData.get(i));
        maxViewDate = xData.get(i);
      }
    }

    chart.addSeries("maxVal", xData, yDataMaxVal);
    chart.getStyler().setOverlapped(true).setChartTitleVisible(true);
    chart.getStyler().setSeriesColors(new Color[]{Color.BLUE, Color.RED}); // set colors of each bar

    String titleStr = "Watched";

    if (type.equals("likes")) {
      titleStr = "Liked";
    }


    BitmapEncoder.saveBitmapWithDPI(chart, titleStr + "History", BitmapEncoder.BitmapFormat.PNG, 300);

    this.upload(titleStr + "History.png", username);
    titleStr = titleStr.toLowerCase();

    return "Most videos " + titleStr + " in a day: " + maxVal + " on " + maxViewDate;
  }

  /**
   * Creates the month frequency graph.
   * @throws IOException if the file is not found
   */
  public void buildMonthFrequencyGraph(String type) throws IOException {
    List<String> xData = new ArrayList<>();
    List<Integer> yData = new ArrayList<>();

    xData.add("Jan");
    xData.add("Feb");
    xData.add("Mar");
    xData.add("Apr");
    xData.add("May");
    xData.add("Jun");
    xData.add("Jul");
    xData.add("Aug");
    xData.add("Sep");
    xData.add("Oct");
    xData.add("Nov");
    xData.add("Dec");

    Map<String, Integer> hash = new HashMap<>();

    for (String line : data) {  // count the videos
      if (line.indexOf("Date: ") == 0) {


        Date d = null;
        try {
          d = new SimpleDateFormat("yyyy-MM-dd").parse(line.substring(6, 16));
        } catch (ParseException e) {
          e.printStackTrace();
        }

        hash.put(new SimpleDateFormat("MMM").format(d), hash.getOrDefault(new SimpleDateFormat("MMM").format(d), 0) + 1);

      }
    }

    yData.add(hash.get("Jan"));
    yData.add(hash.get("Feb"));
    yData.add(hash.get("Mar"));
    yData.add(hash.get("Apr"));
    yData.add(hash.get("May"));
    yData.add(hash.get("Jun"));
    yData.add(hash.get("Jul"));
    yData.add(hash.get("Aug"));
    yData.add(hash.get("Sep"));
    yData.add(hash.get("Oct"));
    yData.add(hash.get("Nov"));
    yData.add(hash.get("Dec"));

    String axisStr = "Watched";

    if (type.equals("likes")) {
      axisStr = "Liked";
    }

    chart = new CategoryChartBuilder().width(640).height(480)
            .xAxisTitle("Month").yAxisTitle("Videos " + axisStr).title("Most Active Months").build();
    this.setupStyle();
    chart.addSeries("test", xData, yData);


    BitmapEncoder.saveBitmapWithDPI(chart, axisStr + "MonthFrequency", BitmapEncoder.BitmapFormat.PNG, 300);

    this.upload(axisStr + "MonthFrequency.png", username);

  }

  /**
   * Creates the weekday frequency graph.
   *
   * @throws IOException if the file is not found
   */
  public void buildWeekdayFrequencyGraph(String type) throws IOException {
    List<String> xData = new ArrayList<>();
    List<Integer> yData = new ArrayList<>();

    xData.add("Sun");
    xData.add("Mon");
    xData.add("Tues");
    xData.add("Wed");
    xData.add("Thurs");
    xData.add("Fri");
    xData.add("Sat");

    Map<String, Integer> hash = new HashMap<>();

    for (String line : data) {  // count the videos
      if (line.indexOf("Date: ") == 0) {


        Date d = null;
        try {
          d = new SimpleDateFormat("yyyy-MM-dd").parse(line.substring(6, 16));
        } catch (ParseException e) {
          e.printStackTrace();
        }


        hash.put(new SimpleDateFormat("EE").format(d), hash.getOrDefault(new SimpleDateFormat("EE").format(d), 0) + 1);

      }
    }

    yData.add(hash.get("Sun"));
    yData.add(hash.get("Mon"));
    yData.add(hash.get("Tue"));
    yData.add(hash.get("Wed"));
    yData.add(hash.get("Thu"));
    yData.add(hash.get("Fri"));
    yData.add(hash.get("Sat"));

    String axisStr = "Watched";

    if (type.equals("likes")) {
      axisStr = "Liked";
    }

    chart = new CategoryChartBuilder().width(640).height(480)
            .xAxisTitle("Weekday").yAxisTitle("Videos " + axisStr).title("Most Active Days").build();
    this.setupStyle();
    chart.addSeries("test", xData, yData);

    BitmapEncoder.saveBitmapWithDPI(chart, axisStr + "WeekdayFrequency", BitmapEncoder.BitmapFormat.PNG, 300);

    this.upload(axisStr + "WeekdayFrequency.png", username);

  }

  /**
   * Creates the hour frequency graph.
   *
   * @throws IOException if the file is not found
   */
  public void buildHourFrequencyGraph(String type, TimeZone timezone) throws IOException {
    List<String> xData = new ArrayList<>();
    List<Integer> yData = new ArrayList<>();

    for (int i = 0; i < 24; i++) {  // add hour as x axis
      if (i < 10) {
        xData.add("0" + i);
      }
      else {
        xData.add(Integer.toString(i));
      }
    }

    Map<String, Integer> hash = new HashMap<>();

    for (String line : data) {  // count the videos
      if (line.indexOf("Date: ") == 0) {
        hash.put(line.substring(17, 19), hash.getOrDefault(line.substring(17, 19), 0) + 1);
      }
    }


    for (int i = 0; i < 24; i++) { // store video counts in yData
      if (i < 10) {
        yData.add(hash.get("0" + i));
      }
      else {
        yData.add(hash.get(Integer.toString(i)));
      }
    }


    int timeDifference = (int) TimeUnit.MILLISECONDS.toHours(timezone.getRawOffset());

    if (timeDifference < 0) {
      for (int i = 0; i < (timeDifference * -1); i++) { // rotate hour backwards
        yData.add(yData.remove(0));
      }
    }
    else if (timeDifference > 0) {
      for (int i = 0; i < timeDifference; i++) { // rotate hour forwards
        yData.add(yData.remove(0));
      }
    }



    String axisStr = "Watched";

    if (type.equals("likes")) {
      axisStr = "Liked";
    }

    chart = new CategoryChartBuilder().width(640).height(480)
            .xAxisTitle("Time Of Day").yAxisTitle("Videos " + axisStr).title("Most Active Hours").build();
    this.setupStyle();
    chart.addSeries("test", xData, yData);


    BitmapEncoder.saveBitmapWithDPI(chart, axisStr + "HourFrequency", BitmapEncoder.BitmapFormat.PNG, 300);
    this.upload(axisStr + "HourFrequency.png", username);


  }

  /*
  public void buildAuthors() throws IOException {


    Map<String, Integer> hash = new HashMap<>();

    for (String line : data) {

      if (line.indexOf("Video Link:") == 0) {
        String link = line.substring(12);

        Document doc = Jsoup.connect(link).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").maxBodySize(0).followRedirects(true).get();


        Elements title = doc.getElementsByTag("link");
        Element titleLine = null;

        for (Element e : title) {
          if (e.toString().contains("rel=\"canonical\"")) {
            titleLine = e;


            System.out.println(titleLine.toString());
            String fullLink = titleLine.attr("href");

            String authorName = fullLink.substring(fullLink.indexOf("@"), fullLink.indexOf("/video/"));
            System.out.println(authorName);

            hash.put(authorName, hash.getOrDefault(authorName, 0) + 1);
          }
        }

      }
    }


    Map<String, Integer> sortedAuthors = this.sortByValue(hash);

    List<String> xData = new ArrayList<>(sortedAuthors.keySet());
    List<Integer> yData = new ArrayList<>(sortedAuthors.values());

    chart = new CategoryChartBuilder().width(640).height(480)
            .xAxisTitle("TikTokers").yAxisTitle("Videos Watched").build();
    this.setupStyle();
    chart.addSeries("test", xData, yData);




    BitmapEncoder.saveBitmapWithDPI(chart, "TikTokerFrequency", BitmapEncoder.BitmapFormat.PNG, 300);
    this.upload("TikTokerFrequency.png", username);

  }


   */

  private static HashMap<String, Integer> sortByValue(Map<String, Integer> hm)
  {
    // Create a list from elements of HashMap
    List<Map.Entry<String, Integer> > list =
            new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

    // Sort the list
    Collections.sort(list, (o1, o2) -> (o1.getValue()).compareTo(o2.getValue()));

    // put data from sorted list to hashmap
    HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
    for (Map.Entry<String, Integer> aa : list) {
      temp.put(aa.getKey(), aa.getValue());
    }
    return temp;
  }



}
