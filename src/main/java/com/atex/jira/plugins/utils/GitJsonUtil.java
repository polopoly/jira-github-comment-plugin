package com.atex.jira.plugins.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GitJsonUtil {

    public static final String ENCODING = "UTF-8";
    private static final String GIT_PUSHER = "pusher";
    private static final String GIT_REPO = "repository";
    private static final String GIT_REPO_NAME = "name";
    private static final String GIT_REPO_REF = "ref";
    private static final String GIT_COMMITS = "commits";
    private static final String GIT_COMMIT_ID = "id";
    private static final String GIT_COMMIT_URL = "url";
    private static final String GIT_COMMIT_MESSAGE ="message";
    private static final String GIT_COMMIT_AUTHOR = "author";
    private static final String GIT_COMMIT_AUTHOR_NAME = "name";
    
    public static final String FIELD_REPO_NAME = "name";
    public static final String FIELD_BRANCH = "branch";
    public static final String FIELD_COMMIT_ID = "id";
    public static final String FIELD_COMMIT_URL = "url";
    public static final String FIELD_COMMIT_MESSAGE ="message";
    public static final String FIELD_COMMIT_AUTHOR = "author";
    
    final Logger logger = LoggerFactory.getLogger(GitJsonUtil.class);    
    
    private JSONObject gitJson;
    
    public GitJsonUtil(String jsonText) {
        JSONParser parser =  new JSONParser();
        try {
            String decoded = URLDecoder.decode(jsonText, ENCODING);
            Object obj = parser.parse(decoded);
            setGitJson((JSONObject) obj);
        } catch (UnsupportedEncodingException e) {
            logger.warn("Please ensure the string is " + ENCODING + "encoding.\n" + e);
        } catch (ParseException e) {
            logger.warn("Unable to parse the string to Json.\n" + e );
        }
    }

    private JSONObject getGitJson() {
        return gitJson;
    }

    private void setGitJson(JSONObject gitJson) {
        this.gitJson = gitJson;
    }

    public JSONObject getPusherObj() {
        Object obj = getGitJson().get(GIT_PUSHER);
        JSONObject result = (JSONObject) obj;
        return result;    
    }
    
    public JSONObject getRepoObj() {
        Object obj = getGitJson().get(GIT_REPO);
        JSONObject result = (JSONObject) obj;
        return result;
    }
    
    public JSONArray getCommitsObject() {
        Object obj = getGitJson().get(GIT_COMMITS);
        JSONArray result = (JSONArray) obj;
        return result;
    }
    
    private String getAuthorString(Object authors) {
        JSONObject authorsJson = (JSONObject) authors;
        String author = (String) authorsJson.get(GIT_COMMIT_AUTHOR_NAME);
        return StringUtil.parseString(author);
    }
    
    public List<Map<String, String>> getCommits() {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        JSONArray commits = getCommitsObject();
        for (Object entry : commits) {
            Map<String, String> commitInfo = new HashMap<String, String>();
            JSONObject commit = (JSONObject) entry ;
            commitInfo.put(FIELD_COMMIT_URL, (String) commit.get(GIT_COMMIT_URL));
            commitInfo.put(FIELD_COMMIT_ID, (String) commit.get(GIT_COMMIT_ID));
            commitInfo.put(FIELD_COMMIT_AUTHOR, getAuthorString(commit.get(GIT_COMMIT_AUTHOR)));
            commitInfo.put(FIELD_COMMIT_MESSAGE, (String) commit.get(GIT_COMMIT_MESSAGE));
            result.add(commitInfo);
        }
        return result;
    }
    
    public Map<String, Object> getPushInfo() {
        Map<String, Object> info = new HashMap<String, Object>();
        JSONObject repo = getRepoObj() ;
        info.put(FIELD_REPO_NAME, repo.get(GIT_REPO_NAME));
        info.put(FIELD_BRANCH, StringUtil.getLastStrAftSlash((String) getGitJson().get(GIT_REPO_REF)));
        return info;
    }
    
}
