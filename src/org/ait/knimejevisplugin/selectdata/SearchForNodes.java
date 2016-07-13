package org.ait.knimejevisplugin.selectdata;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.ait.knimejevisplugin.DataBaseConfiguration;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.cli.CommonCLIOptions.JEVis;
import org.joda.time.DateTime;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;

public class SearchForNodes {
	
	static JEVisDataSourceSQL jevis;
	String project;
	String location;
	String nodeType;
	String devicetype;
	String component;
	int nodeID;

	boolean enabledProject;
	boolean enabledLocation;
	boolean enabledNodeType;
	boolean enableddeviceType; 
	boolean enabledComponent;
	
	List<JEVisObject> list_projects = new ArrayList<JEVisObject>();
	List<JEVisObject> list_location = new ArrayList<JEVisObject>();
	List<JEVisObject> list_component = new ArrayList<JEVisObject>();
	List<JEVisObject> list_datapoint = new ArrayList<JEVisObject>();
	
	List<JEVisObject> list_nodetype = new ArrayList<JEVisObject>();
	
	List<JEVisObject> searchresult;
	JEVisObject projectObject;
	JEVisObject locationObject;
	JEVisObject componentObject;
	JEVisClass jclass;
	DataTableSpec result;
	DataCell[]cells;
	int counter = 0;
	
	JEVisObject organization= null;
	JEVisObject building = null;
	JEVisObject componentlevel = null;

	
	ArrayList<String> list_levels = new ArrayList<String>();
	
	public SearchForNodes(JEVisDataSourceSQL jevis, String project, String location,
			String nodeType, String devicetype,	String component, 
			boolean enabledProject, boolean enabledLocation, boolean enabledNodeType,
			boolean enableddeviceType, boolean enabledComponent, DataTableSpec result) {
		super();
		this.jevis = jevis;
		this.project = project;
		this.location = location;
		this.nodeType = nodeType;
		this.devicetype = devicetype;
		this.component = component;
		this.enabledProject = enabledProject;
		this.enabledLocation = enabledLocation;
		this.enabledNodeType = enabledNodeType;
		this.enableddeviceType = enableddeviceType;
		this.enabledComponent = enabledComponent;
		this.result = result;

	}
	
/*
 * Search for Datapoints in Specific Projects, Locations, Components and Devices 
 */
	public void searchforDataPoints() throws JEVisException{

		List<JEVisObject> projects = jevis.getObjects(jevis.getJEVisClass(
				DataBaseConfiguration.projectLevelName), true);

		computeResult(projects);
		
	}
	
	public void computeResult(List<JEVisObject> children)
			throws JEVisException{
		
		for(JEVisObject child : children){			
			
			if(child.getJEVisClass() == jevis.getJEVisClass(
					DataBaseConfiguration.projectLevelName)){
				System.out.println("Project:" + child.getName());
				if(enabledProject){
					if(child.getName().equals(project)){
						
						organization = child;
						computeResult(child.getChildren());					
					}
				}else{

					organization = child;
					computeResult(child.getChildren());
				}
			}

			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.locationLevelName)){
				System.out.println("Location:" + child.getName());
				if(enabledLocation){
					if(child.getName().equals(location)){
						building = child;
						computeResult(child.getChildren());
					}
				}else{
					building = child;
					computeResult(child.getChildren());
					
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.componentLevelName)){
				System.out.println("Component:" + child.getName());
				if(enabledComponent){
					if(child.getName().equals(component)){
						componentlevel = child;
						computeResult(child.getChildren());
					}
				}else{
					componentlevel = child;
					computeResult(child.getChildren());
				}
			}
			else if(child.getJEVisClass()== jevis.getJEVisClass(
					DataBaseConfiguration.deviceLevelName)){
				System.out.println("Data:" + child.getName());
				if(enableddeviceType){
					if(child.getName().equals(devicetype)){
						list_projects.add(organization);
						list_datapoint.add(child);
						list_location.add(building);
						list_component.add(componentlevel);
						
						
					}else{
						System.out.println("No Datapoint found");
					}
				}else{
					list_projects.add(organization);
					list_datapoint.add(child);
					list_location.add(building);
					list_component.add(componentlevel);
				}	
			}

			else{
				computeResult(child.getChildren());				
			}
		}
	}
	
/*
 * Search for a specific Nodetype and filtering after Project, Location, or Component	
 */
	
	public void searchForNodeType2() throws JEVisException{
		List<JEVisObject> projects = jevis.getObjects(jevis.getJEVisClass(
				DataBaseConfiguration.projectLevelName), true);
		
		computeResultNodeType(projects);
	}
	
	
	public void computeResultNodeType(List<JEVisObject> children) throws JEVisException{
		for(JEVisObject child : children){			
			
			if(child.getJEVisClass() == jevis.getJEVisClass(
					DataBaseConfiguration.projectLevelName)){
				System.out.println("Project:" + child.getName());
				if(enabledProject){
					if(child.getName().equals(project)){
						
						organization = child;
						computeResultNodeType(child.getChildren());					
					}
				}else{

					organization = child;
					computeResultNodeType(child.getChildren());
				}
			}

			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.locationLevelName)){
				System.out.println("Location:" + child.getName());
				if(enabledLocation){
					if(child.getName().equals(location)){
						building = child;
						computeResultNodeType(child.getChildren());
					}
				}else{
					building = child;
					computeResultNodeType(child.getChildren());
					
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.componentLevelName)){
				System.out.println("Component:" + child.getName());
				if(enabledComponent){
					if(child.getName().equals(component)){
						componentlevel = child;
						computeResultNodeType(child.getChildren());
					}
				}else{
					componentlevel = child;
					computeResultNodeType(child.getChildren());
				}
			}
			else if(child.getJEVisClass() == jevis.getJEVisClass(nodeType)){
				list_nodetype.add(child);
				if(componentlevel != null){
					list_component.add(componentlevel);
				}
				if(organization !=null){
					list_projects.add(organization);
				}
				if(building != null){
					list_location.add(building);
				}
			}
			else{
				computeResultNodeType(child.getChildren());
			}
		}
	}
	
	
	
	
 
	
	
	/*
	 * Main Search function for information
	 */
	private List<JEVisObject> findParents(JEVisObject jObject, List<JEVisObject> list) throws JEVisException{

		list= jObject.getParents();
		for(JEVisObject listObject : list){			
			if(listObject.getJEVisClass().equals(jevis.getJEVisClass(
					DataBaseConfiguration.projectLevelName))){
				return list;
			}
			else{
				findParents(listObject,list);
			}
		}
		return list;
	}
	
	/*
	 * Useful Functions short functions.
	 */
	
	static boolean checkLevel(JEVisObject object, String level) throws JEVisException{
		if(object.getJEVisClass().equals(jevis.getJEVisClass(level))){
			return true;
		}
		return false;
	}


}
