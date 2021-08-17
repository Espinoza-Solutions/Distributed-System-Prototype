import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Boss_Actor extends UntypedActor {

	//to print debugging messages
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    int count = 0;
    JSONArray jsonArray = new JSONArray();
    ActorRef[] workers = new ActorRef[4];
    String broker       = "tcp://broker.hivemq.com:1883";   //http://www.mqtt-dashboard.com/index.html
    String clientId     = "ClientB_Publish_SubscriberClient";
    MqttClient sampleClient;

    {
        try {
            sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preStart() {          //what to do when this actor is created and started
        
        log.info("Starting a Boss Actor");
        mqtt();

    }

    @Override
    public void onReceive(Object msg) throws MqttException {    //what to do when a message is received

        if (msg instanceof String && count < 2) {
            count++;
            jsonArray.put(msg.toString());
        }

        if (count == 2) {
            for (int i = 0; i < 2; i++)
                workers[i].tell(new String("Terminate"), getSelf());
            getContext().stop(getSelf());
            publish(sampleClient,jsonArray.toString());
        }

    }
    
    
    @Override
    public void postStop() {                        //what to do when terminated
    	
     	log.info("terminating the Boss actor");

    }
    public void publish(MqttClient sampleClient, String message ) throws MqttException {
        MqttMessage publish_message= new MqttMessage(message.getBytes());    //build the message
        publish_message.setQos(2);                                            //set QoS level
        sampleClient.publish("/DS341/ResultsFrom/Espinoza/ClientB", publish_message);
    }
    public  void mqtt(){

        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);                          //connecting to the broker
            System.out.println("Connected to broker: "+broker);


            sampleClient.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage payload) throws Exception {
                    String message = new String(payload.getPayload()); //get payload
                    System.out.println("\nReceived a Message!" +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + message +
                            "\n\tQoS:     " + payload.getQos() + "\n");
                    //create workers
                    sendWorkersWork(message);
                }

                public void connectionLost(Throwable cause) {
                    // no need to implement this function for this assignment
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    // no need to implement this function for this assignment
                }

            });


            sampleClient.subscribe("/DS341/TaskTo/Espinoza/ClientB", 1);    //subscribe to certain topic using QoS 1

            System.out.println("Subscribed");

            System.out.println("Listening");

        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    public void sendWorkersWork(String message) throws JSONException {

        JSONObject jsonObject0 = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        message = message.replaceAll("[{\"}]*", "");
        String[] arr = message.split(",");
        for (int i = 0; i < arr.length; i++) {
            String[] arr1 = arr[i].split(":");
            jsonObject0.put(arr1[0], arr1[1]);
            jsonObject1.put(arr1[0], arr1[1]);
        }

        //let the boss actor sends a message to the worker
        //the first component in the message is the actual message: which is an object
        //getSelf() is to include the sender identity in the message, used for reply
        //getSelf() can be replaced with ActorRef.noSender(), to send message with no sender info attached

        workers[0] = getContext().actorOf(Props.create(Worker_Actor.class), "Worker"+(1));
        jsonObject0.put("0", "5");
        workers[0].tell(new String(jsonObject0.toString()), getSelf());
        workers[1] = getContext().actorOf(Props.create(Worker_Actor.class), "Worker"+(2));
        jsonObject1.put("1", "5");
        workers[1].tell(new String(jsonObject1.toString()), getSelf());

    }
}
