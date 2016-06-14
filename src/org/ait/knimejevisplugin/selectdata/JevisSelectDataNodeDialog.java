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
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;

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
    /**
     * New pane for configuring the JevisSelectData node.
     */
    protected JevisSelectDataNodeDialog() {
    	
    	connectingtojevis();
    	getnodetypes(jevis, nodefilter);
    	createNewGroup("Search through attributes");
    	addDialogComponent(new DialogComponentBoolean(enableAttribute, "disable Attribute Search"));
    	enableAttribute.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
		    	jhost.setEnabled(enableAttribute.getBooleanValue());
			}
		});
    	addDialogComponent(new DialogComponentStringSelection(m_nodeType, "NodeType", nodefilter));
    	
    	
    	createNewTabAt("Configure Connection",0);
    	createNewGroup("Database Connection Settings");
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

    	connectingtojevis();
    	getsearchinformation(jevis);
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
    			for(int i=0; i<jevis.getObjects(jevis.getJEVisClass("Data"), true).size();i++){
    				if(!devicetypes.contains(jevis.getObjects(jevis.getJEVisClass("Data"), true).
    						get(i).getName())){
    					devicetypes.add(jevis.getObjects(jevis.getJEVisClass("Data"), true).
    							get(i).getName());
    
    				}
    				
    				Map<String, List<JEVisObject>> jevisObjects = new HashMap<String, List<JEVisObject>>();
					List<JEVisObject> list_devices = jevis.getObjects(
							jevis.getJEVisClass("Data"), true);
					list_devices.get(i).getID();
					jevisObjects.put(jevis.getJEVisClasses().get(i).getName(),
							jevis.getObjects(jevis.getJEVisClasses().get(i),true));
					
    			}
    		}
    	}catch(JEVisException e){
    		e.printStackTrace();
    	}
    }
    
    public void getsearchinformation(JEVisDataSourceSQL jevis){
    	
    	try {
			if(jevis.isConnectionAlive()){
				for(int i= 0 ; i<jevis.getObjects(jevis.getJEVisClass("Data"), true).size(); i++) {
						logger.error(jevis.getObjects(jevis.getJEVisClass("Data"), true).
								get(i).getName());
						
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
					logger.error(jevis.getJEVisClasses().get(i).getName());
				}
				for(int i= 0 ; i<jevis.getObjects(jevis.getJEVisClass(jevis.getJEVisClasses().get(i).getName()), true).size(); i++) {
					logger.error(jevis.getObjects(jevis.getJEVisClass(jevis.getJEVisClasses().get(i).getName()), true).
							get(i).getName());
				}
			}
			
    	}catch (JEVisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

