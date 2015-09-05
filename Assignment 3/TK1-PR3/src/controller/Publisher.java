/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import model.Notification;

/**
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class Publisher extends Thread implements ExceptionListener {

    private Notification notification;
    private String messageId;
    private PubSubClient client;
    /**
     * Creates a Publisher thread that has to publish the notification
     * to store the messageId in the client  
     * @param notification
     * @param messageId
     * @param client 
     */
    public Publisher(Notification notification, String messageId, PubSubClient client) {
        this.notification = notification;
        this.messageId = messageId;
        this.client = client;
    }

    @Override
    public void run() {
        try {

            // Create a Session
            Session session = client.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);

            String text = notification.toString();
            if (!notification.getTags().isEmpty()) {
                for (String tag : notification.getTags()) {
                    if (!"".equals(tag)) {
                        Destination destination = session.createTopic(PubSubClient.TAG + tag);
                        MessageProducer producer = session.createProducer(destination);
                        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                        TextMessage msg = session.createTextMessage(text);
                        msg.setStringProperty("messageId", messageId);
                        System.out.println("Sent message: " + msg.getText() + " : " + Thread.currentThread().getName());
                        producer.send(msg);
                    }
                }
            }
            Destination destination = session.createTopic(PubSubClient.USER + notification.getName());
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage msg = session.createTextMessage(text);
            msg.setStringProperty("messageId", messageId);
            System.out.println("Sent message: " + msg.getText() + " : " + Thread.currentThread().getName());
            producer.send(msg);
            session.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

	@Override
    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}
