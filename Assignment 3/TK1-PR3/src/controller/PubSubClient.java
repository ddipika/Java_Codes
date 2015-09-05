/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import model.Notification;
import model.Subscriber;

public class PubSubClient implements ExceptionListener {

    public static final String TAG = "tag.";
    public static final String USER = "user.";
	
    private ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
    private Connection connection;
    private ConcurrentHashMap<String, Consumer> consumers;
    private Set<String> messagesReceived;
    private PubSubController controller;
    private Random random;

    public PubSubClient(PubSubController pubSubController) throws JMSException {
        random = new Random();
        connection = connectionFactory.createConnection();
        connection.start();
        connection.setExceptionListener(this);
        consumers = new ConcurrentHashMap<>();
        messagesReceived = new HashSet<String>();
        this.controller = pubSubController;
    }
    /**
     * Check if a particular message has already been displayed  
     * @param id
     * @return true if message is added for the first time 
     */
    public boolean messageReceived(String id) {
        return messagesReceived.add(id);
    }

    public void close() throws JMSException {
        connection.close();
    }

    @Override
    public void onException(JMSException jmse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Interrupts current consumer threads, listening for particular topic,
     * creates new consumer threads based on subscriber subscriptions 
     * @param subscriber 
     */
    public void subscribe(Subscriber subscriber) {
        for (Map.Entry<String, Consumer> entrySet : consumers.entrySet()) {
            Consumer thread = entrySet.getValue();
            thread.interrupt();
        }
        consumers.clear();
        Consumer consumerThread = null;
        for (String tag : subscriber.getTags()) {
            consumerThread = new Consumer(tag, false, controller);
            consumerThread.start();
            consumers.put(tag, consumerThread);
        }
        for (String user : subscriber.getNames()) {
            consumerThread = new Consumer(user, true, controller);
            consumerThread.start();
            consumers.put(user, consumerThread);
        }
    }
    /**
     * Publish a notification
     * @param user
     * @param notification 
     */
    public void publish(String user, Notification notification) {
        String messageId = user + random.nextLong();
        messageReceived(messageId);

        new Publisher(notification, messageId,this).start();
        System.out.println("new thred created 2");
    }

	public Connection getConnection() {
		return connection;
	}
}
