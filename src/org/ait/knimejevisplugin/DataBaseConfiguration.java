package org.ait.knimejevisplugin;

import java.util.ArrayList;
import java.util.List;

import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;

public class DataBaseConfiguration {
	
	/* Setting the DatabaseParameters and Node SettingsNames*/
	public static String projectModelName = "Project";
	public static String projectLevelName = "Organization"; //the name of the Project JEVis
	
	public static String locationModelName = "Location";
	public static String locationLevelName = "Building"; //The name of the location JEVisClass
	
	public static String componentModelName = "Component";
	public static String componentLevelName = "Device"; //The name of the component JEVisclass.
	
	public static String deviceModelName = "Datapoint";
	public static String deviceLevelName = "Data"; //the name of the Datapoint JEVisClass.
	
	public static String valueAttributeName = "Value";
	
	public static List<String> levels = new ArrayList<String>();
	
	public static String nodeType = "NodeType";
	/* Setting up JEVisDefaultConnection*/
	
	public static  String hostModelName = "Hostaddress";
	public static  String DEFAULT_Host=""; 
	
	public static String portModelName = "Port";
	public static String DEFAULT_port ="";
	
	public static String sqlSchemaModelName = "Schema";
	public static String DEFAULT_sqlShema = "";
	
   	public static String sqlUserModelName = "User";
   	public static String DEFAULT_sqlUserName ="";
   	
   	public static String sqlPWModelName = "Password";
   	public static String DEFAULT_sqlPW ="";
   	
   	public static String jevisUserModelName = "JevisUser";
   	public static String DEFAULT_jevisUserName = "";
   	
   	public static String jevisPWModelName = "JevisPassword";
   	public static String DEFAULT_jevisPW ="";
	
   	//Methods
	public static void fillLevel(){
		levels.clear();
		levels.add(projectLevelName);
		levels.add(locationLevelName);
		levels.add(componentLevelName);
		levels.add(deviceLevelName);
	}
	
	public static boolean checkLevel(JEVisObject object, String level, JEVisDataSourceSQL jevis) throws JEVisException{
		if(object.getJEVisClass().equals(jevis.getJEVisClass(level))){
			return true;
		}
		return false;
	}
	
	/*Constructor*/
	public DataBaseConfiguration() {
		super();
	}

}
