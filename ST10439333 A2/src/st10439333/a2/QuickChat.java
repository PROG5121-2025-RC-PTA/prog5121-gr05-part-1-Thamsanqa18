/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package st10439333.a2;

import java.util.ArrayList;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;

public class QuickChat {
    private static ArrayList<String> messagesSent = new ArrayList<>();
    private static final String JSON_FILE = "messages.json";
    private static int totalMessagesSent = 0;

    public static void main(String[] args) {
        // Login
        String username = JOptionPane.showInputDialog(null, "Enter username:", "QuickChat Login", JOptionPane.PLAIN_MESSAGE);
        if (username == null) {
            JOptionPane.showMessageDialog(null, "Login cancelled. Exiting...", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        String password = JOptionPane.showInputDialog(null, "Enter password:", "QuickChat Login", JOptionPane.PLAIN_MESSAGE);
        if (password == null) {
            JOptionPane.showMessageDialog(null, "Login cancelled. Exiting...", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        // login check
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Login failed. Username or password cannot be empty.", "QuickChat", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.", "QuickChat", JOptionPane.INFORMATION_MESSAGE);

        // Load previous messages from JSON
        loadMessagesFromJSON();

        // Main menu loop
        while (true) {
            String[] options = {"Send Message", "Show recently sent messages", "Quit"};
            int choice = JOptionPane.showOptionDialog(null, "Select an option:", "QuickChat Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0: // Send Message
                    sendMessage();
                    break;
                case 1: // Show recently sent messages
                    showRecentMessages();
                    break;
                case 2: // Quit
                    JOptionPane.showMessageDialog(null, "COMING SOON.", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Try again.", "QuickChat", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void sendMessage() {
        String numMessagesStr = JOptionPane.showInputDialog(null, "Enter the number of messages to send:", "QuickChat", JOptionPane.PLAIN_MESSAGE);
        if (numMessagesStr == null) {
            return; // User cancelled
        }

        int numMessages;
        try {
            numMessages = Integer.parseInt(numMessagesStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number. Try again.", "QuickChat", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < numMessages; i++) {
            // Get recipient
            String recipient = JOptionPane.showInputDialog(null, "Message " + (i + 1) + "\nRecipient Number (+27718693002 format):", "QuickChat", JOptionPane.PLAIN_MESSAGE);
            if (recipient == null) {
                continue; // User cancelled
            }
            if (!checkRecipient(recipient)) {
                JOptionPane.showMessageDialog(null, "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", "QuickChat", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Get message
            String message = JOptionPane.showInputDialog(null, "Message " + (i + 1) + "\nMessage (max 250 chars):", "QuickChat", JOptionPane.PLAIN_MESSAGE);
            if (message == null) {
                continue; // User cancelled
            }
            if (!checkMessage(message)) {
                JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + (message.length() - 250) + " [enter number here], please reduce size.", "QuickChat", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            if (message.length() > 250) {
                JOptionPane.showMessageDialog(null, "Please enter a message of less than 50 characters.", "QuickChat", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Generate Message ID and Hash
            String messageID = generateMessageID();
            String messageHash = createMessageHash(messageID, message);

            // Store message
            JSONObject messageObj = new JSONObject();
            messageObj.put("MessageID", messageID);
            messageObj.put("MessageHash", messageHash);
            messageObj.put("Recipient", recipient);
            messageObj.put("Message", message);

            storeMessage(messageObj);
            messagesSent.add(message);
            totalMessagesSent++;

            // Display message details
            String messageDetails = "Message sent\n\n" +
                    "Message ID: " + messageID + "\n" +
                    "Message Hash: " + messageHash + "\n" +
                    "Recipient: " + recipient + "\n" +
                    "Message: " + message + "\n" +
                    "Total Messages Sent: " + totalMessagesSent;
            JOptionPane.showMessageDialog(null, messageDetails, "QuickChat", JOptionPane.INFORMATION_MESSAGE);

            // Asks to send, store, or disregard
            String[] actionOptions = {"Send Message", "Disregard Message", "Store Message"};
            int action = JOptionPane.showOptionDialog(null, "Select an action:", "QuickChat",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, actionOptions, actionOptions[0]);

            switch (action) {
                case 0: // Sends message
                    JOptionPane.showMessageDialog(null, "Message successfully sent.", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 1: // Disregards message
                    JOptionPane.showMessageDialog(null, "Message 0 to message.", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                    messagesSent.remove(messagesSent.size() - 1);
                    totalMessagesSent--;
                    break;
                case 2: // Stores message
                    JOptionPane.showMessageDialog(null, "Message successfully stored.", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Message stored by default.", "QuickChat", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
        //Shows recently sent or stored messages
    private static void showRecentMessages() {
        if (messagesSent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages sent yet.\nTotal Messages Sent: " + totalMessagesSent, "QuickChat", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder recentMessages = new StringBuilder("Recently sent messages:\n\n");
            for (int i = 0; i < messagesSent.size(); i++) {
                recentMessages.append((i + 1)).append(". ").append(messagesSent.get(i)).append("\n");
            }
            recentMessages.append("\nTotal Messages Sent: ").append(totalMessagesSent);
            JOptionPane.showMessageDialog(null, recentMessages.toString(), "QuickChat", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Validation Methods
    public static boolean checkMessage(String message) {
        return message.length() <= 250;
    }

    public static boolean checkRecipient(String recipient) {
        return recipient.matches("\\+\\d{10,}");
    }

    // Message ID and Hash Generation
    private static String generateMessageID() {
        Random rand = new Random();
        StringBuilder messageID = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            messageID.append(rand.nextInt(10));
        }
        return messageID.toString();
    }

    public static String createMessageHash(String messageID, String message) {
        String[] words = message.split("\\s+");
        if (words.length < 1) return "00:0:NOTHANKS";

        String firstWord = words[0].toUpperCase();
        String lastWord = words[words.length - 1].toUpperCase();
        String firstTwoDigits = messageID.substring(0, 2);
        int messageLength = message.length();
        return firstTwoDigits + ":" + messageLength + ":" + firstWord + lastWord;
    }

    // JSON Storage
    private static void storeMessage(JSONObject message) {
        JSONArray messageArray = new JSONArray();
        try {
            // Read existing messages
            FileReader reader = new FileReader(JSON_FILE);
            StringBuilder jsonContent = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                jsonContent.append((char) character);
            }
            reader.close();

            if (jsonContent.length() > 0) {
                messageArray = new JSONArray(jsonContent.toString());
            }

            // Add new message
            messageArray.put(message);

            // Write back to file
            FileWriter writer = new FileWriter(JSON_FILE);
            writer.write(messageArray.toString(4));
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error storing message: " + e.getMessage(), "QuickChat", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void loadMessagesFromJSON() {
        try {
            FileReader reader = new FileReader(JSON_FILE);
            StringBuilder jsonContent = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                jsonContent.append((char) character);
            }
            reader.close();

            if (jsonContent.length() > 0) {
                JSONArray messageArray = new JSONArray(jsonContent.toString());
                totalMessagesSent = messageArray.length();
                for (int i = 0; i < messageArray.length(); i++) {
                    JSONObject msg = messageArray.getJSONObject(i);
                    messagesSent.add(msg.getString("Message"));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No previous messages found.", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
