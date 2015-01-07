import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import airoports.Airport;

import com.google.gson.Gson;


public class ConvertToDb2 {

	public static void main(String[] args) throws Exception {

		Gson gson = new Gson();

		String jStr = LoadAssetFile("airports2.json");
    	Airport airport[] = gson.fromJson( jStr, Airport[].class);
		
		List<Object> airoportList = new ArrayList<Object>();

		for( Airport airoportItem : airport){
			airoportList.add(airoportItem);
			//System.out.println(airoportItem.getName() + " : " + airoportItem.getNameTranslations());
		}

		JsonToSQLite jsonsqlite = new JsonToSQLite();
		jsonsqlite.createDb("airports.db", "airoports", airoportList);		

	}

	/*
	 * 
	 * Read file from Asset
	 */
	static public String LoadAssetFile(String inFile) {
		String str = "";
		StringBuilder buff = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(	new InputStreamReader(
				    new FileInputStream(inFile), "UTF-8"));
			while ((str = in.readLine()) != null) {
				buff.append(str);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return buff.toString();
	}

}
