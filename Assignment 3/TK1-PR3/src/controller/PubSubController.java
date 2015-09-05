/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import gui.MainWindow;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import model.Notification;
import model.Subscriber;

/**
 * The main controlling unit, dispatching work to PubSubClient
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 
 */
public class PubSubController {

    // The current subscriptions
    private Subscriber subscriber;
    // The main window
    private MainWindow mainwindow;
    // The pub sub client
    private PubSubClient client;
	// The username
    private String username;

    public PubSubController(MainWindow mainwindow, String un) {
        this.mainwindow = mainwindow;
        subscriber = new Subscriber(new HashSet<String>(), new HashSet<String>());
        this.username = un;
        // Create a new PubSubClient
        PubSubClient pubSubClient = null;
        try {
            pubSubClient = new PubSubClient(this);
        } catch (JMSException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.client = pubSubClient;

    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
        // Also forward the new subscriptions to the pub sub client
        client.subscribe(subscriber);
    }
	
	public PubSubClient getClient() {
        return client;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Publish a notification. This method is used for own notifications
     *
     * @param notification the notification.
     */
    public void publishNotification(Notification notification) {
        client.publish(username, notification);
        // Also append to the main window as own notification
        mainwindow.appendTextToNotificationsArea(notification.toString(), true);
    }

    /**
     * Publish a received notification. This method is used for notifications
     * from other users
     *
     * @param text the html formatted text.
     */
    public synchronized void publishNotification(String text) {
        mainwindow.appendTextToNotificationsArea(text, false);
    }
}
