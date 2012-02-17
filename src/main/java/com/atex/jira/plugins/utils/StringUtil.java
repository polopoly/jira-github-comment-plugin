package com.atex.jira.plugins.utils;

public class StringUtil {
    
    public static String getLastStrAftSlash(String raw) {
        String result = parseString(raw);
        int start = result.lastIndexOf("/");
        if (start>0) {
            result = raw.substring(start+1);
        }
        return result;
    }
    
    public static String parseString(String raw) {
        if (raw==null) {
            return "";
        }
        return raw.trim();
    }
    
}
