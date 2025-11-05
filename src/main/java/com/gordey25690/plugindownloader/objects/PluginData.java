package com.gordey25690.plugindownloader.objects;

public class PluginData {
    
    private String name;
    private String author;
    private String version;
    private String description;
    private String source;
    private String downloadUrl;
    private boolean installed;
    
    public PluginData(String name, String author, String version, String description, String source, String downloadUrl) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.description = description;
        this.source = source;
        this.downloadUrl = downloadUrl;
        this.installed = false;
    }
    
    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public boolean isInstalled() { return installed; }
    public void setInstalled(boolean installed) { this.installed = installed; }
}
