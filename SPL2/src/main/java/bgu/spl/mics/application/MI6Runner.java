package bgu.spl.mics.application;
import bgu.spl.mics.AgentsAvailableEvent;
import bgu.spl.mics.MissionReceivedEvent;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {


    public static void main(String[] args) throws IOException, InterruptedException {


        Gson gson = new Gson();
        FileReader FR = new FileReader(args[0]);
        List<Thread> IntellThreads = new LinkedList<>();
        List<Thread> MThreads = new LinkedList<>();
        List<Thread> MPThreads = new LinkedList<>();
        List<Thread> threads = new LinkedList<>();



        JsonReader read = new JsonReader(FR);
        JsonElement element = JsonParser.parseReader(read);

        /** inventory*/
        Inventory inventory = Inventory.getInstance();
        JsonArray inv = element.getAsJsonObject().get("inventory").getAsJsonArray();
        String[] inventoryToLoad = new String[inv.size()];
        for(int i=0;i<inventoryToLoad.length;i++)
        {
            inventoryToLoad[i]= inv.get(i).getAsString();
        }
        Inventory.getInstance().load(inventoryToLoad);

        /** services */
        JsonObject ser = element.getAsJsonObject().get("services").getAsJsonObject();
        Integer m = ser.getAsJsonObject().get("M").getAsInt();
        Integer mp = ser.getAsJsonObject().get("Moneypenny").getAsInt();
        Long timeLimit = ser.getAsJsonObject().get("time").getAsLong();
        JsonArray intelligenceJson = ser.getAsJsonObject().get("intelligence").getAsJsonArray();
        CountDownLatch latch = new CountDownLatch(m+mp+intelligenceJson.size()+1);
        for(int i=0;i<intelligenceJson.size();i++)
        {
            JsonArray intel = intelligenceJson.get(i).getAsJsonObject().get("missions").getAsJsonArray();
            List<MissionInfo> missionInfoList = new LinkedList<MissionInfo>();
            for (int j=0;j<intel.size();j++)
            {
                JsonObject miss = intel.get(j).getAsJsonObject();
                MissionInfo mi = new MissionInfo();
                String missionName = miss.get("name").getAsString();
                String gadget = miss.get("gadget").getAsString();
                Integer duration = miss.get("duration").getAsInt();
                Integer timeIssued = miss.get("timeIssued").getAsInt();
                Integer timeExpired = miss.get("timeExpired").getAsInt();
                List<String> serialAgentsNumbers = new LinkedList<String>();
                JsonArray agentsNumbers = miss.get("serialAgentsNumbers").getAsJsonArray();
                for (int k=0; k<agentsNumbers.size();k++)
                {
                    serialAgentsNumbers.add(agentsNumbers.get(k).getAsString());
                }
                mi.setDuration(duration);
                mi.setGadget(gadget);
                mi.setMissionName(missionName);
                mi.setSerialAgentsNumbers(serialAgentsNumbers);
                mi.setTimeExpired(timeExpired);
                mi.setTimeIssued(timeIssued);
                missionInfoList.add(mi);
            }

            IntellThreads.add( new Thread(new Intelligence(missionInfoList,i,timeLimit,latch)));
        }
        for(int i =0;i<m;i++)
        {
            MThreads.add( new Thread(new M(timeLimit.intValue(),i,latch)));
        }
        for(int i =0;i<mp;i++)
        {
            MPThreads.add( new Thread(new Moneypenny(i,timeLimit,latch)));
        }

        /** squad */
        JsonArray sqd = element.getAsJsonObject().get("squad").getAsJsonArray();
        Agent[] agents = new Agent[sqd.size()];
        int i=0;
        for (JsonElement agentDetails: sqd){
            Agent a = new Agent();
            a.setName(agentDetails.getAsJsonObject().get("name").getAsString());
            a.setSerialNumber(agentDetails.getAsJsonObject().get("serialNumber").getAsString());
            agents[i]=a;
            i++;
        }
        Squad.getInstance().load(agents);
        /** Q and TimeService */
        threads.add( new Thread(new Q(timeLimit,latch)));

        /** run threads */
        for(Thread t : MThreads)
        {
            t.start();
        }
        for(Thread t : MPThreads)
        {
            t.start();
        }
        for(Thread t : IntellThreads)
        {
            t.start();
        }
        for(Thread t : threads)
        {
            t.start();
        }
        latch.await();
        Thread timeServiceThread = new Thread(new TimeService(timeLimit));
        timeServiceThread.start();
        threads.add(timeServiceThread);


        timeServiceThread.join();
        for(Thread t : IntellThreads)
        {
            t.join();
        }
        for(Thread t : threads)  //Q
        {
            t.join();
        }

        for(Thread t : MPThreads)
        {
            if(t.isAlive()){
                t.interrupt();

            }
            t.join();
        }
        for(Thread t : MThreads)
        {
            if(t.isAlive()){
                t.interrupt();

            }
            t.join();
        }


        Inventory.getInstance().printToFile(args[1]);
        Diary.getInstance().printToFile(args[2]);
    }


}
