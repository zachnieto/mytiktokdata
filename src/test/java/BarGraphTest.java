import com.webapp.mytiktokdata.GraphCreator;

import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BarGraphTest {

  @Test
  public void test2() {
    assertEquals("https://p16-amd-va.tiktokcdn.com/img/musically-maliva-obj/36df31e15b1f83fa1c5fed0236f68576~c5_1080x1080.webp", "Profile Photo: https://p16-amd-va.tiktokcdn.com/img/musically-maliva-obj/36df31e15b1f83fa1c5fed0236f68576~c5_1080x1080.webp".substring(15));
  }


  @Test
  public void test() throws IOException {
    List<String> l = new ArrayList<>();
    l.add("Date: 2020-08-13 14:43:52");
    l.add("Video Link: https://www.tiktokv.com/share/video/6855906921459895557/");
    l.add("");

    //assertEquals(0, "Date: 2020-08-13 14:43:52".indexOf("Date: "));
    //assertEquals("14", "Date: 2020-08-13 14:43:52".substring(17, 19));
    //assertEquals("2020-08-13", "Date: 2020-08-13 14:43:52".substring(6, 16));


    List<String> data;  // get file as list of strings

    try (Stream<String> lines = Files.lines(Paths.get("D:\\tiktokdatastorage\\Activity\\Video Browsing History.txt"))) {
      data = lines.collect(Collectors.toList());
    }


    GraphCreator b = new GraphCreator(data, "zneatz");
    b.buildHistory("views");
    //b.buildHourFrequencyGraph("views");
    //b.buildMonthFrequencyGraph("views");
    //b.buildWeekdayFrequencyGraph("views");


    try (Stream<String> lines = Files.lines(Paths.get("D:\\tiktokdatastorage\\Activity\\Like List.txt"))) {
      data = lines.collect(Collectors.toList());
    }

    //b = new GraphCreator(data, "zneatz");
    //b.buildHistory("likes");
    //b.buildHourFrequencyGraph("likes");
    //b.buildMonthFrequencyGraph("likes");
    //b.buildWeekdayFrequencyGraph("likes");


  }

}