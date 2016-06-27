package org.ait.knimejevisplugin.selectdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
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
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;
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
	ArrayList<String> attributes = new ArrayList<String>();

	private static final Logger logger = LogManager.getLogger("SelectNdoeDialog");
	
	private JEVisDataSourceSQL jevis;
	
    private final SettingsModelString jhost = new SettingsModelString(
    		JevisSelectDataNodeModel.configuration.hostModelName,
    		JevisSelectDataNodeModel.configuration.DEFAULT_Host);
    private final SettingsModelString jport = new SettingsModelString(
    		JevisSelectDataNodeModel.configuration.portModelName,
    		JevisSelectDataNodeModel.configuration.DEFAULT_port);
   	private final SettingsModelString jSchema = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.sqlSchemaModelName,
   			JevisSelectDataNodeModel.configuration.DEFAULT_sqlShema);
   	private final SettingsModelString jUser = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.sqlUserModelName,
   			JevisSelectDataNodeModel.configuration.DEFAULT_sqlUserName);
   	private final SettingsModelString jPW = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.sqlPWModelName, 
   			JevisSelectDataNodeModel.configuration.DEFAULT_sqlPW);
   	
   	private final SettingsModelString jevUser = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.jevisUserModelName, 
   			JevisSelectDataNodeModel.configuration.DEFAULT_jevisUserName);
   	private final SettingsModelString jevPW = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.jevisPWModelName,
   			JevisSelectDataNodeModel.configuration.DEFAULT_jevisPW);
	
   	private final SettingsModelBoolean m_enableNodeSearch = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableNodeSearch, true);
   	
   	private final SettingsModelString m_project = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.projectModelName,"");
   	private final SettingsModelString m_location = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.locationModelName," ");
   	private final SettingsModelString m_nodeType = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.nodeType," ");
   	private final SettingsModelString m_devicetype = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.deviceModelName," ");
   	private final SettingsModelString m_component = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.componentModelName," ");
   	
   	private final SettingsModelBoolean m_enableProject = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableProject, true);
   	private final SettingsModelBoolean m_enableLocation = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableLocation, true);
   	private final SettingsModelBoolean m_enableNodeType = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableNodeType, true);
   	private final SettingsModelBoolean m_enableComponent = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableComponent, true);
   	private final SettingsModelBoolean m_enableDevice = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableDevice, true);
   	
   	private final SettingsModelBoolean m_enableAttribute = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableAttributeSearch, true);
   	
   	private final SettingsModelString m_AttributeSearch = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelName, " ");
   	
   	private final SettingsModelBoolean m_enableStructure = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableStructure, true);
   	
   	private final SettingsModelLong m_nodeId = new SettingsModelLong(
   			JevisSelectDataNodeModel.nodeID , 0);
   	
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
    	getAttributes(jevis, attributes);
    	
    	createNewGroup("Database Connection Settings");
    	setHorizontalPlacement(false);
    	addDialogComponent(new DialogComponentString(jhost, "Hostaddress"));
    	addDialogComponent(new DialogComponentString(jport, "Port"));
    	addDialogComponent(new DialogComponentString(jSchema, "SqlSchema"));
    	addDialogComponent(new DialogComponentString(jUser,"SqlUser"));
    	addDialogComponent(new DialogComponentString(jPW, "SqlPassword"));
    	createNewGroup("Jevis User Information");
    	addDialogComponent(new DialogComponentString(jevUser, "JevisUser Name"));
    	addDialogComponent(new DialogComponentString(jevPW, "Jevis Password"));
    	closeCurrentGroup();
    	setDefaultTabTitle("Configure Connection");
    	
    	createNewTabAt("Filter Output",1);
    	createNewGroup("Search through Nodes");
    	addDialogComponent(new DialogComponentBoolean(m_enableNodeSearch, "enable Specific Node Search"));
    	m_enableNodeSearch.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				
		    	m_nodeType.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_devicetype.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_component.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_location.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_project.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	
			}
		});
    	DialogComponentStringSelection diac_nodeType = new DialogComponentStringSelection(
    			m_nodeType, "NodeType", nodefilter);
    	
    	//Searching for Attributes like project, location, nodeType, device and component
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableProject, "enable Project search:"));
    	m_enableProject.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_project.setEnabled(m_enableProject.getBooleanValue());
			}
		});
    	addDialogComponent(new DialogComponentString(m_project, "Project"));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableLocation, "enable location search:"));
    	m_enableLocation.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_location.setEnabled(m_enableLocation.getBooleanValue());
			}
		});
    	addDialogComponent(new DialogComponentString(m_location, "Location"));

    	//addDialogComponent(new DialogComponentString(m_searchNodeType," "));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableDevice, "Enable Device Search"));
    	m_enableDevice.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_devicetype.setEnabled(m_enableDevice.getBooleanValue());
			}
		});
    	addDialogComponent(new DialogComponentStringSelection(m_devicetype, "Device", devicetypes));
    	//addDialogComponent(new DialogComponentString(m_searchDeviceType, " "));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableComponent, "Enable Component Search"));
    	m_enableComponent.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				m_component.setEnabled(m_enableComponent.getBooleanValue());
			}
		});
    	addDialogComponent(new DialogComponentStringSelection(m_component, "Component", components));
    	//addDialogComponent(new DialogComponentString(m_searchComponentType, " "));
    	createNewGroup("Search for specific Nodetype");
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableNodeType, "Enable Nodetype"));
    	m_enableNodeType.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_nodeType.setEnabled(m_enableNodeType.getBooleanValue());
			}
		});
    	addDialogComponent(diac_nodeType);
    	
    	createNewGroup("Search for Specific Attribute");
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableAttribute, "Enable Attribute Search"));
    	addDialogComponent(new DialogComponentStringSelection(
    			m_AttributeSearch, "Select available Attribute", attributes));
    	
    	
    	createNewGroup("Search with structure");
    	setHorizontalPlacement(false);
    	addDialogComponent(new DialogComponentBoolean(m_enableStructure, "Enable Structure Search"));
    	addDialogComponent(new DialogComponentNumber(m_nodeId, "NodeID", 1));
    	m_enableStructure.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_nodeId.setEnabled(m_enableStructure.getBooleanValue());
				m_parents.setEnabled(m_enableStructure.getBooleanValue());
				m_children.setEnabled(m_enableStructure.getBooleanValue());
				m_siblings.setEnabled(m_enableStructure.getBooleanValue());
				m_allChildren.setEnabled(m_enableStructure.getBooleanValue());
			}
		});
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_parents, "Parents"));
    	addDialogComponent(new DialogComponentBoolean(m_children, "Children"));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_siblings, "Siblings"));
    	addDialogComponent(new DialogComponentBoolean(m_allChildren, "AllChildren"));
    	closeCurrentGroup();
    	
    	addDialogComponent(new DialogComponentLabel("Search! "));
    	
    	
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
    
    public void getAttributes(JEVisDataSourceSQL jevis, ArrayList<String> attributes){
    	try{
    		if(jevis.isConnectionAlive()){
    			List<JEVisClass> jClasses = jevis.getJEVisClasses();
    			for(JEVisClass jclass: jClasses){
    				List<JEVisObject> jObjects = jevis.getObjects(jclass, true);
    				for(JEVisObject jObject : jObjects){
    					List<JEVisAttribute> jAttributes = jObject.getAttributes();
    					for(JEVisAttribute jattribute :jAttributes){
    						if(!attributes.contains(jattribute.getName())){
    							attributes.add(jattribute.getName());
    						}
    					}
    				}
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    }
    
}

