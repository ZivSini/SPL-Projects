package bgu.spl.mics.application.passiveObjects;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {
	private List<Report> reports;
	private AtomicInteger total;


	private static class diaryHolder {
		private static Diary instance=new Diary();
	}
	private Diary(){
		reports = new ArrayList<>();
		total= new AtomicInteger(0);
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return diaryHolder.instance;
	}

	public List<Report> getReports() {
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		reports.add(reportToAdd);
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) {

		JSONArray jsonReports = new JSONArray();
		for (Report r : reports) {
			jsonReports.add(r);
		}
		JSONObject obj = new JSONObject();
		obj.put("reports", jsonReports);
		obj.put("total", getTotal());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(obj);
		try {
			FileWriter file = new FileWriter(filename);
			file.write(prettyJson);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total.get();
	}

	public void incrementTotal(){

		total.getAndIncrement();
		System.out.println("TOTAL++");
	}



}
