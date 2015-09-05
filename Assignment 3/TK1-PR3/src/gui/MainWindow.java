/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import controller.PubSubController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import model.Notification;
import model.Subscriber;

/**
 * The GUI's main window.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {

    /**
     * The main method showing the GUI.
     */
    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }

    // Dimensions of the window.
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 400;

    // The controller
    public PubSubController pubsubcontroller;

    // The area where notifications are displayed
    private final JTextPane notificationsArea;
    // The html body of the notifications pane
    private String notificationsBody = "";

    /**
     * Constructor for the window.
     */
    public MainWindow() {
        //Enter Name
        String name = JOptionPane.showInputDialog(this, "Enter your name");
        if (name == null) {
            // Exit if no name given.
            System.exit(0);
        }
        setTitle("Pub/Sub: " + name);

    
        // and pass it to the new PubSubController
        pubsubcontroller = new PubSubController(this,name);
        // Exit VM when closing
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // A label for the Subscriptions
        JLabel subsLabel = new JLabel("Subscriptions");
        mainPanel.add(subsLabel);

        // The user can enter the name subscriptions here
        JPanel nameSubsPanel = new JPanel();
        nameSubsPanel.setLayout(new BoxLayout(nameSubsPanel, BoxLayout.X_AXIS));
        nameSubsPanel.add(new JLabel("Name subscriptions:"));
        final JTextField nameSubsTextField = new JTextField(30);
        nameSubsTextField.setToolTipText("Note: For multiple users subscription, seperate them with comma(,)");
        nameSubsPanel.add(nameSubsTextField);
        mainPanel.add(nameSubsPanel);

        // The user can enter the tag subscriptions here
        JPanel tagSubsPanel = new JPanel();
        tagSubsPanel.setLayout(new BoxLayout(tagSubsPanel, BoxLayout.X_AXIS));
        tagSubsPanel.add(new JLabel("Tag subscriptions    :"));
        final JTextField tagSubsTextField = new JTextField(30);
        tagSubsTextField.setToolTipText("Note: For multiple tag subscriptions, seperate them with comma(,)");
        tagSubsPanel.add(tagSubsTextField);
        mainPanel.add(tagSubsPanel);

        // Button to save the subscriptions
        JButton saveSubsButton = new JButton("Save subscriptions");
        mainPanel.add(saveSubsButton);
        saveSubsButton.addActionListener(new ActionListener() {
            // Parse tag and name subscriptions (comma separated)
            // and create a new subscriber object which is then
            // passed to the PubSubController
            @Override
            public void actionPerformed(ActionEvent ae) {
                Set<String> trimmedTags = new HashSet<>();
                Set<String> trimmedNames = new HashSet<>();
                String namesText = nameSubsTextField.getText();
                String tagsText = tagSubsTextField.getText();

                if (!namesText.isEmpty()) {
                    String[] splittedNames = namesText.split(",");
                    for (String name : splittedNames) {
                        trimmedNames.add(name.trim());
                    }
                }
                if (!tagsText.isEmpty()) {
                    String[] splittedTags = tagsText.split(",");
                    for (String tag : splittedTags) {
                        trimmedTags.add(tag.trim());
                    }
                }

                pubsubcontroller.setSubscriber(new Subscriber(trimmedTags, trimmedNames));
                // Notify user of saved changes
                JOptionPane.showMessageDialog(MainWindow.this, "Your Subscriptions are saved!");
            }
        });

        // Label for Notifications
        JLabel notificationsLabel = new JLabel("Notifications");
        mainPanel.add(notificationsLabel);

        // Panel for notification and and User/Tag Information
        JPanel subscriptionInfoPanel = new JPanel();
        subscriptionInfoPanel.setLayout(new BoxLayout(subscriptionInfoPanel, BoxLayout.X_AXIS));

        // Area for notifications
        notificationsArea = new JTextPane();
        notificationsArea.setContentType("text/html");
        Border notificationBorder = BorderFactory.createLineBorder(Color.BLACK);
        notificationsArea.setBorder(BorderFactory.createCompoundBorder(notificationBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        notificationsArea.setEditable(false);
        JScrollPane notificationScrollPane = new JScrollPane(notificationsArea);
        notificationScrollPane.setViewportView(notificationsArea);
        subscriptionInfoPanel.add(notificationScrollPane);
        notificationScrollPane.setPreferredSize(new Dimension(400, 450));

        mainPanel.add(subscriptionInfoPanel);

        // A button to send a new notification
        JButton sendNotificationButton = new JButton("Send a new notification");
        mainPanel.add(sendNotificationButton);
        sendNotificationButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                // Option Pane and Dialog shown for sending notifications
                final JTextField notificationTags = new JTextField();
                notificationTags.setToolTipText("Note: To notify the message with multiple tags, seperate tags with comma(,)");
                final JTextArea notificationMessageArea = new JTextArea(10, 10);
                notificationMessageArea.setLineWrap(true);
                JScrollPane scrollPaneForMessageArea = new JScrollPane(notificationMessageArea);
                JButton notificationButton = new JButton("Send");
                Object[] optionPaneContent = {"Tags", notificationTags, "Message",
                    scrollPaneForMessageArea, notificationButton};
                JOptionPane optionPane = new JOptionPane(optionPaneContent,
                        JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{});
                final JDialog dialog = optionPane.createDialog(MainWindow.this, "Send a new notification");

                notificationButton.addActionListener(new ActionListener() {
					// Parse the entered, comma-separated tags and
                    // publish the notification with the controller
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        Set<String> trimmedTags = new HashSet<>();
                        String tagsText = notificationTags.getText();
                        if (!tagsText.isEmpty()) {
                            String[] splittedTags = tagsText.split(",");
                            for (String tag : splittedTags) {
                                trimmedTags.add(tag.trim());
                            }
                        }

                        pubsubcontroller.publishNotification(new Notification(pubsubcontroller.getUsername(),
                                notificationMessageArea.getText(), trimmedTags));
                        dialog.dispose();
                    }
                });
                dialog.setVisible(true);
            }
        });

        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.add(mainPanel);
        this.pack();
    }

    /**
     * Append html formatted text to the notifications area.
     *
     * @param text the html formatted text
     * @param ownNotifcation if {@code true} text will be displayed italic.
     */
    public void appendTextToNotificationsArea(String text, boolean ownNotifcation) {
        if (ownNotifcation) {
            text = "<i>" + text + "</i>";
        }

        notificationsBody = notificationsBody + text + "<br/>";
        notificationsArea.setText("<html><head></head><body>" + notificationsBody + "</body></html>");
    }

}
