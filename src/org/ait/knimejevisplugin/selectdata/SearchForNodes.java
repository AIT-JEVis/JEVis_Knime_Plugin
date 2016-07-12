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

		computeResultTable(projects);
		
	}
	

	
	public void computeResultTable(List<JEVisObject> children)
			throws JEVisException{
		
		for(JEVisObject child : children){			
			
			if(child.getJEVisClass() == jevis.getJEVisClass(
					DataBaseConfiguration.projectLevelName)){
				System.out.println("Project:" + child.getName());
				if(enabledProject){
					if(child.getName().equals(project)){
						
						organization = child;
						computeResultTable(child.getChildren());					
					}
				}else{

					organization = child;
					computeResultTable(child.getChildren());
				}
			}

			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.locationLevelName)){
				System.out.println("Location:" + child.getName());
				if(enabledLocation){
					if(child.getName().equals(location)){
						building = child;
						computeResultTable(child.getChildren());
					}
				}else{
					building = child;
					computeResultTable(child.getChildren());
					
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.componentLevelName)){
				System.out.println("Component:" + child.getName());
				if(enabledComponent){
					if(child.getName().equals(component)){
						componentlevel = child;
						computeResultTable(child.getChildren());
					}
				}else{
					componentlevel = child;
					computeResultTable(child.getChildren());
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
				computeResultTable(child.getChildren());				
			}
			
		}
	
	}
	
/*
 * Search for a specific Nodetype and filtering after Project, Location, or Component	
 */
	
	public List<JEVisObject> searchForNodeType() throws JEVisException{
		List<JEVisObject> liste = searchNodetype();
		return liste;
	}

	public List<JEVisObject> searchNodetype() throws JEVisException{
		List<JEVisObject> list_nodetype = jevis.getObjects(jevis.getJEVisClass(nodeType), true);
		for(JEVisObject node : list_nodetype){
			getParentData(node, list_nodetype);
		}
		
		return list_nodetype;
	}
	
	private void getParentData(JEVisObject node, List<JEVisObject> list_nodeType) 
			throws JEVisException{
		if(!checkLevel(node, DataBaseConfiguration.projectLevelName)){
			if(checkLevel(node, DataBaseConfiguration.locationLevelName)){
				searchNodeTypeList(node, enabledLocation, location, 
						list_nodeType, list_location);
			}
			else if(checkLevel(node, DataBaseConfiguration.componentLevelName)){
				searchNodeTypeList(node, enabledComponent, component, 
						list_nodeType, list_component);
			}
			else{
				for(JEVisObject parent : node.getParents()){
					getParentData(parent, list_nodeType);
				}
			}
		}else{
			searchNodeTypeList(node, enabledProject, project, list_nodeType, list_projects);
		}
	}
	
	private void searchNodeTypeList(
			JEVisObject object, boolean enabled, String objectName, 
			List<JEVisObject> list_nodeType, List<JEVisObject> list_Level) 
			throws JEVisException{
		if(enabled){
			if(!object.getName().equals(objectName)){
				list_nodeType.remove(object);
			}
			else{
				list_Level.add(object);
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
