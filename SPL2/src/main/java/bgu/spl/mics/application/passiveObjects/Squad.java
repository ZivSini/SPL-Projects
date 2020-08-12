package bgu.spl.mics.application.passiveObjects;
import java.util.*;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private HashMap<String, Agent> agentsMap;

	private static class SquadHolder{
		private static Squad instance = new Squad();
	}
	private Squad(){
		agentsMap = new HashMap<>();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return SquadHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for(Agent a:agents) {
			this.agentsMap.put(a.getSerialNumber(), a);
		}
	}

	/**
	 * Releases agents.
	 */
	public  void releaseAgents(List<String> serials){
		Collections.sort(serials);
		for (String agentSerialNum: serials){
			if (this.agentsMap.containsKey(agentSerialNum)) {
				this.agentsMap.get(agentSerialNum).release();
			}
		}
		System.out.println("Agents released");
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time) throws InterruptedException {
		Thread.sleep(time*100);
		this.releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public  boolean getAgents(List<String> serials) throws InterruptedException {
		Collections.sort(serials); //TODO: check if sort is okay
		for (String agentSerialNum : serials) {
			if (!this.agentsMap.containsKey(agentSerialNum)) {
				this.releaseAgents(serials);
				return false;
			}
				this.agentsMap.get(agentSerialNum).acquire();
			}
		return true;
	}

	/**
	 * gets the agents names
	 * @param serials the serial numbers of the agents
	 * @return a list of the names of the agents with the specified serials.
	 */
	public List<String> getAgentsNames(List<String> serials){
		Collections.sort(serials);
		List<String> agentsNameList = new LinkedList<>();
		for (String agentSerialNum: serials){
			if (this.agentsMap.containsKey(agentSerialNum)){
				agentsNameList.add(this.agentsMap.get(agentSerialNum).getName());
			}
		}
		return  agentsNameList;
	}

}
