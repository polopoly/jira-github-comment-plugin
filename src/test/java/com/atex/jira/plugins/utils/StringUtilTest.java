package com.atex.jira.plugins.utils;

import junit.framework.TestCase;

import org.junit.Test;

public class StringUtilTest extends TestCase {

    @Test
    public void testGetLastStrAftSlashNullBlankSpace() {
        assertNotNull(StringUtil.getLastStrAftSlash(null));
        assertNotNull(StringUtil.getLastStrAftSlash(""));
        assertNotNull(StringUtil.getLastStrAftSlash(" "));        
        assertEquals("", StringUtil.getLastStrAftSlash(null));
        assertEquals("", StringUtil.getLastStrAftSlash(""));
        assertEquals("", StringUtil.getLastStrAftSlash(" "));
    }
    
    @Test
    public void testGetLastStrAftSlashWithContents() {
        assertEquals("master", StringUtil.getLastStrAftSlash("refs/heads/master"));
        assertEquals("refsheadsmaster", StringUtil.getLastStrAftSlash("refsheadsmaster"));
        assertEquals("", StringUtil.getLastStrAftSlash("refsheadsmaster/"));
    }
    
    @Test
    public void testParseStringNullBlankSpace() {
        assertNotNull(StringUtil.parseString(null));
        assertNotNull(StringUtil.parseString(""));
        assertNotNull(StringUtil.parseString(" "));
        assertEquals("", StringUtil.parseString(null));
        assertEquals("", StringUtil.parseString(""));
        assertEquals("", StringUtil.parseString(" "));        
    }

    @Test
    public void testParseStringWithContents() {
        assertNotNull(StringUtil.parseString(" ab c "));
        assertNotNull(StringUtil.parseString("abc"));
    }

    @Test
    public void testParseUrlLabelNullBlankSpace() {
        assertNotNull(StringUtil.parseUrlLabel(null, null));
        assertNotNull(StringUtil.parseUrlLabel(null, ""));
        assertNotNull(StringUtil.parseUrlLabel("", null));
        assertNotNull(StringUtil.parseUrlLabel("", ""));
        assertNotNull(StringUtil.parseUrlLabel(" ", " "));
        assertEquals("", StringUtil.parseUrlLabel(null, null));
        assertEquals("", StringUtil.parseUrlLabel(null, ""));
        assertEquals("", StringUtil.parseUrlLabel("", null));
        assertEquals("", StringUtil.parseUrlLabel("", ""));
        assertEquals("", StringUtil.parseUrlLabel(" ", " "));
    }
    
    @Test
    public void testParseUrlLabelWithContents() {
        assertEquals("abc", StringUtil.parseUrlLabel("abc", ""));
        assertEquals("www.google.com", StringUtil.parseUrlLabel("", "www.google.com"));
        assertEquals("[google|www.google.com]", StringUtil.parseUrlLabel("google", "www.google.com"));
        
    }
    
}
