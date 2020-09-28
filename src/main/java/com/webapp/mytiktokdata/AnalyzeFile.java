package com.webapp.mytiktokdata;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.webapp.mytiktokdata.storage.StorageService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main class to analyze the given file.
 */
public class AnalyzeFile {

    private final MultipartFile file;
    private final RedirectAttributes redirectAttributes;
    private final TimeZone timezone;
    private final StorageService storageService;
    private String username;

    /**
     * Contructs an AnalyzeFile and parses the data.
     *
     * @param file               input data file
     * @param redirectAttributes attributes on data.html
     * @param timezone           timezone of user
     * @param storageService     file storage
     * @throws IOException     if the file is invalid
     * @throws GeoIp2Exception if the ip can't be parsed
     */
    public AnalyzeFile(MultipartFile file, RedirectAttributes redirectAttributes,
                       TimeZone timezone, StorageService storageService) throws IOException, GeoIp2Exception {
        this.file = file;
        this.redirectAttributes = redirectAttributes;
        this.timezone = timezone;
        this.storageService = storageService;

        this.parseFile();
    }

    /**
     * Checks if the file is valid.
     *
     * @return if the file is valid
     */
    private boolean isValidFile() {
        if (file.getSize() == 0) {
            redirectAttributes.addFlashAttribute("message",
                    "File must not be empty!");
            System.out.println("File was empty");
            redirectAttributes.addFlashAttribute("errormsg", "File can't be empty!");
            return false;
        } else if (!(FilenameUtils.getExtension(file.getOriginalFilename()).equals("zip"))) {
            redirectAttributes.addFlashAttribute("message",
                    "File must be a .zip!");
            System.out.println("File was not a zip");
            redirectAttributes.addFlashAttribute("errormsg", "File must be a zip!");
            return false;
        }

        return true;
    }

    /**
     * Unzips the file.
     *
     * @return the list of files unzipped
     * @throws IOException if the file cannot be found
     */
    private File[] extractFiles() throws IOException {
        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream o = new FileOutputStream(zip);
        IOUtils.copy(file.getInputStream(), o);
        o.close();

        String destination = "D:\\tiktokdatastorage";
        try {
            ZipFile zipFile = new ZipFile(zip);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        } finally {
            zip.delete();
        }

        File f = new File(destination);
        return f.listFiles();
    }

    /**
     * Analyze the Activity folder.
     *
     * @param child activity folder
     * @throws IOException     if the file cannot be found
     * @throws GeoIp2Exception if the plotting fails
     */
    private void analyzeActivity(File child) throws IOException, GeoIp2Exception {
        File[] activityList = child.listFiles();

        assert activityList != null;
        for (File textFiles : activityList) {

            if (textFiles.getName().equals("Video Browsing History.txt")) {
                redirectAttributes.addFlashAttribute("videoswatched",
                        "Videos watched: " + this.count(textFiles.getAbsolutePath()) / 3);

                List<String> data;  // get file as list of strings
                try (Stream<String> lines = Files.lines(Paths.get(textFiles.getPath()))) {
                    data = lines.collect(Collectors.toList());
                }


                GraphCreator graph = new GraphCreator(data, username);
                graph.buildHourFrequencyGraph("views", timezone);
                graph.buildWeekdayFrequencyGraph("views");
                graph.buildMonthFrequencyGraph("views");
                String mostWatchedDay = graph.buildHistory("views");
                //graph.buildAuthors();  way too slow to be useful

                redirectAttributes.addFlashAttribute("mostWatchedDay", mostWatchedDay);

                redirectAttributes.addFlashAttribute("viewHistoryLink",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_WatchedHistory.png");
                redirectAttributes.addFlashAttribute("viewWeekdayLink",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_WatchedWeekdayFrequency.png");
                redirectAttributes.addFlashAttribute("viewMonthLink",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_WatchedMonthFrequency.png");
                redirectAttributes.addFlashAttribute("viewHourLink",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_WatchedHourFrequency.png");

            } else if (textFiles.getName().equals("Like List.txt")) {

                List<String> data;  // get file as list of strings
                try (Stream<String> lines = Files.lines(Paths.get(textFiles.getPath()))) {
                    data = lines.collect(Collectors.toList());
                }


                GraphCreator graph = new GraphCreator(data, username);
                graph.buildHourFrequencyGraph("likes", timezone);
                graph.buildWeekdayFrequencyGraph("likes");
                graph.buildMonthFrequencyGraph("likes");
                String mostLikedDay = graph.buildHistory("likes");

                redirectAttributes.addFlashAttribute("mostLikedDay", mostLikedDay);

                redirectAttributes.addFlashAttribute("likeHistoryLink",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_LikedHistory.png");
                redirectAttributes.addFlashAttribute("likeWeekdayLink",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_LikedWeekdayFrequency.png");
                redirectAttributes.addFlashAttribute("likeMonthLink",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_LikedMonthFrequency.png");
                redirectAttributes.addFlashAttribute("likeHourLink",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_LikedHourFrequency.png");


                redirectAttributes.addFlashAttribute("likedvideos",
                        "Videos liked: " + this.count(textFiles.getAbsolutePath()) / 3);
            } else if (textFiles.getName().equals("Follower List.txt")) {
                redirectAttributes.addFlashAttribute("followers",
                        "Followers: " + this.count(textFiles.getAbsolutePath()) / 3);
            } else if (textFiles.getName().equals("Following List.txt")) {
                redirectAttributes.addFlashAttribute("following",
                        "Following: " + this.count(textFiles.getAbsolutePath()) / 3);
            } else if (textFiles.getName().equals("Login History.txt")) {

                List<String> data;  // get file as list of strings
                try (Stream<String> lines = Files.lines(Paths.get(textFiles.getPath()))) {
                    data = lines.collect(Collectors.toList());
                }

                MapPlotter map = new MapPlotter(data, username);
                map.plotIPs();

                redirectAttributes.addFlashAttribute("ipMap",
                        "https://mytiktokdatastorage.blob.core.windows.net/data/" + username + "_IPMap.png");

                redirectAttributes.addFlashAttribute("appopens",
                        "TikTok launches: " + this.count(textFiles.getAbsolutePath()) / 8);
            } else if (textFiles.getName().equals("Hashtag.txt")) {
                redirectAttributes.addFlashAttribute("hashtags",
                        "Hashtags used: " + this.count(textFiles.getAbsolutePath()) / 3);
            }

        }
    }

    /**
     * Analyze the Comments folder.
     *
     * @param child comments folder
     * @throws IOException if the file cannot be found
     */
    private void analyzeComments(File child) throws IOException {
        File[] activityList = child.listFiles();

        assert activityList != null;
        for (File textFiles : activityList) {


            if (textFiles.getName().equals("Comments.txt")) {
                redirectAttributes.addFlashAttribute("commentsmade",
                        "Comments made: " + this.count(textFiles.getAbsolutePath()) / 3);
            }
        }
    }

    /**
     * Analyze the Chat History folder.
     *
     * @param child Comments folder
     * @throws IOException if the file cannot be found
     */
    private void analyzeMessages(File child) throws IOException {
        File[] activityList = child.listFiles();

        assert activityList != null;
        for (File textFiles : activityList) {


            if (textFiles.getName().equals("Chat History.txt")) {
                redirectAttributes.addFlashAttribute("directmessages",
                        "Direct messages: " + this.count(textFiles.getAbsolutePath()) / 3);
            }
        }
    }

    /**
     * Analyze the Settings folder.
     *
     * @param child settings folder
     * @throws IOException if the file cannot be found
     */
    private void analyzeSettings(File child) throws IOException {
        File[] activityList = child.listFiles();

        assert activityList != null;
        for (File textFiles : activityList) {


            if (textFiles.getName().equals("Block List.txt")) {
                redirectAttributes.addFlashAttribute("blockedusers",
                        "Blocked users: " + this.count(textFiles.getAbsolutePath()) / 3);
            }
        }
    }

    /**
     * Analyze the Profile folder.
     *
     * @param child profile folder
     * @throws IOException if the file cannot be found
     */
    private void analyzeProfile(File child) throws IOException {
        File[] profileFiles = child.listFiles();

        assert profileFiles != null;
        for (File textFiles : profileFiles) {


            if (textFiles.getName().equals("Profile Information.txt")) {

                List<String> lines = Files.readAllLines(textFiles.toPath());

                for (String line : lines) {
                    if (line.contains("Username:")) {
                        redirectAttributes.addFlashAttribute("username",
                                "Username: " + line.substring(10));
                    } else if (line.contains("Name:")) {
                        redirectAttributes.addFlashAttribute("name",
                                "Name: " + line.substring(6));
                    } else if (line.contains("Telephone Number:")) {
                        redirectAttributes.addFlashAttribute("phonenumber",
                                "Phone Number: " + line.substring(18));
                    } else if (line.contains("Email Address:")) {
                        redirectAttributes.addFlashAttribute("email",
                                "Email: " + line.substring(15));
                    } else if (line.contains("Birthdate:")) {
                        redirectAttributes.addFlashAttribute("birthday",
                                "Birthday: " + line.substring(11));
                    } else if (line.contains("Profile Photo:")) {
                        redirectAttributes.addFlashAttribute("profilePic", line.substring(15));
                    }
                }
            }
        }
    }

    /**
     * Analyze the Videos folder.
     *
     * @param child videos folder
     * @throws IOException if the file cannot be found
     */
    private void analyzeVideos(File child) throws IOException {
        File[] activityList = child.listFiles();

        assert activityList != null;
        for (File textFiles : activityList) {


            if (textFiles.getName().equals("Videos.txt")) {
                redirectAttributes.addFlashAttribute("videosmade",
                        "Videos made: " + this.count(textFiles.getAbsolutePath()) / 3);
            }
        }
    }

    /**
     * Parses the data file and calls the needed methods.
     *
     * @return the redirect link
     * @throws IOException     if the file isnt found
     * @throws GeoIp2Exception if the plotting fails
     */
    public String parseFile() throws IOException, GeoIp2Exception {

        if (this.isValidFile()) {

            storageService.store(file);
            File[] filesList = Objects.requireNonNull(this.extractFiles());
            this.getUsername(filesList);

            for (File child : filesList) {
                String fileName = child.getName();

                switch (fileName) {
                    case ("Activity"):
                        this.analyzeActivity(child);
                        break;
                    case ("Comments"):
                        this.analyzeComments(child);
                        break;
                    case ("Direct Messages"):
                        this.analyzeMessages(child);
                        break;
                    case ("App Settings"):
                        this.analyzeSettings(child);
                        break;
                    case ("Profile"):
                        this.analyzeProfile(child);
                        break;
                    case ("Videos"):
                        this.analyzeVideos(child);
                        break;
                    default:
                        System.out.println("Unknown file: " + fileName);
                }

            }

            return "redirect:/data";
        } else {
            return "redirect:/error";
        }
    }

    /**
     * Gets the username from the profile information file.
     *
     * @param files unzipped files to be searched
     * @throws IOException if the file is not found
     */
    private void getUsername(File[] files) throws IOException {

        for (File f : files) {
            if (f.getName().equals("Profile")) {


                File[] activityList = f.listFiles();

                assert activityList != null;
                for (File textFiles : activityList) {

                    if (textFiles.getName().equals("Profile Information.txt")) {

                        List<String> lines = Files.readAllLines(textFiles.toPath());

                        for (String line : lines) {
                            if (line.contains("Username:")) {
                                this.username = line.substring(10);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Counts the lines in the file.
     *
     * @param filename File to be counted
     * @return the number of lines
     * @throws IOException if the file is not found
     */
    private int count(String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n');
            }
            if (endsWithoutNewLine) {
                ++count;
            }
            return count;
        }
    }

}
