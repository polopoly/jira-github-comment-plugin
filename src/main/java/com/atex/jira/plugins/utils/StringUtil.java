package com.atex.jira.plugins.utils;

/**
*
* @author wkuo
*/
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

    public static String parseUrlLabel(String rawLabel, String rawUrl) {
        StringBuilder result = new StringBuilder();
        String label = parseString(rawLabel);
        String url = parseString(rawUrl);               
        if (label.length() > 0 && url.length() > 0) {
            result.append("[");
            result.append(label);
            result.append("|");
            result.append(url);
            result.append("]");
        } else if (label.length() > 0 && url.length() == 0) {
            result.append(label);
        } else if (label.length() == 0 && url.length() > 0) {
            result.append(url);
        }
        return result.toString();
    }  
}
