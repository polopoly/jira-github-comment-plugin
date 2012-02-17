package com.atex.jira.plugins.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.jira.plugins.utils.GitJsonUtil;
import com.atex.jira.plugins.utils.SignatureUtil;

public class GitServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    final Logger logger = LoggerFactory.getLogger(GitServlet.class.getName());
    private static final String FIELD_SIGNATURE = "X-Hub-Signature";
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(req.getInputStream(), Charset.forName("UTF-8")));
            String text = readAll(rd);
            String genSignature = SignatureUtil.calculateRFC2104HMAC(text, "abc");
            String recSignature = "";
            String signatureHeader = null;
            signatureHeader = req.getHeader(FIELD_SIGNATURE);
            if (signatureHeader!=null && signatureHeader.length()>5) {
                recSignature = signatureHeader.substring(5);
            }
            List<Map<String, String>> commits = new ArrayList<Map<String, String>>();
            if (recSignature.equals(genSignature)) {
                GitJsonUtil jsonUtil = new GitJsonUtil(text);
                commits = jsonUtil.getCommits();
            }
//            Add Comment to Jira
            if (!commits.isEmpty()) {

            }
        
        } catch (SignatureException e) {
            logger.error("Signature Exception " + e);
        }
        super.doPost(req, resp);
    }
    
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
      }

}
