package org.jenkinsci.plugins.sonargerrit.data.converter;

import org.jenkinsci.plugins.sonargerrit.data.entity.Issue;
import org.jenkinsci.plugins.sonargerrit.util.Localization;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 12:51
 *
 * $Id$
 */
public class CustomIssueFormatter implements IssueFormatter, TagFormatter<CustomIssueFormatter.Tag> {

    public static final String DEFAULT_ISSUE_COMMENT_TEXT = Localization.getLocalized("jenkins.plugin.default.review.body");

    private Issue issue;
    private String text;
    private String host;

    public CustomIssueFormatter(Issue issue, String text, String host) {
        this.issue = issue;
        this.text = prepareText(text, DEFAULT_ISSUE_COMMENT_TEXT);
        this.host = host;
    }

    private static String prepareText(String text, String defaultValue) {
        return text != null && !text.trim().isEmpty() ? text.trim() : defaultValue;
    }

    @Override
    public String getMessage() {
        String res = text;
        for (Tag tag : Tag.values()) {
            if (res.contains(tag.getName())) {
                res = res.replace(tag.getName(), getValueToReplace(tag));
            }
        }
        return res;
    }

    @Override
    public String getValueToReplace(Tag tag) {
        switch (tag) {
            case KEY:
                return issue.getKey();
            case COMPONENT:
                return issue.getComponent();
            case MESSAGE:
                return issue.getMessage();
            case SEVERITY:
                return issue.getSeverity().name();
            case RULE:
                return issue.getRule();
            case RULE_URL:
                return getRuleLink(issue.getRule());
            case STATUS:
                return issue.getStatus();
            case CREATION_DATE:
                return issue.getCreationDate().toString();
            default:
                return null;
        }
    }

    protected String getRuleLink(String rule) {
        if (host != null) {
            StringBuilder sb = new StringBuilder();
            String url = host.trim();
            if (!(url.startsWith("http://") || host.startsWith("https://"))) {
                sb.append("http://");
            }
            sb.append(url);
            if (!(url.endsWith("/"))) {
                sb.append("/");
            }
            sb.append("coding_rules#rule_key=");
            sb.append(escapeHttp(rule));      // squid%3AS1319
            return sb.toString();
        }
        return rule;
    }


    protected String escapeHttp(String query) {
//        return StringEscapeUtils.escapeHtml(query);     // todo this method does not escape semicolon. but is URLEncoder.encode a correct way to do so?
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return query;
        }
    }

    public enum Tag {
        KEY("<key>"),
        COMPONENT("<component>"),
        MESSAGE("<message>"),
        SEVERITY("<severity>"),
        RULE("<rule>"),
        RULE_URL("<rule_url>"),
        STATUS("<status>"),
        CREATION_DATE("<creation_date>");

        private final String name;

        Tag(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
