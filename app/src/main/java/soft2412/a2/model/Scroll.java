package soft2412.a2.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Scroll {
    private int scrollID;
    private String uploadDate;
    private String scrollName;
    private String filename;
    private String uploaderName;
    private User uploader;
    private int downloads;
    public Scroll(int scrollID, String uploadDate, String scrollName, String filename, User user, int downloads) {
        this.scrollID = scrollID;
        this.uploadDate = uploadDate;
        this.scrollName = scrollName;
        this.filename = filename;
        this.uploader = user;
        this.uploaderName = user.getUsername();
        this.downloads = downloads;
    }

    public int getScrollID() {
        return scrollID;
    }
    public String getScrollName() { return scrollName; }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getFilename() {
        return filename;
    }

    public User getUploader() {
        return uploader;
    }

    public int getDownloads() {
        return downloads;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setScrollID(int scrollID) {
        this.scrollID = scrollID;
    }
}
