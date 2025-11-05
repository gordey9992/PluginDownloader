package com.gordey25690.plugindownloader.objects;

public class DownloadSource {
    
    private String name;
    private String urlTemplate;
    private boolean enabled;
    
    public DownloadSource(String name, String urlTemplate, boolean enabled) {
        this.name = name;
        this.urlTemplate = urlTemplate;
        this.enabled = enabled;
    }
    
    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getUrlTemplate() { return urlTemplate; }
    public void setUrlTemplate(String urlTemplate) { this.urlTemplate = urlTemplate; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String buildUrl(String author, String plugin, String version) {
        return urlTemplate
            .replace("{автор}", author)
            .replace("{плагин}", plugin)
            .replace("{версия}", version);
    }
}
