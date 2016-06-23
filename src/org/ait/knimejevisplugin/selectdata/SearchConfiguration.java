package org.ait.knimejevisplugin.selectdata;

import java.util.ArrayList;
import java.util.List;

public class SearchConfiguration {
	
	/* Setting the DatabaseParameters and Node SettingsNames*/
	public String projectModelName = "Project";
	public String projectLevelName = "Organization";
	
	public String locationModelName = "Location";
	public String locationLevelName = "Building";
	
	public String componentModelName = "Component";
	public String componentLevelName = "Device";
	
	public String deviceModelName = "Devicetype";
	public String deviceLevelName = "Data";
	
	public String valueAttributeName = "Value";
	
	public List<String> levels = new ArrayList<String>();
	
	public String nodeType = "NodeType";
	/* Setting up JEVisDefaultConnection*/
	
	public String hostModelName = "Hostaddress";
	public String DEFAULT_Host= "jevis3.ait.ac.at";
	
	public String portModelName = "Port";
	public String DEFAULT_port = "3306";
	
	public String sqlSchemaModelName = "Schema";
	public String DEFAULT_sqlShema = "jevis";
	
   	public String sqlUserModelName = "User";
   	public String DEFAULT_sqlUserName = "jevis";
   	
   	public String sqlPWModelName = "Password";
   	public String DEFAULT_sqlPW = "vu5eS1ma";
   	
   	public String jevisUserModelName = "JevisUser";
   	public String DEFAULT_jevisUserName = "BerhnardM";
   	
   	public String jevisPWModelName = "JevisPassword";
   	public String DEFAULT_jevisPW = "testpass01593";
	
	
	
	public void fillLevel(){
		levels.clear();
		levels.add(projectLevelName);
		levels.add(locationLevelName);
		levels.add(componentLevelName);
		levels.add(deviceLevelName);
	}
	
	
	/*Constructor*/
	public SearchConfiguration() {
		super();
	}
	
	
	
}
