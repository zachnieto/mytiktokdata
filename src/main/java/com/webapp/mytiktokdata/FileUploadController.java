package com.webapp.mytiktokdata;


import java.io.IOException;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.webapp.mytiktokdata.storage.StorageService;
import com.maxmind.geoip2.exception.GeoIp2Exception;

/**
 * Handles file uploads and extracts the data from the zip, along with
 * sending the data to the GraphCreator for the charts to be made.
 */
@Controller
public class FileUploadController {

  private final StorageService storageService;

  /**
   * Constructs the controller.
   *
   * @param storageService File storage service
   */
  @Autowired
  public FileUploadController(StorageService storageService) {
    this.storageService = storageService;
  }

  /**
   * Shows the home page.
   *
   * @return the home page
   */
  @GetMapping("/")
  public String homePage() {
    return "index";
  }

  /**
   * Shows the data page.
   *
   * @return the data page
   */
  @GetMapping("/data")
  public String showData() {
    return "data";
  }

  /**
   * Shows the example page.
   *
   * @return the example page
   */
  @GetMapping("/example")
  public String showExample() {
    return "example";
  }

  /**
   * Handles the file upload and data analysis.
   *
   * @param file               the data file as a zip
   * @param redirectAttributes attributes to be changed
   * @return redirect to the data page
   * @throws IOException case the file is not found
   */
  @PostMapping("/")
  public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes,
                                 TimeZone timezone) throws IOException, GeoIp2Exception {

    AnalyzeFile fileAnalyzer = new AnalyzeFile(file, redirectAttributes, timezone, storageService);
    return fileAnalyzer.parseFile();
  }




}

