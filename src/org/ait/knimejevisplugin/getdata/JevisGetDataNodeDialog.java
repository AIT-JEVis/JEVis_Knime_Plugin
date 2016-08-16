package org.ait.knimejevisplugin.getdata;

import java.util.ArrayList;

import org.jevis.api.JEVisException;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "JevisGetData" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Monschiebl
 */
public class JevisGetDataNodeDialog extends DefaultNodeSettingsPane {

    protected static ArrayList<String> months = new ArrayList<String>(); 
	protected static ArrayList<String> days = new ArrayList<String>();
	
	private JEVisDataSourceSQL jevis;
    /**
     * New pane for configuring the JevisGetData node.
     *  
     */
    protected JevisGetDataNodeDialog() throws JEVisException {
    	super();
    	
    	connectingtojevis();
    	addDialogComponent(new DialogComponentNumber(
    			new SettingsModelInteger(
    					JevisGetDataNodeModel.CFGKEY_nodeID, JevisGetDataNodeModel.nodeID),
    			"NodeID",
    			0));
    	createNewGroup("Start Date:");
    	setHorizontalPlacement(true);
	    addDialogComponent(new DialogComponentString(
	    	new SettingsModelString(JevisGetDataNodeModel.startDateyear, "2000"), "year"));
	    //Selection for month
	    for(int i = 1; i<13; i++)
	    	months.add(new String(""+i));
	    
	    addDialogComponent(new DialogComponentStringSelection(
	    	new SettingsModelString(JevisGetDataNodeModel.startDateMonth,"1"),
	    		 "month", months));
	    //Selection for date
	    for(int i = 1; i<32; i++)
	   		days.add(new String(""+i));    		
	   	
	   	addDialogComponent(new DialogComponentStringSelection(
   			new SettingsModelString(JevisGetDataNodeModel.startDateDay,"1"),
   				"day", days));
    	
    	createNewGroup("Start Time Selection");
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisGetDataNodeModel.startHour, "00"), "hour"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisGetDataNodeModel.startMinute, "00"), "minute"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisGetDataNodeModel.startSecond, "00"), "seconds"));
    	
    	createNewGroup("End Date:");
    	setHorizontalPlacement(true);
	    addDialogComponent(new DialogComponentString(
	    	new SettingsModelString(JevisGetDataNodeModel.endDateyear, "2017"), "year"));
	    //Selection for month
	    for(int i = 1; i<13; i++)
	    	months.add(new String(""+i));
	    
	    addDialogComponent(new DialogComponentStringSelection(
	    	new SettingsModelString(JevisGetDataNodeModel.endDateMonth,"12"),
	    		 "month", months));
	    //Selection for date
	    for(int i = 1; i<32; i++)
	   		days.add(new String(""+i));    		
	   	
	   	addDialogComponent(new DialogComponentStringSelection(
   			new SettingsModelString(JevisGetDataNodeModel.endDateDay,"31"),
   				"day", days));
    	
    	createNewGroup("End Time Selection");
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisGetDataNodeModel.endHour, "00"), "hour"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisGetDataNodeModel.endMinute, "00"), "minute"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisGetDataNodeModel.endSecond, "00"), "seconds"));
    }
    
    private void connectingtojevis(){
    	    	
    	try{

    	//Connecting to Jevis with connection information
    	jevis = new JEVisDataSourceSQL(JevisGetDataNodeModel.host, JevisGetDataNodeModel.port,
    			JevisGetDataNodeModel.sqlSchema, JevisGetDataNodeModel.sqlUser, 
    			JevisGetDataNodeModel.sqlPW);
    	jevis.connect(JevisGetDataNodeModel.jevisUser, JevisGetDataNodeModel.jevisPW);
    	}catch(JEVisException e){
    		e.printStackTrace();
    		JevisGetDataNodeModel.logger.error("Connection error! Check Jevis settings and try again!");
    	}
    	
    }

}

