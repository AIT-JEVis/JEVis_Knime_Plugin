package org.ait.knimejevisplugin.selectdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.cli.CommonCLIOptions.JEVis;
import org.jscience.geography.coordinates.crs.ProjectedCRS;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;

import javafx.scene.control.DialogPane;

/**
 * <code>NodeDialog</code> for the "JevisSelectData" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Monschiebl
 */
public class JevisSelectDataNodeDialog extends DefaultNodeSettingsPane {
	
	ArrayList<String> nodefilter = new ArrayList<String>();
	ArrayList<String> devicetypes = new ArrayList<String>();
	ArrayList<String> components = new ArrayList<String>();
	ArrayList<String> nodetypes = new ArrayList<String>();
	
	private static final Logger logger = LogManager.getLogger("SelectNdoeDialog");
	
	private JEVisDataSourceSQL jevis;
	
    private final SettingsModelString jhost = new SettingsModelString(
    		JevisSelectDataNodeModel.host, "jevis3.ait.ac.at");
    private final SettingsModelString jport = new SettingsModelString(
    		JevisSelectDataNodeModel.port, "3306");
   	private final SettingsModelString jSchema = new SettingsModelString(
   			JevisSelectDataNodeModel.sqlSchema, "jevis");
   	private final SettingsModelString jUser = new SettingsModelString(
   			JevisSelectDataNodeModel.sqlUser, "jevis");
   	private final SettingsModelString jPW = new SettingsModelString(
   			JevisSelectDataNodeModel.sqlPW, "vu5eS1ma");
   	
   	private final SettingsModelString jevUser = new SettingsModelString(
   			JevisSelectDataNodeModel.jevisUser, "BerhnardM");
   	private final SettingsModelString jevPW = new SettingsModelString(
   			JevisSelectDataNodeModel.jevisPW,"testpass01593");
	
   	private final SettingsModelBoolean enableAttribute = new SettingsModelBoolean("ENABLE", false);
   	
   	private final SettingsModelString m_project = new SettingsModelString(
   			JevisSelectDataNodeModel.project,"");
   	private final SettingsModelString m_location = new SettingsModelString(
   			JevisSelectDataNodeModel.location,"");
   	private final SettingsModelString m_nodeType = new SettingsModelString(
   			JevisSelectDataNodeModel.nodeType,"");
   	private final SettingsModelString m_devicetype = new SettingsModelString(
   			JevisSelectDataNodeModel.devicetype,"");
   	private final SettingsModelString m_component = new SettingsModelString(
   			JevisSelectDataNodeModel.component,"");
   	private final SettingsModelString m_searchNodeType = new SettingsModelString(
   			JevisSelectDataNodeModel.searchNodeType, " ");
   	private final SettingsModelString m_searchDeviceType = new SettingsModelString(
   			JevisSelectDataNodeModel.searchDeviceType, " ");
   	private final SettingsModelString m_searchComponentType = new SettingsModelString(
   			JevisSelectDataNodeModel.searchComponentType, " ");
   	
   	private final SettingsModelBoolean m_parents = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.parent, false);
   	private final SettingsModelBoolean m_children = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.children, false);
   	private final SettingsModelBoolean m_siblings = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.siblings, false);
   	private final SettingsModelBoolean m_allChildren = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.allChildren, false);
    /**
     * New pane for configuring the JevisSelectData node.
     */
    protected JevisSelectDataNodeDialog() {
    	JevisSelectDataNodeModel.logger.setLevel(NodeLogger.LEVEL.INFO);
    	JevisSelectDataNodeModel.logger.warn("Opening Configuration Window. Please be patient it may take a moment.");
    	connectingtojevis();
    	getnodetypes(jevis, nodefilter);
    	getdevicetypes(jevis, devicetypes);
    	getcomponents(jevis, components);
    	
    	createNewGroup("Database Connection Settings");
    	setHorizontalPlacement(false);
    	addDialogComponent(new DialogComponentString(jhost, "Hostaddress"));
    	addDialogComponent(new DialogComponentString(jport, "Port"));
    	addDialogComponent(new DialogComponentString(jSchema, "SqlSchema"));
    	addDialogComponent(new DialogComponentString(jUser,"SqlUser"));
    	addDialogComponent(new DialogComponentString(jPW, "SqlPassword"));
    	createNewGroup("Jevis User Information");
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
    			JevisSelectDataNodeModel.jevisUser,"BerhnardM"), "JevisUser Name"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
    			JevisSelectDataNodeModel.jevisPW,"testpass01593"), "Jevis Password"));
    	closeCurrentGroup();
    	setDefaultTabTitle("Configure Connection");
    	
    	createNewTabAt("Filter Output",1);
    	createNewGroup("Search through attributes");
    	addDialogComponent(new DialogComponentBoolean(enableAttribute, "disable Attribute Search"));
    	enableAttribute.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
		    	m_nodeType.setEnabled(enableAttribute.getBooleanValue());
		    	m_devicetype.setEnabled(enableAttribute.getBooleanValue());
		    	m_component.setEnabled(enableAttribute.getBooleanValue());
		    	m_location.setEnabled(enableAttribute.getBooleanValue());
		    	m_searchDeviceType.setEnabled(enableAttribute.getBooleanValue());
		    	m_searchNodeType.setEnabled(enableAttribute.getBooleanValue());
		    	m_searchComponentType.setEnabled(enableAttribute.getBooleanValue());
		    	m_project.setEnabled(enableAttribute.getBooleanValue());
		    	
			}
		});
    	DialogComponentStringSelection diac_nodeType = new  DialogComponentStringSelection(m_nodeType, "NodeType", nodefilter);

    	//Searching for Attributes like project, location, nodeType, device and component
    	addDialogComponent(new DialogComponentString(m_project, "Project"));
    	addDialogComponent(new DialogComponentString(m_location, "Location"));
    	setHorizontalPlacement(true);
    	addDialogComponent(diac_nodeType);
    	//addDialogComponent(new DialogComponentString(m_searchNodeType," "));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentStringSelection(m_devicetype, "Device", devicetypes));
    	//addDialogComponent(new DialogComponentString(m_searchDeviceType, " "));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentStringSelection(m_component, "Component", components));
    	//addDialogComponent(new DialogComponentString(m_searchComponentType, " "));
    	
    	createNewGroup("Search with structure");
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_parents, "Parents"));
    	addDialogComponent(new DialogComponentBoolean(m_children, "Children"));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_siblings, "Siblings"));
    	addDialogComponent(new DialogComponentBoolean(m_allChildren, "AllChildren"));
    	closeCurrentGroup();
    	
    	addDialogComponent(new DialogComponentLabel("Search! "));
    	
    	
    	
    	//getsearchinformation(jevis);
    }
    
    
    private void connectingtojevis(){
    	
	    try{
		//Connecting to Jevis with connection information
		jevis = new JEVisDataSourceSQL(jhost.getStringValue(), jport.getStringValue(), jSchema.getStringValue(), jUser.getStringValue(), jPW.getStringValue());
		jevis.connect(jevUser.getStringValue(), jevPW.getStringValue());
		
		
		}catch(JEVisException e){
			e.printStackTrace();
			logger.error("Connection error! Check Jevis settings and try again!");
		}
    }
    
    public void getnodetypes(JEVisDataSourceSQL jevis, ArrayList<String> nodefilter){
    	try{
    		if(jevis.isConnectionAlive()){
    			for(int i=0; i<jevis.getJEVisClasses().size(); i++){
        			nodefilter.add(jevis.getJEVisClasses().get(i).getName());
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    }
    
    public void getdevicetypes(JEVisDataSourceSQL jevis, ArrayList<String> devicetypes){
    	try{
    		if(jevis.isConnectionAlive()){
    			//filling device type string selection list:
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass("Data"), true).size();i++){
    				if(!devicetypes.contains(jevis.getObjects(jevis.getJEVisClass("Data"), true).
    						get(i).getName())){
    					devicetypes.add(jevis.getObjects(jevis.getJEVisClass("Data"), true).
    							get(i).getName());
    
    				}
    			}
    			
    			Map<String, List<JEVisObject>> jevisObjects = new HashMap<String, List<JEVisObject>>();
    			for(int i=0; i<jevis.getJEVisClasses().size();i++){
    				jevisObjects.put(jevis.getJEVisClasses().get(i).getName(),
    						jevis.getObjects(jevis.getJEVisClasses().get(i),true));
    			}
    				//Currently don't remember what that should do. Have to think about. 
    				/*
					List<JEVisObject> list_devices = jevis.getObjects(
							jevis.getJEVisClass("Data"), true);
					list_devices.get(i).getID();
					*/
				
    			
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    }
    
    public void getcomponents(JEVisDataSourceSQL jevis, ArrayList<String> components){
    	
    	try{
    		if(jevis.isConnectionAlive()){
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass("Device"), true).size();i++){
    				if(!components.contains(jevis.getObjects(jevis.getJEVisClass("Device"), true).
    						get(i).getName())){
    					components.add(jevis.getObjects(jevis.getJEVisClass("Device"), true).
    							get(i).getName());
    
    				}
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    	
    }
    
    private void filtering(DialogComponentStringSelection diac_nodeType){
    	
    	createNewGroup("Search for Attributes");
    	setHorizontalPlacement(false);

    }
    
    public void getsearchinformation(JEVisDataSourceSQL jevis){
    	
    	try {
			if(jevis.isConnectionAlive()){
				for(int i= 0 ; i<jevis.getObjects(jevis.getJEVisClass("Data"), true).size(); i++) {
						//logger.error(jevis.getObjects(jevis.getJEVisClass("Data"), true).
							//	get(i).getName());
						
						/*
						if(myObject.getJEVisClass().equals(jevis.getJEVisClass("Organization"))){
							ArrayList<JEVisObject> projects = new ArrayList<JEVisObject>();
							projects.add(myObject);
							logger.error("Searching Object: Class:  " + myObject.getJEVisClass().toString()
									+"sortied into: " + projects.toString() );
						}
						else if(myObject.getJEVisClass().equals(jevis.getJEVisClass("Building"))){
							ArrayList<JEVisObject> buildings = new ArrayList<JEVisObject>();
							buildings.add(myObject);
						}
						else if(myObject.getJEVisClass().equals(jevis.getJEVisClass("Data"))){
							ArrayList<JEVisObject> datapoints = new ArrayList<JEVisObject>();
							datapoints.add(myObject);
						}*/
							
				}
				for(int i=0; i<jevis.getJEVisClasses().size();i++){
					//logger.error(jevis.getJEVisClasses().get(i).getName());
					
				}
				for(int i= 0 ; i<jevis.getObjects(jevis.getJEVisClass(
						jevis.getJEVisClasses().get(i).getName()), true).size(); i++) {
					/*logger.error(jevis.getObjects(
							jevis.getJEVisClass(jevis.getJEVisClasses().get(i).getName()), true).
							get(i).getName());
							*/
				}
			}
			
    	}catch (JEVisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

