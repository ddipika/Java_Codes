/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Set;

/**
 * The model for our notifications. They're made up of the sender's name,
 * the text and a list of tags.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class Notification {
    
    private String name;
    private String text;
    private Set<String> tags;

    public Notification(String name, String text, Set<String> tags) {
        this.name = name;
        this.text = text;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

	// The toString method formats the notification with html
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("<b>").append(name).append(": </b>").append(text);
        result.append("<font color=\"purple\">");
        for(String tag : tags) {
            result.append(" #").append(tag);
        }
        result.append("</font>");
        return result.toString();
    }   
}
