import com.webapp.mytiktokdata.FileUploadController;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class FileUploadControllerTest {

    @Test
    public void testCount() throws IOException {
     String dir = "D:\\tiktokdatastorage\\Activity\\Video Browsing History.txt";
     FileUploadController c = new FileUploadController(null);
     //assertEquals(68004, c.count(dir));
    }

}