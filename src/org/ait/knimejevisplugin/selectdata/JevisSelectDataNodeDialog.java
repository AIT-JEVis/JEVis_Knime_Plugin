package org.ait.knimejevisplugin.selectdata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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
import org.knime.core.node.NodeLogger;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentPasswordField;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

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
	
	final ArrayList<String> nodefilter = new ArrayList<String>();
	final ArrayList<String> devicetypes = new ArrayList<String>();
	final ArrayList<String> components = new ArrayList<String>();
	ArrayList<String> nodetypes = new ArrayList<String>();
	ArrayList<String> attributes = new ArrayList<String>();
	final ArrayList<String> projects = new ArrayList<String>();
	final ArrayList<String> locations = new ArrayList<String>();

	ArrayList<String> operators = new ArrayList<String>();
	
	ArrayList<String> attributesfiltered = new ArrayList<String>();

	Thread t = new Thread();
	
	private static final Logger logger = LogManager.getLogger("SelectNdoeDialog");
	
	static JEVisDataSourceSQL jevis; 
	
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
   			JevisSelectDataNodeModel.enableNodeSearch, false);
   	
   	private final SettingsModelString m_project = new SettingsModelString(
   			DataBaseConfiguration.projectModelName,"");
   	private final SettingsModelString m_location = new SettingsModelString(
   			DataBaseConfiguration.locationModelName," ");
   	private final SettingsModelString m_devicetype = new SettingsModelString(
   			DataBaseConfiguration.deviceModelName," ");
   	private final SettingsModelString m_component = new SettingsModelString(
   			DataBaseConfiguration.componentModelName," ");
/*   	
   	private final SettingsModelBoolean m_enableProject = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableProject, false);
   	private final SettingsModelBoolean m_enableLocation = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableLocation, false);
   	private final SettingsModelBoolean m_enableComponent = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableComponent, false);
   	private final SettingsModelBoolean m_enableDevice = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableDevice, false);
*/  	
   	
   	private final SettingsModelBoolean m_enableNodeType = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableNodeType, false);
   	private final SettingsModelString m_nodeType = new SettingsModelString(
   			DataBaseConfiguration.nodeType," ");
   	   	
   	private final SettingsModelBoolean m_enableAttribute = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableAttributeSearch, false); 	
/* 	private final SettingsModelString m_AttributeSearch = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelName, " ");
*/
   	private final SettingsModelString m_attributeModelList1 = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelList1, " ");
   	private final SettingsModelString m_attributeModelList2 = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelList2, " ");
   	private final SettingsModelString m_attributeModelList4 = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelList4, " ");
   	private final SettingsModelString m_attributeModelList3 = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelList3, " ");
   	
   	private final SettingsModelString m_operator1 = new SettingsModelString(
   			JevisSelectDataNodeModel.operator1," ");
   	private final SettingsModelString m_operator2 = new SettingsModelString(
   			JevisSelectDataNodeModel.operator2," ");
   	private final SettingsModelString m_operator3 = new SettingsModelString(
   			JevisSelectDataNodeModel.operator3," ");
   	private final SettingsModelString m_operator4 = new SettingsModelString(
   			JevisSelectDataNodeModel.operator4," ");
   	
   	private final SettingsModelString m_attributeModelValue1 = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelValue1, " ");
   	private final SettingsModelString m_attributeModelValue2 = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelValue2, " ");
   	private final SettingsModelString m_attributeModelValue3 = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelValue3, " ");
   	private final SettingsModelString m_attributeModelValue4 = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelValue4, " ");
   	   	
   	private final SettingsModelBoolean m_enableStructure = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableStructure, false);
   	
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
   	/*
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
	
   	
   	
   	
   	
   	
    /**
     * New pane for configuring the JevisSelectData node.
     * @throws JEVisException 
     */
    @SuppressWarnings({ "static-access", "deprecation" })
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
        attributesfiltered.add(" ");
       
        /*diac_nodeType.replaceListItems(nodefilter, null);
        diac_projects.replaceListItems(projects, null);
        diac_location.replaceListItems(locations, null);
        diac_component.replaceListItems(components, null);
        diac_deviceType.replaceListItems(devicetypes, null);
        */
        
        operators.add("contains");
        operators.add("equals");
        
	
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
    	
    	
    /*	DialogComponentStringSelection diac_attribute = new DialogComponentStringSelection(
    			m_AttributeSearch, "Select available Attribute", attributes);
    */	
    	DialogComponentStringSelection diac_attribute1 = new DialogComponentStringSelection(
    			m_attributeModelList1, " ", attributesfiltered);
    	DialogComponentStringSelection diac_attribute2 = new DialogComponentStringSelection(
    			m_attributeModelList2, " ", attributesfiltered);
    	DialogComponentStringSelection diac_attribute3 = new DialogComponentStringSelection(
    			m_attributeModelList3, " ", attributesfiltered);
    	DialogComponentStringSelection diac_attribute4 = new DialogComponentStringSelection(
    			m_attributeModelList4, " ", attributesfiltered);
    	
    	DialogComponentStringSelection diac_op1 = new DialogComponentStringSelection(
    			m_operator1, " " , operators);
    	DialogComponentStringSelection diac_op2 = new DialogComponentStringSelection(
    			m_operator2, " " , operators);    			
    	DialogComponentStringSelection diac_op3 = new DialogComponentStringSelection(
    			m_operator3, " " , operators);
    	DialogComponentStringSelection diac_op4 = new DialogComponentStringSelection(
    			m_operator4, " " , operators);
    	
    	DialogComponentString diac_attributevalue1 = new DialogComponentString(
    			m_attributeModelValue1, "Value: ");
    	DialogComponentString diac_attributevalue2 = new DialogComponentString(
    			m_attributeModelValue2, "Value: ");
    	DialogComponentString diac_attributevalue3 = new DialogComponentString(
    			m_attributeModelValue3, "Value: ");
    	DialogComponentString diac_attributevalue4 = new DialogComponentString(
    			m_attributeModelValue4, "Value: ");
    	
    	DialogComponentLabel diac_con = new DialogComponentLabel("Disconnected!");
    	
    	DialogComponentButton connectBtn= new DialogComponentButton("Connect to Jevis");
    	connectBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				diac_con.setText("Trying to connect!");
		    	connectingtojevis();
		    	try {
		    		Thread t1 = new Thread(new Runnable(){

						@Override
						public void run() {
						
							
							getProjects(jevis, projects);
				        	diac_projects.replaceListItems(projects, null);

							getAttributes(jevis, attributesfiltered);
							diac_attribute1.replaceListItems(attributesfiltered, null);
							diac_attribute2.replaceListItems(attributesfiltered, null);
							diac_attribute3.replaceListItems(attributesfiltered, null);
							diac_attribute4.replaceListItems(attributesfiltered, null);
							
				        	getnodetypes(jevis, nodefilter);
				        	diac_nodeType.replaceListItems(nodefilter, null);
				        	getAttributes(jevis, attributes);
				        	diac_con.setText("Connected!");
						}
		    			
		    		});
					if(jevis.isConnectionAlive()){
						
						t1.start();
//			        	diac_attribute.replaceListItems(attributes, null);
						JevisSelectDataNodeModel.logger.warn("Connecting to Jevis. "
								+ "May Take a while!");

					}
				} catch (JEVisException e1) {
	
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
    	addDialogComponent(new DialogComponentPasswordField(jPW, "SqlPassword"));
    	createNewGroup("Jevis User Information");
    	addDialogComponent(new DialogComponentString(jevUser, "JevisUser Name"));
    	addDialogComponent(new DialogComponentPasswordField(jevPW, "Jevis Password"));
    	setHorizontalPlacement(true);
    	addDialogComponent(connectBtn);
    	addDialogComponent(diac_con);
    	setHorizontalPlacement(false);
    	closeCurrentGroup();
    	setDefaultTabTitle("Configure Connection");
    	
    	createNewTabAt("Attribute and DataPoint Search",1);
    	createNewGroup("Select Search:");
    	setHorizontalPlacement(true);
    	addDialogComponent(new DialogComponentBoolean(m_enableNodeSearch, 
    			"Search for Datapoints"));
    	addDialogComponent(new DialogComponentBoolean(m_enableNodeType, "Search for JEVisClass objects"));
    	addDialogComponent(new DialogComponentBoolean(m_enableAttribute, "Override Attribute Filter."));
    	//Searching for Attributes like project, location, nodeType, device and component
    	setHorizontalPlacement(true);
/*    	addDialogComponent(new DialogComponentBoolean(m_enableProject, 
    			"enable Project search:"));
    	m_enableProject.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_project.setEnabled(m_enableProject.getBooleanValue());
			}
		});
*/    	
    	createNewGroup("Filter for Levels in Database. ");
    	addDialogComponent(diac_projects);
    	m_project.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				//putting them into Button will be a faster solution but also feasable?
	        	
				try {
					if(jevis.isConnectionAlive()){
						if(!t.isAlive()){
							t = new Thread(new Runnable(){
								@Override
								public void run() {
									
									getLocation(JevisSelectDataNodeDialog.jevis, locations);
									diac_location.replaceListItems(locations, null);
								//	getcomponents(JevisSelectDataNodeDialog.jevis, components);
								//	diac_component.replaceListItems(components, null);
								//	getdevicetypes(JevisSelectDataNodeDialog.jevis, devicetypes);
								//	diac_deviceType.replaceListItems(devicetypes, null);
	
								}						 
							});
							t.start();
						}
						//wait();
					}
				} catch (JEVisException e1) {
					
					e1.printStackTrace();
				}
			}
		});
    	m_location.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				try {
					if(jevis.isConnectionAlive()){
						if(!t.isAlive()){
							
							t = new Thread(new Runnable(){
								@Override
								public void run() {
								
									getcomponents(JevisSelectDataNodeDialog.jevis, components);
									diac_component.replaceListItems(components, null);
									//getdevicetypes(JevisSelectDataNodeDialog.jevis, devicetypes);
									//diac_deviceType.replaceListItems(devicetypes, null);

								}						 
							});
							t.start();
						}
						else{
							System.out.println("Blocked");
						}
						//wait();
					}
				} catch (JEVisException e1) {
					
					e1.printStackTrace();
				}
			}
		});
    	m_component.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
	
				try {
					if(jevis.isConnectionAlive()){
						if(!t.isAlive()){
							
							t = new Thread(new Runnable(){
								@Override
								public void run() {
								
									getdevicetypes(JevisSelectDataNodeDialog.jevis, devicetypes);
									diac_deviceType.replaceListItems(devicetypes, null);

								}						 
							});
							t.start();
						}
						else{
							System.out.println("Blocked");
						}
						//wait();
					}
				} catch (JEVisException e1) {
					
					e1.printStackTrace();
				}
			}
		});
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
/*    	addDialogComponent(new DialogComponentBoolean(m_enableLocation, 
    			"enable location search:"));
    	m_enableLocation.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_location.setEnabled(m_enableLocation.getBooleanValue());
			}
		});
*/    	
    	addDialogComponent(diac_location);

    	//addDialogComponent(new DialogComponentString(m_searchNodeType," "));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
/*		addDialogComponent(new DialogComponentBoolean(
    			m_enableDevice, "Enable Device Search"));
    	m_enableDevice.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_devicetype.setEnabled(m_enableDevice.getBooleanValue());
			}
		});
*/
    	addDialogComponent(diac_component);
    	//addDialogComponent(new DialogComponentString(m_searchDeviceType, " "));
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
/*    	addDialogComponent(new DialogComponentBoolean(m_enableComponent, "Enable Component Search"));
    	m_enableComponent.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				m_component.setEnabled(m_enableComponent.getBooleanValue());
			}
		});
*/   	
    	
    	addDialogComponent(diac_deviceType);
    	createNewGroup("Search for Attibutes:");
    	setHorizontalPlacement(true);
    	    	
//    	addDialogComponent(diac_attribute);
    	
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(diac_attribute1);
    	addDialogComponent(diac_op1);
    	addDialogComponent(diac_attributevalue1);
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(diac_attribute2);
    	addDialogComponent(diac_op2);
    	addDialogComponent(diac_attributevalue2);
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(diac_attribute3);
    	addDialogComponent(diac_op3);
    	addDialogComponent(diac_attributevalue3);
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	addDialogComponent(diac_attribute4);
    	addDialogComponent(diac_op4);
    	addDialogComponent(diac_attributevalue4);
    	
    	createNewGroup("Search for specific Nodetype");
    	setHorizontalPlacement(false);
    	setHorizontalPlacement(true);
    	
    	m_enableNodeType.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				m_nodeType.setEnabled(m_enableNodeType.getBooleanValue());
				
			}
		});
    	addDialogComponent(diac_nodeType);
    	
    	//createNewTab("Attribute Search");
	
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
    
    public void connectingtojevis(){
    	
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
    	devicetypes.clear();
    	devicetypes.add(" ");
    	try{
    		if(jevis.isConnectionAlive()){
    			//filling device type string selection list:
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass(
    					DataBaseConfiguration.deviceLevelName), true).size();i++){
    				if(!devicetypes.contains(jevis.getObjects(
    						jevis.getJEVisClass(DataBaseConfiguration.deviceLevelName), true).get(i).getName())){
    					if(getParent(jevis.getObjects(jevis.getJEVisClass(
								DataBaseConfiguration.deviceLevelName), true).
						get(i)).getName().equals(m_component.getStringValue())){
        					devicetypes.add(jevis.getObjects(jevis.getJEVisClass(DataBaseConfiguration.deviceLevelName), true).
        							get(i).getName());
    					}
    					else if(m_component.getStringValue().equals(" ") || m_component.getStringValue().isEmpty()){
          					devicetypes.add(jevis.getObjects(jevis.getJEVisClass(DataBaseConfiguration.deviceLevelName), true).
        							get(i).getName());
    					}
    				}
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    }
    
    public void getcomponents(JEVisDataSourceSQL jevis, ArrayList<String> components){
    	components.clear();
    	components.add(" ");
    	try{
    		if(jevis.isConnectionAlive()){
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass(
    					DataBaseConfiguration.componentLevelName), true).size();i++){
    				if(!components.contains(jevis.getObjects(
    						jevis.getJEVisClass(DataBaseConfiguration.componentLevelName), true).get(i).getName())){
       					if(getParent(jevis.getObjects(jevis.getJEVisClass(
								DataBaseConfiguration.componentLevelName), true).
						get(i)).getName().equals(m_location.getStringValue())){
        					components.add(jevis.getObjects(jevis.getJEVisClass(DataBaseConfiguration.componentLevelName), true).
        							get(i).getName());
       					}
       					else if(m_location.getStringValue().equals(" ") || m_location.getStringValue().isEmpty()){
        					components.add(jevis.getObjects(jevis.getJEVisClass(DataBaseConfiguration.componentLevelName), true).
        							get(i).getName());
       					}
    				}
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    }
    
    public void getLocation(JEVisDataSourceSQL jevis, ArrayList<String> locations){
    	locations.clear();
    	locations.add(" ");
    	try{
    		if(jevis.isConnectionAlive()){
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass(
    					DataBaseConfiguration.locationLevelName), true).size();i++){
    				if(!locations.contains(jevis.getObjects(
    						jevis.getJEVisClass(
    								DataBaseConfiguration.locationLevelName), true)
    						.get(i).getName()))
    						/*&& jevis.getObjects(jevis.getJEVisClass(
    								DataBaseConfiguration.locationLevelName),true)
    						.get(i).getParents().get(0).getName()
    						.equals(m_project.getStringValue()))*/{
    					
    					if(getParent(jevis.getObjects(jevis.getJEVisClass(
    									DataBaseConfiguration.locationLevelName), true).
    							get(i)).getName().equals(m_project.getStringValue())){
    						locations.add(jevis.getObjects(jevis.getJEVisClass(
									DataBaseConfiguration.locationLevelName), true).
							get(i).getName());
    					}
    					
    
    				}
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    }
    
    private JEVisObject getParent(JEVisObject inobj) throws JEVisException{
    	JEVisObject object= null;
    	for(JEVisObject obj: inobj.getParents()){
    		if(obj.getJEVisClass().getName().equals(DataBaseConfiguration.locationLevelName)){
    			
    			return obj;
    		}
    		else if(obj.getJEVisClass().getName().equals(DataBaseConfiguration.componentLevelName)){
    			return obj;
    		}
    		else if(obj.getJEVisClass().getName().equals(DataBaseConfiguration.projectLevelName)){
    			return obj;
    		}
    		else{
    			object = getParent(obj);
    		}
    	}
    	
    	return object;
    }

    public void getAttributes(JEVisDataSourceSQL jevis, ArrayList<String> attributes){
    	try{
    		if(jevis.isConnectionAlive()){
    			List<JEVisClass> jClasses = jevis.getJEVisClasses();
    			for(JEVisClass jclass: jClasses){
    				if(!jevis.getObjects(jclass,true).isEmpty()){
    					JEVisObject jObject = jevis.getObjects(jclass, true).get(0);
    					List<JEVisAttribute> jAttributes = jObject.getAttributes();
    					for(JEVisAttribute jattribute : jAttributes){
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

