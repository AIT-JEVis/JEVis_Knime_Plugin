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
	List<String> list_comment = new ArrayList<String>();
	
	String attribute1; 
	String attribute2;
	String attribute3; 
	String attribute4;
	
	String attributevalue1;
	String attributevalue2;
	String attributevalue3;
	String attributevalue4;
	
	DataTableSpec spec;
	
	DataCell[] cells;
	
	//constructor
	
	public SearchForAttributes(JEVisDataSourceSQL jevis, boolean enabledProject, String projectLevelName,
			boolean enabledLocation, String locationLevelName, boolean enabledComponent, String componentLevelName,
			String attribute1, String attribute2, String attribute3, String attribute4, String attributevalue1,
			String attributevalue2, String attributevalue3, String attributevalue4, DataTableSpec spec) {
		super();
		this.jevis = jevis;
		this.enabledProject = enabledProject;
		this.projectLevelName = projectLevelName;
		this.enabledLocation = enabledLocation;
		this.locationLevelName = locationLevelName;
		this.enabledComponent = enabledComponent;
		this.componentLevelName = componentLevelName;
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
		this.attribute3 = attribute3;
		this.attribute4 = attribute4;
		this.attributevalue1 = attributevalue1;
		this.attributevalue2 = attributevalue2;
		this.attributevalue3 = attributevalue3;
		this.attributevalue4 = attributevalue4;
		this.spec = spec;
	}

	public void searchforAttributesEntry() throws JEVisException{
		List<JEVisObject> list = jevis.getObjects(
				jevis.getJEVisClass(DataBaseConfiguration.projectLevelName), true);
	
		computeResultAttributes(list);
	}
	
	private void computeResultAttributes(List<JEVisObject> children) throws JEVisException{
		
		for(JEVisObject child : children){		
			
			checkAttributes(child);
			
			if(child.getJEVisClass() == jevis.getJEVisClass(
					DataBaseConfiguration.projectLevelName)){
				System.out.println("Project:" + child.getName());
					organization = child;
					computeResultAttributes(child.getChildren());
			}

			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.locationLevelName)){
				System.out.println("Location:" + child.getName());

					building = child;
					computeResultAttributes(child.getChildren());
					
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.componentLevelName)){
				System.out.println("Component:" + child.getName());

					componentlevelobject = child;
					computeResultAttributes(child.getChildren());
				
			}

			else if(child.getJEVisClass()== jevis.getJEVisClass(
					DataBaseConfiguration.deviceLevelName)){
					list_projects.add(organization);
					list_location.add(building);
					list_component.add(componentlevelobject);
			}
			else{
				computeResultAttributes(child.getChildren());				
			}
		}
	}
	
	
	 protected void checkAttributes(JEVisObject child) throws JEVisException{
		List<JEVisAttribute> attributes = child.getAttributes();
		for(JEVisAttribute attribute : attributes){
			attributesValueCheck(child, attribute, attributevalue1, attribute1);
			attributesValueCheck(child, attribute, attributevalue2, attribute2);
			attributesValueCheck(child, attribute, attributevalue3, attribute3);
			attributesValueCheck(child, attribute, attributevalue4, attribute4);
		}
	}
	
	private void attributesValueCheck(JEVisObject child,
			JEVisAttribute attribute, String attributevalue, String attributeName) 
					throws JEVisException{
		if(attribute.getName().equals(attributeName)){
			if(attribute.hasSample()){
				if((!list_attributes.contains(child)) && 
						attribute.getLatestSample().getValue().equals(attributevalue)){
					list_attributes.add(child);
					list_comment.add(" ");
				}		
			}
			else{
				if(!list_attributes.contains(child)){	
					list_attributes.add(child);
					list_comment.add("No Sample in attribute");
				}
			}
		}
	}
}
