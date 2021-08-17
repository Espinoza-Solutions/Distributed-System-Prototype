import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Worker_Actor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    String[] list = new java.lang.String[4];
    String first = "", last = "", address = "", age = "", salary = "", file = "";
    int[] whichCMDS = {-1,-1,-1,-1,-1};
    int numberOfCMDS = 0;
    boolean firstTwoFiles = false;
    
    @Override
    public void preStart() {                                            //what to do when created and started
        
        log.info("Starting worker actor");
        list[0] = "C:\\Users\\ijesp\\Documents\\School\\CS341_HW3\\src\\main\\resources\\Folder1\\File_1.csv";
        list[1] = "C:\\Users\\ijesp\\Documents\\School\\CS341_HW3\\src\\main\\resources\\Folder2\\File_2.csv";
        list[2] = "C:\\Users\\ijesp\\Documents\\School\\CS341_HW3\\src\\main\\resources\\Folder3\\File_3.csv";
        list[3] = "C:\\Users\\ijesp\\Documents\\School\\CS341_HW3\\src\\main\\resources\\Folder4\\File_4.csv";

    }

    
    @Override
    public void onReceive(Object msg) throws IOException, JSONException {                                 //what to do when message is received

        if (msg.toString().equals("Terminate")) {

            log.info("Received Message: " + msg.toString());                                    //print the message
            log.info("the sender of this message is " + getSender().path().name().toString());  //print the identity of the sender

            getContext().stop(getSelf());
        }
        else {
            assignCommands(msg.toString());

            if (firstTwoFiles)
                processFiles();
            processFiles();
        }
    }

    private void processFiles() throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        BufferedReader csvReader = new BufferedReader(new FileReader(list[Integer.parseInt(file)]));
        String row = "";
        int count = 0;
        while ((row = csvReader.readLine()) != null) {
            count = 0;
            String[] data = row.split(",");
            if (data[0].equals(first)) {
                count++;
            }
            if (data[1].equals(last)) {
                count++;
            }
            if (data[2].equals(address)) {
                count++;
            }
            if (data[3].equals(salary)) {
                count++;
            }
            if (data[4].equals(age)) {
                count++;
            }
            if (count == numberOfCMDS) {
                jsonObject.put("First", data[0]);
                jsonObject.put("Last", data[1]);
                jsonObject.put("Address", data[2]);
                jsonObject.put("Salary", data[3]);
                jsonObject.put("Age", data[4]);
            }
               // do something with the data
        }

        getSender().tell(new String(jsonObject.toString()), getSelf());
        csvReader.close();
    }

    private void assignCommands(String msg) {
        String message = msg.replaceAll("[{\"}]*", "");
        String[] arr = message.split(",");
        for (int i = 0; i < arr.length; i++) {
            String[] arr1 = arr[i].split(":");
            if (arr1[1].contains("0")) {
                first = arr1[0];
                whichCMDS[0] = 1;
                numberOfCMDS++;
            }
            if (arr1[1].contains("1")) {
                last = arr1[0];
                whichCMDS[1] = 1;
                numberOfCMDS++;
            }
            if (arr1[1].contains("2")) {
                address = arr1[0];
                whichCMDS[2] = 1;
                numberOfCMDS++;
            }
            if (arr1[1].contains("3")) {
                age = arr1[0];
                whichCMDS[4] = 1;
                numberOfCMDS++;
            }
            if (arr1[1].contains("4")) {
                salary = arr1[0];
                whichCMDS[3] = 1;
                numberOfCMDS++;
            }
            if (arr1[1].contains("5")) {
                file = arr1[0];
            }
        }
    }

    @Override
    public void postStop() {                                            //what to do when terminated
     	log.info("terminating");

    }
}
