import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client_Espinoza {
    static JSONObject jsonObject = new JSONObject();

    public static void main(String[] args) throws InterruptedException {

        String broker = "tcp://broker.hivemq.com:1883";   //http://www.mqtt-dashboard.com/index.html
        String clientId = "";
        for (int i = 0; i < 4; i++) {
            int x = (int) (Math.random() * 9999) + 1000;
            clientId += x + "";
        }
        System.out.println("Your client ID is: " + clientId);
        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);                          //connecting to the broker
            System.out.println("Connected to broker: " + broker);

            dialogue(sampleClient);

            sampleClient.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("\nReceived a Message!" +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + new String(message.getPayload()) +
                            "\n\tQoS:     " + message.getQos() + "\n");
                }

                public void connectionLost(Throwable cause) {
                    // no need to implement this function for this assignment
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    // no need to implement this function for this assignment
                }

            });


            sampleClient.subscribe("/DS341/ResultsFrom/Espinoza/#", 2);    //subscribe to certain topic using QoS 1

            System.out.println("Subscribed");

            System.out.println("Listening");

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public static void publish(MqttClient sampleClient, String choice) throws MqttException {
        /*if (choice.contains("5"))
            sampleClient.disconnect();*/
        String cmd = choice;
        MqttMessage publish_message = new MqttMessage(cmd.getBytes());    //build the message
        publish_message.setQos(1);                                            //set QoS level

        sampleClient.publish("/DS341/TaskTo/Espinoza/ClientB", publish_message);
    }

    public static void dialogue(MqttClient sampleClient) throws MqttException {
        String choice = "";
        do {
            commands();                                                 //dialog
            Scanner in = new Scanner(System.in);
            choice = in.nextLine();                                     //user choice
            String regex = "[0-5]+";
            Pattern p = Pattern.compile(regex);
            if (choice.length() >= 1 && choice.length() <= 5)
                if (choice != null) {
                    // Find match between given string
                    // and regular expression
                    // using Pattern.matcher()
                    Matcher m = p.matcher(choice);

                    // Return if the string
                    // matched the ReGex
                    if (m.matches()) {
                        break;
                    }
                }
            System.out.println("Command not available.");
        } while (true);
        try {
            choice = format(choice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        publish(sampleClient, choice);
    }

    public static String format(String z) throws JSONException {
        String input = "";
        Scanner in = new Scanner(System.in);
        if (z.contains("0")) {
            System.out.print("Enter a first name: ");
            input = in.nextLine();
            jsonObject.put(input, 0);
        }
        if (z.contains("1")) {
            System.out.print("Enter a last name: ");
            input = in.nextLine();
            jsonObject.put(input, 1);
        }
        if (z.contains("2")) {
            System.out.print("Enter a address: ");
            input = in.nextLine();
            jsonObject.put(input, 2);
        }
        if(z.contains("3")) {
            System.out.print("Enter an age: ");
            input = in.nextLine();
            jsonObject.put(input, 3);
        }
        if (z.contains("4")) {
            System.out.print("Enter a salary: ");
            input = in.nextLine();
            jsonObject.put(input,4);
        }
        return jsonObject.toString();
    }
    public static void commands(){
        System.out.println("Commands: ");
        System.out.println("Find by First Name. Enter 0");
        System.out.println("Find by Last Name. Enter 1");
        System.out.println("Find by Address. Enter 2");
        System.out.println("Find by Age. Enter 3");
        System.out.println("Find by Salary. Enter 4");
        System.out.println("You can enter multiple commands by entering multiple numbers.");
    }
}
