package com.atex.jira.plugins.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.atex.jira.plugins.servlets.GitServlet;
import com.atex.jira.plugins.utils.GitJsonUtil;



import junit.framework.TestCase;

public class GitJsonUtilTest extends TestCase {
    private GitJsonUtil test ;

    @Before
    protected void setUp() {
        String rawText ="%7B%22pusher%22%3A%7B%22name%22%3A%22wkuo%22%2C%22email%22%3A%22wuichia%40gmail.com%22%7D%2C%22repository%22%3A%7B%22name%22%3A%22DummyFirst%22%2C%22size%22%3A260%2C%22has_wiki%22%3Atrue%2C%22created_at%22%3A%222011%2F12%2F15%2000%3A23%3A36%20-0800%22%2C%22watchers%22%3A1%2C%22private%22%3Afalse%2C%22fork%22%3Afalse%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fwkuo%2FDummyFirst%22%2C%22pushed_at%22%3A%222012%2F02%2F14%2003%3A32%3A31%20-0800%22%2C%22open_issues%22%3A0%2C%22has_downloads%22%3Atrue%2C%22homepage%22%3A%22%22%2C%22has_issues%22%3Atrue%2C%22forks%22%3A1%2C%22description%22%3A%22My%20first%20dummy%20project%22%2C%22owner%22%3A%7B%22name%22%3A%22wkuo%22%2C%22email%22%3A%22wuichia%40gmail.com%22%7D%7D%2C%22forced%22%3Afalse%2C%22after%22%3A%22462b53fb81b2129f6ed1b96f03565a62fdc96c39%22%2C%22deleted%22%3Afalse%2C%22ref%22%3A%22refs%2Fheads%2Fmaster%22%2C%22commits%22%3A%5B%7B%22added%22%3A%5B%5D%2C%22modified%22%3A%5B%22newfile.txt%22%5D%2C%22author%22%3A%7B%22name%22%3A%22wkuo%22%2C%22username%22%3A%22wkuo%22%2C%22email%22%3A%22wuichia%40gmail.com%22%7D%2C%22timestamp%22%3A%222012-02-14T03%3A32%3A13-08%3A00%22%2C%22removed%22%3A%5B%5D%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fwkuo%2FDummyFirst%2Fcommit%2F462b53fb81b2129f6ed1b96f03565a62fdc96c39%22%2C%22id%22%3A%22462b53fb81b2129f6ed1b96f03565a62fdc96c39%22%2C%22distinct%22%3Atrue%2C%22message%22%3A%22Compare3%20X-Hub-Signature%22%7D%5D%2C%22before%22%3A%22c699f41a26a4e60e4caf958dc5240a064cb0341f%22%2C%22compare%22%3A%22https%3A%2F%2Fgithub.com%2Fwkuo%2FDummyFirst%2Fcompare%2Fc699f41...462b53f%22%2C%22created%22%3Afalse%7D" ;
        try {
            test = new GitJsonUtil(URLDecoder.decode(rawText, GitJsonUtil.ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testGetCommitMessage() {
        String output;
        List<Map<String, String>> commits = test.getCommits();
        Map<String, String> commit = commits.get(0);
        output = commit.get(GitJsonUtil.FIELD_COMMIT_MESSAGE);
        assertEquals("Compare3 X-Hub-Signature", output);
    }

}
