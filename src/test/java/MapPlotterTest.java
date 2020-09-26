import com.webapp.mytiktokdata.MapPlotter;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapPlotterTest {


  @Test
  public void test() throws IOException, GeoIp2Exception {

    List<String> data;


    try (Stream<String> lines = Files.lines(Paths.get("D:\\tiktokdatastorage\\Activity\\Login History.txt"))) {
      data = lines.collect(Collectors.toList());
    }

    MapPlotter map = new MapPlotter(data, "zneatz");
    map.plotIPs();



  }

}