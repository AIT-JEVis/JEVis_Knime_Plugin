package org.ait.knimejevisplugin.selectdata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ait.knimejevisplugin.DataBaseConfiguration;
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
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;

import com.sun.media.sound.ModelChannelMixer;

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
	ArrayList<String> projects = new ArrayList<String>();
	ArrayList<String> locations = new ArrayList<String>();

	private static final Logger logger = LogManager.getLogger("SelectNdoeDialog");
	
	private JEVisDataSourceSQL jevis;
	
    private final SettingsModelString jhost = new SettingsModelString(
    		DataBaseConfiguration.hostModelName,
    		DataBaseConfiguration.DEFAULT_Host);
    private final SettingsModelString jport = new SettingsModelString(
    		DataBaseConfiguration.portModelName,
    		DataBaseConfiguration.DEFAULT_port);
   	private final SettingsModelString jSchema = new SettingsModelString(
   			DataBaseConfiguration.sqlSchemaModelName,
   			DataBaseConfiguration.DEFAULT_sqlShema);
   	private final SettingsModelString jUser = new SettingsModelString(
   			DataBaseConfiguration.sqlUserModelName,
   			DataBaseConfiguration.DEFAULT_sqlUserName);
   	private final SettingsModelString jPW = new SettingsModelString(
   			DataBaseConfiguration.sqlPWModelName, 
   			DataBaseConfiguration.DEFAULT_sqlPW);
   	
   	private final SettingsModelString jevUser = new SettingsModelString(
   			DataBaseConfiguration.jevisUserModelName, 
   			DataBaseConfiguration.DEFAULT_jevisUserName);
   	private final SettingsModelString jevPW = new SettingsModelString(
   			DataBaseConfiguration.jevisPWModelName,
   			DataBaseConfiguration.DEFAULT_jevisPW);
	
   	private final SettingsModelBoolean m_enableNodeSearch = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableNodeSearch, true);
   	
   	private final SettingsModelString m_project = new SettingsModelString(
   			DataBaseConfiguration.projectModelName,"");
   	private final SettingsModelString m_location = new SettingsModelString(
   			DataBaseConfiguration.locationModelName," ");
   	private final SettingsModelString m_nodeType = new SettingsModelString(
   			DataBaseConfiguration.nodeType," ");
   	private final SettingsModelString m_devicetype = new SettingsModelString(
   			DataBaseConfiguration.deviceModelName," ");
   	private final SettingsModelString m_component = new SettingsModelString(
   			DataBaseConfiguration.componentModelName," ");
   	
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
     * @throws JEVisException 
     */
    protected JevisSelectDataNodeDialog() throws JEVisException {
    	JevisSelectDataNodeModel.logger.setLevel(NodeLogger.LEVEL.INFO);
    	JevisSelectDataNodeModel.logger.warn("Opening Configuration Window. "
    			+ "Please be patient it may take a moment.");
    	projects.add(" ");
		nodefilter.add(" ");
		devicetypes.add(" ");
		components.add(" ");
		attributes.add(" ");
		locations.add(" ");
        	
    	DialogComponentStringSelection diac_nodeType = new DialogComponentStringSelection(
    			m_nodeType, "NodeType", nodefilter);
    	
    	DialogComponentStringSelection diac_projects = 
    			new DialogComponentStringSelection(m_project, 
    			"Project", projects);
    	
    	DialogComponentStringSelection diac_location = new DialogComponentStringSelection(
    					m_location, "Location", locations);
    	
    	DialogComponentStringSelection diac_deviceType = new DialogComponentStringSelection(
    			m_devicetype, "Device", devicetypes);
    	
    	DialogComponentStringSelection diac_component = new DialogComponentStringSelection(
    			m_component, "Component", components);
    	
    	
    	DialogComponentStringSelection diac_attribute = new DialogComponentStringSelection(
    			m_AttributeSearch, "Select available Attribute", attributes);
    	
    	DialogComponentLabel diac_con =new DialogComponentLabel("Disconnected!");
    	
    	DialogComponentButton connectBtn= new DialogComponentButton("Connect to Jevis");
    	connectBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				diac_con.setText("Trying to connect!");
		    	connectingtojevis();
		    	try {
					if(jevis.isConnectionAlive()){
						
						projects.clear();
						getProjects(jevis, projects);
			        	diac_projects.replaceListItems(projects, null);
						JevisSelectDataNodeModel.logger.warn("Connecting to Jevis. "
								+ "May Take a while!");
				
			        	getnodetypes(jevis, nodefilter);
			        	diac_nodeType.replaceListItems(nodefilter, null);
			        	getAttributes(jevis, attributes);
			        	diac_attribute.replaceListItems(attributes, null);
			        	diac_con.setText("Connected!");

					}
				} catch (JEVisException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
    	

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
    	setHorizontalPlacement(true);
    	addDialogComponent(connectBtn);
    	addDialogComponent(diac_con);
    	setHorizontalPlacement(false);
    	closeCurrentGroup();
    	setDefaultTabTitle("Configure Connection");
    	
    	
    	createNewTabAt("Filter Output",1);
    	createNewGroup("Search through Nodes");
    	addDialogComponent(new DialogComponentBoolean(m_enableNodeSearch, 
    			"enable Specific Node Search"));
    	m_enableNodeSearch.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				
		    	//m_nodeType.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_devicetype.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_component.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_location.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_project.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_enableComponent.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_enableProject.setEnabled(m_enableNodeSearch.getBooleanValue());
		    	m_enableDevice.setEnabled(m_enableNodeSearch.getBooleanValue());
			}
		});

    	
    	//Searching for Attributes like project, location, nodeType, device and component
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableProject, 
    			"enable Project search:"));
    	m_enableProject.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_project.setEnabled(m_enableProject.getBooleanValue());
			}
		});
    	addDialogComponent(diac_projects);
    	m_project.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
	        	getdevicetypes(jevis, devicetypes);
	        	diac_deviceType.replaceListItems(devicetypes, null);
	        	getcomponents(jevis, components);
	        	diac_component.replaceListItems(components, null);
			}
		});
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableLocation, 
    			"enable location search:"));
    	m_enableLocation.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_location.setEnabled(m_enableLocation.getBooleanValue());
			}
		});
    	addDialogComponent(diac_location);

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
    	addDialogComponent(diac_deviceType);
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
    	addDialogComponent(diac_component);
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
    	addDialogComponent(diac_attribute);
    	
    	createNewTab("Structure Search");
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
    	
    	addDialogComponent(new DialogComponentLabel("Search!"));
    	    	
    }
    
    
    private void connectingtojevis(){
    	
	    try{
		//Connecting to Jevis with connection information
		jevis = new JEVisDataSourceSQL(jhost.getStringValue(), jport.getStringValue(), jSchema.getStringValue(),
				jUser.getStringValue(), jPW.getStringValue());
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
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass(
    					"Data"), true).size();i++){
    				if(!devicetypes.contains(jevis.getObjects(
    						jevis.getJEVisClass("Data"), true).get(i).getName())){
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
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass(
    					"Device"), true).size();i++){
    				if(!components.contains(jevis.getObjects(
    						jevis.getJEVisClass("Device"), true).get(i).getName())){
    					components.add(jevis.getObjects(jevis.getJEVisClass("Device"), true).
    							get(i).getName());
    
    				}
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    	
    }
    // TODO: Needing to improve on Perfomance
    public void getAttributes(JEVisDataSourceSQL jevis, ArrayList<String> attributes){
    	try{
    		if(jevis.isConnectionAlive()){
    			List<JEVisClass> jClasses = jevis.getJEVisClasses();
    			for(JEVisClass jclass: jClasses){
    				if(!jevis.getObjects(jclass,true).isEmpty()){
    					JEVisObject jObject = jevis.getObjects(jclass, true).get(0);
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
    public void getProjects(JEVisDataSourceSQL jevis, ArrayList<String> projects){
    	try{
    		if(jevis.isConnectionAlive()){
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass(
    					DataBaseConfiguration.projectLevelName), true).size();i++){
    				if(!projects.contains(jevis.getObjects(jevis.getJEVisClass(
    						DataBaseConfiguration.projectLevelName), true).
    						get(i).getName())){
    					projects.add(jevis.getObjects(jevis.getJEVisClass(
    							DataBaseConfiguration.projectLevelName), true).
    							get(i).getName());
    
    				}
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    	
    }
}

