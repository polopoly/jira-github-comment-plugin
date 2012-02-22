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

import com.atex.jira.plugins.configs.Configuration;
import com.atex.jira.plugins.configs.ConfigurationReader;
import com.atex.jira.plugins.utils.CommentProcessor;
import com.atex.jira.plugins.utils.GitJsonUtil;
import com.atex.jira.plugins.utils.SignatureUtil;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class GitServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    final Logger logger = LoggerFactory.getLogger(GitServlet.class.getName());
    private static final String FIELD_SIGNATURE = "X-Hub-Signature";
    
    private transient final PluginSettingsFactory pluginSettingsFactory;
    private transient final TransactionTemplate transactionTemplate;
    
    public GitServlet(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ConfigurationReader configurationReader = new ConfigurationReader(pluginSettingsFactory);
        Configuration configuration = transactionTemplate.execute(configurationReader);
        try {
            if (configuration.isActivate()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(req.getInputStream(), Charset.forName("UTF-8")));
                String text = readAll(rd).trim();
                String secretKey = configuration.getSecretKey();
                String cropPrefix = configuration.getCropPrefix();
                String genSignature = SignatureUtil.calculateRFC2104HMAC(text, secretKey);
                String recSignature = "";
                String signatureHeader = null;
                signatureHeader = req.getHeader(FIELD_SIGNATURE);
                if (signatureHeader!=null && signatureHeader.length()>5) {
                    recSignature = signatureHeader.substring(5);
                }
                List<Map<String, String>> commits = new ArrayList<Map<String, String>>();
                if (recSignature.equals(genSignature)) {
                    GitJsonUtil jsonUtil = new GitJsonUtil(text, cropPrefix);
                    commits = jsonUtil.getCommits();
                }
    //            Add Comment to Jira
                if (!commits.isEmpty()) {
                    CommentProcessor commentProcessor = new CommentProcessor(configuration);
                    commentProcessor.processCommits(commits);
                }
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
