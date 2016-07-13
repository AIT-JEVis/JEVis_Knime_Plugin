package org.ait.knimejevisplugin.selectdata;

import java.util.ArrayList;
import java.util.List;

import org.ait.knimejevisplugin.DataBaseConfiguration;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;

public class SearchForAttributes {

	int counter= 0;
	JEVisDataSourceSQL jevis;
	
	boolean enabledProject;
	String projectLevelName;
	
	boolean enabledLocation;
	String locationLevelName; 
	
	boolean enabledComponent;
	String componentLevelName;
	
	boolean enabledNodeType;
	String nodeType;
	
	JEVisObject organization = null; 
	JEVisObject building = null;
	JEVisObject componentlevelobject = null;
	
	List<JEVisObject> list_projects = new ArrayList<JEVisObject>();
	List<JEVisObject> list_location = new ArrayList<JEVisObject>();
	List<JEVisObject> list_component = new ArrayList<JEVisObject>();
	List<JEVisObject> list_attributes = new ArrayList<JEVisObject>();
	
	String attribute1; 
	String attribute2;
	String attribute3; 
	String attribute4;
	
	DataTableSpec spec;
	
	DataCell[] cells;
	
	public void searchforAttributesEntry() throws JEVisException{
		List<JEVisObject> list = jevis.getObjects(
				jevis.getJEVisClass(DataBaseConfiguration.projectLevelName), true);
	
		computeResultAttributes(list);
	}
	
	
	private void computeResultAttributes(List<JEVisObject> children) throws JEVisException{
		for(JEVisObject child : children){			
			List<JEVisAttribute> attributes = child.getAttributes();
			for(JEVisAttribute attribute : attributes){
				if(attribute.getName() == attribute1){
					list_attributes.add(child);
				}
			}
			if(child.getJEVisClass() == jevis.getJEVisClass(
					DataBaseConfiguration.projectLevelName)){
				System.out.println("Project:" + child.getName());
				if(enabledProject){
					if(child.getName().equals(DataBaseConfiguration.projectLevelName)){
						
						organization = child;
						computeResultAttributes(child.getChildren());
					}
				}else{

					organization = child;
					computeResultAttributes(child.getChildren());
				}
			}

			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.locationLevelName)){
				System.out.println("Location:" + child.getName());
				if(enabledLocation){
					if(child.getName().equals(DataBaseConfiguration.locationLevelName)){
						building = child;
						computeResultAttributes(child.getChildren());
					}
				}else{
					building = child;
					computeResultAttributes(child.getChildren());
					
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.componentLevelName)){
				System.out.println("Component:" + child.getName());
				if(enabledComponent){
					if(child.getName().equals(DataBaseConfiguration.componentLevelName)){
						componentlevelobject = child;
						computeResultAttributes(child.getChildren());
					}
				}else{
					componentlevelobject = child;
					computeResultAttributes(child.getChildren());
				}
			}
			else if((child.getAttributes().contains(attribute1) ||
					child.getAttributes().contains(attribute2) ||
					child.getAttributes().contains(attribute3) ||
					child.getAttributes().contains(attribute4)) &&
					( 
					child.getAttribute(attribute1).hasSample() ||
					child.getAttribute(attribute2).hasSample() ||
					child.getAttribute(attribute3).hasSample() ||
					child.getAttribute(attribute4).hasSample())){
					// TODO insert Method. 
			}
				
			/*
			else if(child.getJEVisClass()== jevis.getJEVisClass(
					DataBaseConfiguration.attributeslevelName){
				System.out.println("Data:" + child.getName());
				
					if(child.getName().equals(devicetype)){
						list_projects.add(organization);
						list_datapoint.add(child);
						list_location.add(building);
						list_component.add(componentlevelobject);
						
						
					}else{
						System.out.println("No Datapoint found");
				}	
			}
			*/
			else{
				computeResultAttributes(child.getChildren());				
			}
		}
	}
	
}
