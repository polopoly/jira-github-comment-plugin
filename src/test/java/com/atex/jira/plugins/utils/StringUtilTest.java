package com.atex.jira.plugins.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.atex.jira.plugins.utils.StringUtil;

public class StringUtilTest {
    private StringUtil test;

    @Before
    public void setUp() throws Exception {
        test = new StringUtil();
    }

    @Test
    public void testGetLastStrAftSlash() {
        assertEquals("master", test.getLastStrAftSlash("refs/heads/master"));
        assertEquals("refsheadsmaster", test.getLastStrAftSlash("refsheadsmaster"));
        assertEquals("", test.getLastStrAftSlash("refsheadsmaster/"));
        assertEquals("", test.getLastStrAftSlash(""));
        assertEquals("", test.getLastStrAftSlash(" "));
    }
    
    @Test
    public void testParseString() {
        assertNotNull(test.parseString(null));
        assertNotNull(test.parseString(""));
        assertNotNull(test.parseString(" "));
        assertNotNull(test.parseString("abc"));
        assertNotNull(test.parseString(" ab c "));
    }

}
