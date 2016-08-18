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
import org.knime.core.data.DataTableSpec;

public class SearchForNodes {
	
	static JEVisDataSourceSQL jevis;
	String project;
	String location;
	String nodeType;
	String devicetype;
	String component;
	int nodeID;

	boolean enabledNodeType;
	boolean enabledAttribute;
	
	List<JEVisObject> list_projects = new ArrayList<JEVisObject>();
	List<JEVisObject> list_location = new ArrayList<JEVisObject>();
	List<JEVisObject> list_component = new ArrayList<JEVisObject>();
	List<JEVisObject> list_datapoint = new ArrayList<JEVisObject>();
	List<JEVisObject> list_attributes = new ArrayList<JEVisObject>();
	
	List<String> list_comment = new ArrayList<String>();
	
	String attribute1; 
	String attribute2;
	String attribute3; 
	String attribute4;
	
	String operator1;
	String operator2;
	String operator3;
	String operator4;
	
	String attributevalue1;
	String attributevalue2;
	String attributevalue3;
	String attributevalue4;
	
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

	String comment = " ";
	
	ArrayList<String> list_levels = new ArrayList<String>();
	
	
public SearchForNodes(JEVisDataSourceSQL jevis, 
		String project, String location, 
		String nodeType, String devicetype, String component,
			int nodeID, boolean enabledNodeType, boolean enabledAttribute,
			String attribute1, String attribute2,
			String attribute3, String attribute4, 
			String operator1, String operator2,
			String operator3, String operator4,
			String attributevalue1, String attributevalue2,
			String attributevalue3, String attributevalue4, 
			DataTableSpec spec) {
		super();
		this.jevis = jevis;
		this.project = project;
		this.location = location;
		this.nodeType = nodeType;
		this.devicetype = devicetype;
		this.component = component;
		this.nodeID = nodeID;
		this.enabledNodeType = enabledNodeType;
		this.enabledAttribute = enabledAttribute;
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
		this.attribute3 = attribute3;
		this.attribute4 = attribute4;
		this.operator1 = operator1;
		this.operator2 = operator2;
		this.operator3 = operator3;
		this.operator4 = operator4;
		this.attributevalue1 = attributevalue1;
		this.attributevalue2 = attributevalue2;
		this.attributevalue3 = attributevalue3;
		this.attributevalue4 = attributevalue4;
		this.result= spec;
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
				
				if(!project.equals(" ")|| startAttributeCheckforDatapoint(child)){
					if(child.getName().equals(project)){
						organization = child;			
						computeResult(child.getChildren());
					}
				}

				else if(startAttributeCheckforDatapoint(child) || enabledAttribute){
					organization = child;
					computeResult(child.getChildren());					
				}
			}
			else if(child.getJEVisClass().equals(jevis.getJEVisClass(
					DataBaseConfiguration.locationLevelName))){
				System.out.println("Location:" + child.getName());
	
				if(!location.equals(" ")){
					if(child.getName().equals(location)){
						
							building = child;
							computeResult(child.getChildren());
						
					}
				}
				else if(startAttributeCheckforDatapoint(child) || enabledAttribute){ 
					
					building = child;
					computeResult(child.getChildren());	
					
				}				
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.componentLevelName)){
				System.out.println("Component:" + child.getName());
				
				if(!component.equals(" ") || startAttributeCheckforDatapoint(child)){
					if(child.getName().equals(component)){
						componentlevel = child;
						computeResult(child.getChildren());
					}
				}
				else if(startAttributeCheckforDatapoint(child) || enabledAttribute){
					componentlevel = child;
					computeResult(child.getChildren());
					
				}
			}
			else if(child.getJEVisClass()== jevis.getJEVisClass(
					DataBaseConfiguration.deviceLevelName)){
				System.out.println("Data:" + child.getName());
				if(!devicetype.equals(" ")){
					if(child.getName().equals(devicetype)){
						list_projects.add(organization);
						list_datapoint.add(child);
						list_location.add(building);
						list_component.add(componentlevel);
						list_comment.add(comment);
						comment = " ";
					}else{
						System.out.println("No Datapoint found");
					}
				}else{
					list_projects.add(organization);
					list_datapoint.add(child);
					list_location.add(building);
					list_component.add(componentlevel);
					list_comment.add(comment);
					comment = " ";
				}	
			}	
			else{
				if(startAttributeCheckforDatapoint(child)){
					list_comment.add(child.getName()+ "("+child.getJEVisClass().
							toString()+")");
					computeResult(child.getChildren());
				}
				computeResult(child.getChildren());				
			}
		}	
	}

/*
 * Search for a specific Nodetype and filtering after Project, Location, or Component	
 */
	
	public void searchForNodeType() throws JEVisException{
		List<JEVisObject> projects = jevis.getObjects(jevis.getJEVisClass(
				DataBaseConfiguration.projectLevelName), true);
		
		computeResultNodeType(projects);
	}
	
	public void computeResultNodeType(List<JEVisObject> children) throws JEVisException{
		for(JEVisObject child : children){			

			if(child.getJEVisClass() == jevis.getJEVisClass(
					DataBaseConfiguration.projectLevelName)){
				System.out.println("Project:" + child.getName());
				if(!project.equals(" ")){
					if(child.getName().equals(project)){						
						organization = child;
						computeResultNodeType(child.getChildren());					
					}		
				}
				else{
					organization = child;
					computeResultNodeType(child.getChildren());
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					DataBaseConfiguration.locationLevelName)){
				System.out.println("Location:" + child.getName());
				if(!location.equals(" ")){
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
				if(!component.equals(" ")){
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

	private boolean startAttributeCheckforDatapoint(JEVisObject object) throws JEVisException{
		for(JEVisAttribute attribute : object.getAttributes()){
			if(checkAttributeforDataPoint(
					attribute, attribute1, attributevalue1, operator1)||
					checkAttributeforDataPoint(
							attribute, attribute2, attributevalue2, operator2)||
					checkAttributeforDataPoint(
							attribute, attribute3, attributevalue3, operator3)||
					checkAttributeforDataPoint(
							attribute, attribute4, attributevalue4, operator4)){
				return true;
			}else{
				return false;
			}
			
		}
		return false;
	}
	
	private boolean checkAttributeforDataPoint(
			JEVisAttribute attribute, String attributeName, String attributeValue, String operator)
			throws JEVisException{
		
		if(attribute.getName().equals(attributeName)){
			if(attribute.hasSample()){
				if(attribute.getLatestSample().getValue().
						toString().matches(".*" + attributeValue.trim() + ".*")
						&& operator.equals("contains")){
					JevisSelectDataNodeModel.logger.debug(" Contain accessed.");
					return true;
				}
				else if(attribute.getLatestSample().getValue().
						toString().matches(attributeValue.trim())
						&& operator.equals("equals")){
					JevisSelectDataNodeModel.logger.debug("Equals accessed. ");
					return true;
				}
				else{
					return false;
				}
			}else{
				return false;		
			}
		}else{
			return false;
		}
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
