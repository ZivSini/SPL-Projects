package bgu.spl.mics.application.passiveObjects;

import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets;
	/**
	 * Retrieves the single instance of this class.
	 */
	private static class InventoryHolder{
		private static Inventory instance = new Inventory();
	}
	private Inventory(){
		gadgets = new LinkedList<String>();
	}

	public static Inventory getInstance() {
		//TODO: Implement this
		return InventoryHolder.instance;
	}

	/**
	 * Initializes the inventory. This method adds all the items given to the gadget
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (String[] inventory) {
		//TODO: Implement this
		List<String> gadgets = new LinkedList<String>();
		for(String s : inventory)
		{
			this.gadgets.add(s);
		}
	}

	/**
	 * acquires a gadget and returns 'true' if it exists.
	 * <p>
	 * @param gadget 		Name of the gadget to check if available
	 * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
	 */
	public boolean getItem(String gadget){
		if(gadgets.contains(gadget))
		{
			gadgets.remove(gadget);
			return true;
		}
		return false;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Gadget> which is a
	 * List of all the gadgets that remain in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) throws IOException {
		JsonArray obj = new JsonArray();
		for(String gadget : gadgets) obj.add(gadget);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(obj);



		FileWriter file = new FileWriter(filename);
		try{
			file.write(prettyJson);
			file.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
