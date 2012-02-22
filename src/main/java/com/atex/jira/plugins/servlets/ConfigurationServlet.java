package com.atex.jira.plugins.servlets;

import static com.atex.jira.plugins.configs.Configuration.ACTIVATE;
import static com.atex.jira.plugins.configs.Configuration.COMMENT_USER_ID;
import static com.atex.jira.plugins.configs.Configuration.CROP_PREFIX;
import static com.atex.jira.plugins.configs.Configuration.GIT_WEB_SERVER;
import static com.atex.jira.plugins.configs.Configuration.SECRET_KEY;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atex.jira.plugins.configs.Configuration;
import com.atex.jira.plugins.configs.ConfigurationReader;
import com.atex.jira.plugins.utils.CommentProcessor;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

/**
 *
 * @author wkuo
 */
public class ConfigurationServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 3132823194905563507L;
    private static final String CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String VIEW = "configure.vm";
    
    private transient final UserManager userManager;
    private transient final LoginUriProvider loginUriProvider;
    private transient final TemplateRenderer renderer;
    private transient final PluginSettingsFactory pluginSettingsFactory;
    private transient final TransactionTemplate transactionTemplate;
    
    public ConfigurationServlet(TemplateRenderer renderer, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate, UserManager userManager, LoginUriProvider loginUriProvider) {
        this.renderer = renderer;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = userManager.getRemoteUsername(req);
        if (username != null && !userManager.isSystemAdmin(username)) {
            redirectToMain(req, resp);
            return;
        } else if (username==null) {
            redirectToLogin(req, resp);
            return;            
        }
        Configuration configuration = transactionTemplate.execute(new ConfigurationReader(pluginSettingsFactory));
        Map<String, Object> models = new HashMap<String, Object>();
        models.put("configuration", configuration);
        resp.setContentType(CONTENT_TYPE);
        renderer.render(VIEW, models, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String cropPrefix = req.getParameter("cropPrefix");
        final String commentUserId = req.getParameter("commentUserId");
        final String secretKey = req.getParameter("secretKey");
        final String gitWebServer = req.getParameter("gitWebServer");
        final Boolean activate = Boolean.valueOf(req.getParameter("activate"));
        
        Map<String, Object> models = new HashMap<String, Object>();

        if (!isValid(commentUserId)) {
            models.put("commentUserId", "error");
        }
        if (!isValid(secretKey)) {
            models.put("secretKey", "error");
        }

        Configuration configuration = null;
        try {
            configuration = new Configuration();
            configuration.setCropPrefix(cropPrefix);
            configuration.setCommentUserId(commentUserId);
            configuration.setSecretKey(secretKey);
            if (gitWebServer!=null && !gitWebServer.trim().isEmpty()) {
                configuration.setGitWebURL(gitWebServer);
            }
            configuration.setActivate(activate);
        } catch (MalformedURLException e) {
            models.put("gitWebServer", "error");
        }
        // some error occured
        if (!models.isEmpty()) {
            resp.setContentType(CONTENT_TYPE);
            // add to models for display to avoid re-input data
            models.put("configuration", configuration);
            renderer.render(VIEW, models, resp.getWriter());
        } else {

            // do save operation
            transactionTemplate.execute(new TransactionCallback<Void>() {

                @Override
                public Void doInTransaction() {
                    PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
                    pluginSettings.put(CROP_PREFIX, cropPrefix);
                    pluginSettings.put(COMMENT_USER_ID, commentUserId);
                    pluginSettings.put(SECRET_KEY, secretKey);
                    pluginSettings.put(GIT_WEB_SERVER, gitWebServer);
                    pluginSettings.put(ACTIVATE, activate.toString());
                    return null;
                }
            });
            // update to use new configuration
            CommentProcessor.setConfiguration(configuration);
            models.put("configuration", configuration);
            models.put("success", "success");
            resp.setContentType(CONTENT_TYPE);
            renderer.render(VIEW, models, resp.getWriter());
        }
    }
    
    private boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    private void redirectToMain(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(loginUriProvider.getLoginUri(URI.create("")).toASCIIString());
    }
   
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(loginUriProvider.getLoginUri(getUri(req)).toASCIIString());
    }

    private URI getUri(HttpServletRequest req) {
        StringBuffer builder = req.getRequestURL();
        if (req.getQueryString() != null) {
            builder.append("?");
            builder.append(req.getQueryString());
        }
        return URI.create(builder.toString());
    }
    
}
