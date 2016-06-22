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
	
	public void fillLevel(){
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
