package org.ait.knimejevisplugin.selectdata;

import java.util.List;

import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.cli.CommonCLIOptions.JEVis;
import org.joda.time.DateTime;

public class SearchPattern {
	
	JEVisDataSourceSQL jevis;
	String project;
	String location;
	String nodeType;
	String devicetype;
	String component;
	int nodeID;
	boolean parent; 
	boolean children;
	boolean siblings;
	boolean allChildren;
	
	
	List<JEVisObject> searchresult;
	
	public SearchPattern(JEVisDataSourceSQL jevis, String project, String location, String nodeType, String devicetype,
			String component, int nodeID, boolean parent, boolean children, boolean siblings, boolean allChildren) {
		super();
		this.jevis = jevis;
		this.project = project;
		this.location = location;
		this.nodeType = nodeType;
		this.devicetype = devicetype;
		this.component = component;
		this.nodeID = nodeID;
		this.parent = parent;
		this.children = children;
		this.siblings = siblings;
		this.allChildren = allChildren;
	}



	private List<JEVisObject> searchNodetype() throws JEVisException{
		List<JEVisObject> list_nodetype = jevis.getObjects(jevis.getJEVisClass(nodeType), true);
		return list_nodetype;
		
	}

	public List<JEVisObject> searchData() throws JEVisException{
		
		
		// List of all Datapoints which are gonna be searched
		List<JEVisObject> datapoints = jevis.getObjects(jevis.getJEVisClass("Data"), true);
		//List containing found datapoints which match the search.
		List<JEVisObject> searchresult= null;
		for(JEVisObject datapoint: datapoints){
			List<JEVisObject> parents = datapoint.getParents();
			//Getting Time information for OuputTable
			//DateTime firstTS = datapoint.getAttribute("Value").getTimestampFromFirstSample();
			//DateTime lastTS = datapoint.getAttribute("Value").getTimestampFromLastSample();
			//long fnodeID = datapoint.getID();
			//String dataName =  datapoint.getName();
			for(JEVisObject parent :parents){
				
				if(parent.getID() == nodeID){
					//TODO: some method to search there.
					long fnodeID = parent.getID();
				}
				if(parent.getJEVisClass() == jevis.getJEVisClass("Device")){
					if(!devicetype.isEmpty()){
						if(parent.getName().equals(devicetype)){
							String fcomponent = parent.getName();
							searchresult.add(datapoint);
						}
						else{
							break;
						}
					}
					else{
						String fcomponent = parent.getName();
						searchresult.add(datapoint);
						
					}
				}
				else if(parent.getJEVisClass() == jevis.getJEVisClass("Building")){
					if(!location.isEmpty()){
						if(parent.getName().equals(location)){
							String fLocation = parent.getName();
							searchresult.add(datapoint);
						}
						else{
							break;
						}
					}
					else{
						String flocation = parent.getName();
						searchresult.add(datapoint);
						
					}
				}
				else if(parent.getJEVisClass()== jevis.getJEVisClass("Organization")){

					if(!project.isEmpty()){
						if(parent.getName().equals(project)){
							String fproject = parent.getName();
							searchresult.add(datapoint);
						}
						else{
							break;
						}
					}
					else{
						String fproject = parent.getName();
						searchresult.add(datapoint);
					}
					
				}
			}
		}
		return searchresult;
	}
				
	
}
