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

import com.jcraft.jsch.Logger;

public class SearchForAttributes {
	
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
	DataTableSpec result;
	DataCell[]cells;
	int counter = 0;
	
	ArrayList<String> list_levels = new ArrayList<String>();
	
	public SearchForAttributes(JEVisDataSourceSQL jevis, String project, String location, String nodeType, String devicetype,
			String component, int nodeID, boolean parent, boolean children, boolean siblings, boolean allChildren,
			boolean enabledProject, boolean enabledLocation, boolean enabledNodeType, boolean enableddeviceType,
			boolean enabledComponent, DataTableSpec result) {
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
		this.result = result;

	}
	
	private List<JEVisObject> searchNodetype() throws JEVisException{
		List<JEVisObject> list_nodetype = jevis.getObjects(jevis.getJEVisClass(nodeType), true);
		return list_nodetype;
		
	}


	public BufferedDataContainer searchData(BufferedDataContainer buf) throws JEVisException{
		counter = 0;
		cells = new DataCell[result.getNumColumns()];
		List<JEVisObject> projects = jevis.getObjects(jevis.getJEVisClass("Organization"), true);
		checkLevel(projects,buf);
		return buf;
		
	}
	public BufferedDataContainer checkLevel(List<JEVisObject> children, BufferedDataContainer buf) throws JEVisException{
		
		for(JEVisObject child : children){
			if(child.getJEVisClass() == jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.projectLevelName)){
				System.out.println("Project:" + child.getName());
				if(enabledProject){
					if(child.getName().equals(project)){
				
						cells[2] = new StringCell(child.getName());
						checkLevel(child.getChildren(), buf);					
					}
				}else{
					cells[2] = new StringCell(child.getName());		
					checkLevel(child.getChildren(), buf);
				}
			}

			else if(child.getJEVisClass()==jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.locationLevelName)){
				System.out.println("Location:" + child.getName());
				if(enabledLocation){
					if(child.getName().equals(location)){
						cells[3] = new StringCell(child.getName());
						checkLevel(child.getChildren(), buf);
					}
				}else{
					cells[3] = new StringCell(child.getName());
					checkLevel(child.getChildren(), buf);
					
				}
			}
			else if(child.getJEVisClass()==jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.componentLevelName)){
				System.out.println("Component:" + child.getName());
				if(enabledComponent){
					if(child.getName().equals(component)){
						cells[4] = new StringCell(child.getName());
						checkLevel(child.getChildren(), buf);
					}
				}else{
					cells[4] = new StringCell(child.getName());
					checkLevel(child.getChildren(), buf);
				}
			}
			else if(child.getJEVisClass()== jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.deviceLevelName)){
				System.out.println("Data:" + child.getName());
				if(enableddeviceType){
					if(child.getName().equals(devicetype)){
			          buf = filter(child, buf);
					}else{
						System.out.println("No Datapoint found");
					}
				}else{
					buf = filter(child, buf);
				}	
			}
	/*		else if(child.getJEVisClass()== jevis.getJEVisClass(nodeType)){
				System.out.println("NodeType:"+ child.getName());
				if(enabledNodeType){
					searchresult.add(child);
				}
			}*/
			else{
				checkLevel(child.getChildren(), buf);				
			}
			
		}
		return buf;
	}
	
	private BufferedDataContainer filter(JEVisObject child, BufferedDataContainer buf ) throws JEVisException{
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
	

	private BufferedDataContainer fillTable(DataCell[] cells,BufferedDataContainer buf){
		counter++;
		DataRow row = new DefaultRow("Row"+counter, cells);
		buf.addRowToTable(row);
        return buf;
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
