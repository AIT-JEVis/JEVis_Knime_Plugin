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
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;

public class SearchForNodes {
	
	JEVisDataSourceSQL jevis;
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
	
	List<JEVisObject> searchresult;
	JEVisObject projectObject;
	JEVisObject locationObject;
	JEVisObject componentObject;
	DataTableSpec result;
	DataCell[]cells;
	int counter = 0;
	
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
	public BufferedDataContainer searchforDataPoints(BufferedDataContainer buf) throws JEVisException{
		counter = 0;
		cells = new DataCell[result.getNumColumns()];
		List<JEVisObject> projects = jevis.getObjects(jevis.getJEVisClass(
				JevisSelectDataNodeModel.configuration.projectLevelName), true);
		computeResultTable(projects,buf);
		return buf;
		
	}
	
	public BufferedDataContainer computeResultTable(List<JEVisObject> children, BufferedDataContainer buf)
			throws JEVisException{
		
		for(JEVisObject child : children){
			
			if(child.getJEVisClass() == jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.projectLevelName)){
				System.out.println("Project:" + child.getName());
				if(enabledProject){
					if(child.getName().equals(project)){
				
						cells[2] = new StringCell(child.getName());
						computeResultTable(child.getChildren(), buf);					
					}
				}else{
					cells[2] = new StringCell(child.getName());		
					computeResultTable(child.getChildren(), buf);
				}
			}

			else if(child.getJEVisClass()==jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.locationLevelName)){
				System.out.println("Location:" + child.getName());
				if(enabledLocation){
					if(child.getName().equals(location)){
						cells[3] = new StringCell(child.getName());
						computeResultTable(child.getChildren(), buf);
					}
				}else{
					cells[3] = new StringCell(child.getName());
					computeResultTable(child.getChildren(), buf);
					
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.componentLevelName)){
				System.out.println("Component:" + child.getName());
				if(enabledComponent){
					if(child.getName().equals(component)){
						cells[4] = new StringCell(child.getName());
						computeResultTable(child.getChildren(), buf);
					}
				}else{
					cells[4] = new StringCell(child.getName());
					computeResultTable(child.getChildren(), buf);
				}
			}
			else if(child.getJEVisClass()== jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.deviceLevelName)){
				System.out.println("Data:" + child.getName());
				if(enableddeviceType){
					if(child.getName().equals(devicetype)){
			          buf = fillTableWithDatapointInformation(child, buf);
					}else{
						System.out.println("No Datapoint found");
					}
				}else{
					buf = fillTableWithDatapointInformation(child, buf);
				}	
			}

			else{
				computeResultTable(child.getChildren(), buf);				
			}
			
		}
		return buf;
	}
	
	private BufferedDataContainer fillTableWithDatapointInformation(
			JEVisObject child, BufferedDataContainer buf ) throws JEVisException{
		cells[0] = new LongCell(child.getID());
        cells[1] = new StringCell(child.getName());
        cells[5] = new StringCell(" ");
        if(child.getAttributes()== null){
        	cells[6] = new DateAndTimeCell(0, 0, 0);
        	cells[7]= new DateAndTimeCell(0, 0, 0);
        }
        else if(child.getAttribute(JevisSelectDataNodeModel.configuration.valueAttributeName) != null){
        	if(child.getAttribute(JevisSelectDataNodeModel.configuration.valueAttributeName).hasSample()){
        		cells[6] = new DateAndTimeCell(
		 	            		child.getAttribute(
		 	            				JevisSelectDataNodeModel.configuration.valueAttributeName)
		 	            					.getTimestampFromFirstSample().getYear(),
		 	            		child.getAttribute(
		 	            				JevisSelectDataNodeModel.configuration.valueAttributeName)
		 	            					.getTimestampFromFirstSample().getMonthOfYear(), 
		 	            		child.getAttribute(
		 	            				JevisSelectDataNodeModel.configuration.valueAttributeName)
		 	            					.getTimestampFromFirstSample().getDayOfMonth());
         		cells[7] = new DateAndTimeCell(
		 	            		child.getAttribute(
		 	            				JevisSelectDataNodeModel.configuration.valueAttributeName)
		 	            					.getTimestampFromLastSample().getYear(),
		 	            		child.getAttribute(
		 	            				JevisSelectDataNodeModel.configuration.valueAttributeName)
		 	            					.getTimestampFromLastSample().getMonthOfYear(), 
		 	            		child.getAttribute(
		 	            				JevisSelectDataNodeModel.configuration.valueAttributeName)
		 	            					.getTimestampFromLastSample().getDayOfMonth());
         	}
         	else{
	            cells[6] = new DateAndTimeCell(0, 0, 0);
	            cells[7] = new DateAndTimeCell(0, 0, 0);
         	}
        }else{
         		cells[6] = new DateAndTimeCell(0, 0, 0);
         		cells[7] = new DateAndTimeCell(0, 0, 0);
        }
		fillTable(cells, buf);
		return buf;
	}
	


	
/*
 * Search for a specific Nodetype and filtering after Project, Location, or Component	
 */
	
	public BufferedDataContainer fillTableWithNodetypeSearchResult(BufferedDataContainer buf, DataTableSpec result) throws JEVisException{
		DataCell[] cells = new DataCell[result.getNumColumns()];
		int counter = 0;
		for(JEVisObject foundObject : searchNodetype()){
			cells[0] = new LongCell(foundObject.getID());
			cells[1] = new StringCell(foundObject.getName());
			cells[2] = new StringCell(foundObject.getJEVisClass().getName());
			counter++;
			DataRow row = new DefaultRow("Row"+ counter, cells);
			buf.addRowToTable(row);
		}
		return buf;
	}
	
	
	
	public List<JEVisObject> searchNodetype() throws JEVisException{
		List<JEVisObject> list_nodetype = jevis.getObjects(jevis.getJEVisClass(nodeType), true);
		for(JEVisObject node : list_nodetype){
			getParentData(node, list_nodetype);
		}
		
		return list_nodetype;
		
	}
	
	private void getParentData(JEVisObject node, List<JEVisObject> list_nodeType) throws JEVisException{
		if(!checkLevel(node, JevisSelectDataNodeModel.configuration.projectLevelName)){
			if(checkLevel(node, JevisSelectDataNodeModel.configuration.locationLevelName)){
				searchNodeTypeList(node, enabledLocation, location, list_nodeType);
			}
			else if(checkLevel(node, JevisSelectDataNodeModel.configuration.componentLevelName)){
				searchNodeTypeList(node, enabledComponent, component, list_nodeType);
			}
			else{
				for(JEVisObject parent : node.getParents()){
					getParentData(parent, list_nodeType);
				}
			}
		}else{
			searchNodeTypeList(node, enabledProject, project, list_nodeType);
		}
	}
	
	private void searchNodeTypeList(
			JEVisObject object, boolean enabled, String objectName, List<JEVisObject> list_nodeType) 
			throws JEVisException{
		if(enabled){
			if(!object.getName().equals(objectName)){
				list_nodeType.remove(object);
			}
		}
	}
	
	/*
	 * Useful Functions short functions.
	 */
	
	private boolean checkLevel(JEVisObject object, String level) throws JEVisException{
		if(object.getJEVisClass().equals(jevis.getJEVisClass(level))){
			return true;
		}
		return false;
	}
	
	
	private BufferedDataContainer fillTable(DataCell[] cells,BufferedDataContainer buf){
		counter++;
		DataRow row = new DefaultRow("Row"+counter, cells);
		buf.addRowToTable(row);
        return buf;
	}
	
}
