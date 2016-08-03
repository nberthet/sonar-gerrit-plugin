package org.jenkinsci.plugins.sonargerrit.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 22.09.2015 11:55
 *
 * $Id$
 */
public final class Localization {

    public static final String MESSAGES = "messages";

    private Localization() {
    }

    public static String getLocalized(String s){
        ResourceBundle messages = ResourceBundle.getBundle(MESSAGES);
        return messages.getString(s);
    }

    public static String getLocalized(String s, Object... params){
        String string = getLocalized(s);
        return String.format(string, params);
    }

    public static String getLocalized(String s, Locale l){
        ResourceBundle messages = ResourceBundle.getBundle(MESSAGES, l);
        return messages.getString(s);
    }
}
