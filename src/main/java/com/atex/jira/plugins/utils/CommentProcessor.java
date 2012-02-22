package com.atex.jira.plugins.utils;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.jira.plugins.configs.Configuration;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.JiraKeyUtils;

/**
*
* @author wkuo
*/
public class CommentProcessor {

    final Logger logger = LoggerFactory.getLogger(CommentProcessor.class);
    private static Configuration configuration;

    public CommentProcessor(Configuration configuration) {
        setConfiguration(configuration);
    }
    
    public static void setConfiguration(Configuration configuration) {
        CommentProcessor.configuration = configuration;
    }
    
    public Configuration getConfiguration() {
        return CommentProcessor.configuration;
    }
    
    private User getUser() {
        String backendUsername = configuration.getCommentUserId();
        User user = null;
        UserUtil userUtil = ComponentManager.getInstance().getUserUtil();
        if (userUtil!=null && backendUsername!=null) {
            user = userUtil.getUserObject(backendUsername);
        }
        return user;
    }
   
    private Set<String> getIssuesKeyFromContent(String content) {
        Set<String> issueSet = new HashSet<String>();
        List<String> issues = new ArrayList<String>();
        if (JiraKeyUtils.isKeyInString(content)) {
            issues = JiraKeyUtils.getIssueKeysFromString(content);
        } else {
            logger.info("No key found from comment " + content);
            return issueSet;
        }
        for (String issue : issues) {
            issueSet.add(issue);
        }
        logger.info("Issue keys " + issues.toString() + " found");
        return issueSet;
    }
    
    private void addComment(String issueId, String content) {
        IssueManager issueManager = ComponentManager.getInstance().getIssueManager();
        User user = getUser();
        String userName = "";
        try {
            if (user!=null) {
                userName = user.toString();
                MutableIssue issue = issueManager.getIssueObject(issueId);
                CommentManager commManager = ComponentManager.getInstance().getCommentManager();
                Comment comment;
                comment = commManager.create(issue, userName, content, true);
                commManager.update(comment, true);
            } else {
                logger.warn("Comment User not found");
            }
        } catch (Exception e) {
            logger.error("Error while adding comment" + e.getMessage());
        }
        
    }
    
    
    private String constructGitWebUrl(String repo, String commitId) {
        String gitWebServer = configuration.getGitWebURL();
        StringBuilder sb = new StringBuilder() ;
        if (gitWebServer!=null && !gitWebServer.trim().isEmpty() && repo!=null && commitId!=null) {
            sb.append(gitWebServer);
            sb.append("?");
            sb.append("p=");
            sb.append(repo);
            sb.append(";a=commitdiff;h=");
            sb.append(commitId);
        }
        return sb.toString();
    }
   
    private String formCommentStream(Map<String, String> commit) {
        StringBuilder sb = new StringBuilder();
        String commitId = commit.get(GitJsonUtil.FIELD_COMMIT_ID);
        String repoName = commit.get(GitJsonUtil.FIELD_REPO_NAME);
        sb.append("|| Revision || Repo || Branch || Author || \n");
        sb.append(" | ");
        sb.append(commitId);
        sb.append(" ");
        sb.append(StringUtil.parseUrlLabel("GitHub", commit.get(GitJsonUtil.FIELD_COMMIT_URL)));
        sb.append(" ");
        sb.append(StringUtil.parseUrlLabel("GitWeb", constructGitWebUrl(repoName, commitId)));
        sb.append(" | ");
        sb.append(commit.get(GitJsonUtil.FIELD_REPO_NAME));
        sb.append(" | ");
        sb.append(commit.get(GitJsonUtil.FIELD_BRANCH));
        sb.append(" | ");
        sb.append(commit.get(GitJsonUtil.FIELD_COMMIT_AUTHOR));
        sb.append(" | \n");
        sb.append(commit.get(GitJsonUtil.FIELD_COMMIT_MESSAGE));
        sb.append("\n");
        return sb.toString();
    }
    
    private void addComments(Map<String, String> commit, String rawContent) {
        for (String issueId : getIssuesKeyFromContent(rawContent)) {
            String content = formCommentStream(commit);
            addComment(issueId, content);
        }
    }
    
    public void processCommits(List<Map<String, String>> commits) {
        for (Map<String, String> commit: commits) {
            addComments(commit, commit.get(GitJsonUtil.FIELD_COMMIT_MESSAGE));
        }
    }
    
    
    
    
    
    
    
    
    
}
