
import com.webapp.mytiktokdata.VideoGrabber;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VideoGrabberTest {

  List<String> data = new ArrayList<>();

  @Test
  public void testGrabber() throws IOException {
    data.add("Video Link: https://www.tiktokv.com/share/video/6861001352768769286/");
    assertEquals("https://www.tiktokv.com/share/video/6861001352768769286/", data.get(0).substring(12));
    VideoGrabber v = new VideoGrabber(data);
    v.analyze();
  }


}