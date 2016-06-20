package org.ait.knimejevisplugin.selectdata;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.cli.CommonCLIOptions.JEVis;
import org.joda.time.DateTime;

import com.jcraft.jsch.Logger;

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
	
	boolean enabledProject;
	boolean enabledLocation;
	boolean enabledNodeType;
	boolean enableddeviceType; 
	boolean enabledComponent;
	
	List<JEVisObject> searchresult;
	JEVisObject projectObject;
	JEVisObject locationObject;
	JEVisObject componentObject;

	
	public SearchPattern(JEVisDataSourceSQL jevis, String project, String location, String nodeType, String devicetype,
			String component, int nodeID, boolean parent, boolean children, boolean siblings, boolean allChildren,
			boolean enabledProject, boolean enabledLocation, boolean enabledNodeType, boolean enableddeviceType,
			boolean enabledComponent) {
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
		this.enabledProject = enabledProject;
		this.enabledLocation = enabledLocation;
		this.enabledNodeType = enabledNodeType;
		this.enableddeviceType = enableddeviceType;
		this.enabledComponent = enabledComponent;

	}
	
	private List<JEVisObject> searchNodetype() throws JEVisException{
		List<JEVisObject> list_nodetype = jevis.getObjects(jevis.getJEVisClass(nodeType), true);
		return list_nodetype;
		
	}


	public List<JEVisObject> searchData() throws JEVisException{
		
		List<JEVisObject> projects = jevis.getObjects(jevis.getJEVisClass("Organization"), true);
		List<JEVisObject> searchresult= new ArrayList<JEVisObject>();
		checkLevel(projects,searchresult);
		return searchresult;
		
	}
	public List<JEVisObject> checkLevel(List<JEVisObject> children, List<JEVisObject> searchresult) throws JEVisException{

		for(JEVisObject child : children){
			if(child.getJEVisClass()== jevis.getJEVisClass("Organization")){
				System.out.println("Project:" + child.getName());
				if(!enabledProject){
					if(child.getName().equals(project)){
						projectObject = child;
						List<JEVisObject> projectChildren = projectObject.getChildren();
						checkLevel(projectChildren, searchresult);					
					}
				}else{
					checkLevel(child.getChildren(), searchresult);
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass("Building")){
				System.out.println("Location:" + child.getName());
				if(!enabledLocation){
					if(child.getName().equals(location)){
						locationObject = child;
						List<JEVisObject> locationChildren = locationObject.getChildren();
						checkLevel(locationChildren, searchresult);
					}
				}else{
					checkLevel(child.getChildren(), searchresult);
					
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass("Device")){
				System.out.println("Component:" + child.getName());
				if(!enabledComponent){
					if(child.getName().equals(component)){
						componentObject = child;
						List<JEVisObject> componentChildren = componentObject.getChildren();
						checkLevel(componentChildren, searchresult);
					}
				}else{
					checkLevel(child.getChildren(), searchresult);
				}
			}
			else if(child.getJEVisClass()== jevis.getJEVisClass("Data")){
				System.out.println("Data:" + child.getName());
				if(!enableddeviceType){
					if(child.equals(devicetype)){
						searchresult.add(child);
					}else{
						System.out.println("No Datapoint found");
					}
				}else{
					searchresult.add(child);
				}

			}
			else{
				checkLevel(child.getChildren(), searchresult);				
			}
			
		}
		return searchresult;
	}
	/*
	public List<JEVisObject> searchDatatest() throws JEVisException{
		
		
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
	
			}
		}
		return searchresult;
	}
				
	public void checkData(JEVisObject parent, JEVisObject datapoint) throws JEVisException{
		if(parent.getJEVisClass() == jevis.getJEVisClass("Device")){
			if(enableddeviceType){
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
			if(enabledLocation){
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

			if(enabledProject){
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
	
	

	}*/
}
