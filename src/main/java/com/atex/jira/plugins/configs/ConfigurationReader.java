package com.atex.jira.plugins.configs;

import static com.atex.jira.plugins.configs.Configuration.*;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wkuo
 */
public class ConfigurationReader implements TransactionCallback<Configuration> {

    private PluginSettingsFactory pluginSettingsFactory;
    
    final Logger logger = LoggerFactory.getLogger(ConfigurationReader.class);
    
    public ConfigurationReader(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }
    
    @Override
    public Configuration doInTransaction() {
        try {
            PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
            String cropPrefix = (String) settings.get(CROP_PREFIX);
            String commentUserId = (String) settings.get(COMMENT_USER_ID);
            String secretKey = (String) settings.get(SECRET_KEY);
            String gitWebServer = (String) settings.get(GIT_WEB_SERVER);
            Boolean activate = Boolean.valueOf((String) settings.get(ACTIVATE));
            return new Configuration(cropPrefix, commentUserId, secretKey, gitWebServer, activate);
        } catch (MalformedURLException ex) {
            logger.warn("Url Malformed " + ex);
        } catch (Exception e) {
            
        }
        return null;
    }
}
