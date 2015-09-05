/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class Consumer extends Thread implements ExceptionListener, MessageListener {

    private String topic;
    private PubSubController controller;
    /**
     * Constructor to create a new consumer thread
     * that listens to a particular topic and displays
     * the messages with the PubSubcontroller
     * @param identifier
     * @param user
     * @param controller 
     */
    public Consumer(String identifier, boolean user, PubSubController controller) {
        if (user) {
            topic = PubSubClient.USER + identifier;
        } else {
            topic = PubSubClient.TAG + identifier;
        }
        this.controller = controller;
    }
    @Override
    public void run() {
        try {
            // Create a Session
            Session session = controller.getClient().getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createTopic(topic);

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);
            // Wait for a message
            consumer.setMessageListener(this);

            while (!Thread.currentThread().isInterrupted()) {
            }
            System.out.println("Thread interrupted");

            consumer.close();
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
    /**
     * MessageListener method implemented, this one is executed
     * every time a message from the specific listening topic arrives
     * @param message 
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println(controller.getUsername() + ": Received: " + text);
                System.out.println("format:" + textMessage.getJMSDestination());
                System.out.println("id:" + textMessage.getStringProperty("messageId"));

                if (controller.getClient().messageReceived(textMessage.getStringProperty("messageId"))) {
                    controller.publishNotification(text);
                }

            } else {
                System.out.println("Received: " + message);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
