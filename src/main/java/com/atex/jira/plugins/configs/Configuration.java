package com.atex.jira.plugins.configs;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.auth.AuthScope;

public class Configuration {

    private String cropPrefix;
    private String commentUserId;
    private String secretKey;
    private String gitWebServer;
    private boolean activate = false;
    private URL gitWebURL;
    
    public static final String NAME_SPACE = Configuration.class.getName() + ".";
    public static final String CROP_PREFIX = NAME_SPACE + "cropPrefix";
    public static final String COMMENT_USER_ID = NAME_SPACE + "commentUserId";
    public static final String SECRET_KEY = NAME_SPACE + "secretKey";
    public static final String GIT_WEB_SERVER = NAME_SPACE + "gitWebServer";
    public static final String ACTIVATE = NAME_SPACE + "activate";    
    
    public Configuration() {
    }

    public Configuration(String cropPrefix, String commentUserId, 
            String secretKey, String gitWebServer,
            boolean activate) throws MalformedURLException {
        this.cropPrefix = cropPrefix;
        this.commentUserId = commentUserId;
        this.secretKey = secretKey;
        this.gitWebServer = gitWebServer;
        this.gitWebURL = toURL(gitWebServer);
        this.activate = activate;
    }

    public AuthScope getAuthScope() {
        return new AuthScope(gitWebURL.getHost(), gitWebURL.getPort() == -1? 80: gitWebURL.getPort());
    }
    
    public String getCropPrefix() {
        return cropPrefix;
    }
    public void setCropPrefix(String cropPrefix) {
        this.cropPrefix = cropPrefix;
    }
    public String getCommentUserId() {
        return commentUserId;
    }
    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    public String getGitWebServer() {
        return gitWebServer;
    }
    public void setGitWebServer(String gitWebServer) {
        this.gitWebServer = gitWebServer;
    }
    public String getGitWebURL() {
        return gitWebServer;
    }
    public void setGitWebURL(String gitWebServer) throws MalformedURLException {
        this.gitWebServer = gitWebServer;
        this.gitWebURL = toURL(gitWebServer);
    }
    public boolean isActivate() {
        return activate;
    }
    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    private URL toURL(String url) throws MalformedURLException {
        if (url==null)
            return null;
        if (url.trim().isEmpty())
            return null;
        URL result = new URL(url);
        return result;
    }
}
