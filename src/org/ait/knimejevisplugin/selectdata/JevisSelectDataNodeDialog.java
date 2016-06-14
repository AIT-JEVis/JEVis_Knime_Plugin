package org.ait.knimejevisplugin.selectdata;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jevis.api.JEVisException;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
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
	
	private final Logger logger = LogManager.getLogger("SelectNdoeDialog");
	
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
   	
    /**
     * New pane for configuring the JevisSelectData node.
     */
    protected JevisSelectDataNodeDialog() {
    	
    	createNewGroup("Search through attributes");
    	addDialogComponent(new DialogComponentBoolean(enableAttribute, "Enable Jevis"));
    	
    	
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
    	enableAttribute.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
		    	jhost.setEnabled(enableAttribute.getBooleanValue());
			}
		});
    	
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
    
}

