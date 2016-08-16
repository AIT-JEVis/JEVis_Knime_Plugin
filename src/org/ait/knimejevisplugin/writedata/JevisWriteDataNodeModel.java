package org.ait.knimejevisplugin.writedata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.ait.knimejevisplugin.DataBaseConfiguration;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.joda.time.DateTime;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.date.DateAndTimeValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of JevisWriteData.
 * 
 *
 * @author Monschiebl
 */
public class JevisWriteDataNodeModel extends NodeModel {
    
	JEVisDataSourceSQL jevis;
	
	final static int IN_PORT = 0;
	
	static String objID = "ObjectID";
	static String updateDataPoint = "UpdateDataPoint";
	static String newDataPoint = "NewDataPoint";
	static String objectName = "ObjectName";
	static String newDataPointClass = "newDataPointClass";
		
	List<SettingsModel> settingsModels = new ArrayList<SettingsModel>();
	static String deleteDataPoint = "Delete DataPoint";

	//Jevis Connection Information
	public static String host = DataBaseConfiguration.DEFAULT_Host;
	public static String port = DataBaseConfiguration.DEFAULT_port;
	public static String sqlSchema = DataBaseConfiguration.DEFAULT_sqlShema;
	public static String sqlUser = DataBaseConfiguration.DEFAULT_sqlUserName;
	public static String sqlPW =  DataBaseConfiguration.DEFAULT_sqlPW;
	 	
	public static String jevisUser = DataBaseConfiguration.DEFAULT_jevisUserName;
	public static String jevisPW = DataBaseConfiguration.DEFAULT_jevisPW; 
	  
	private final SettingsModelLong m_objID = 
			new SettingsModelLong(objID, 0);	
	private final SettingsModelBoolean m_update = 
			new SettingsModelBoolean(updateDataPoint, false);
	
	private final SettingsModelBoolean m_newDataPoint = 
			new SettingsModelBoolean(newDataPoint, false);
	private final SettingsModelString m_objectName =
			new SettingsModelString(objectName, "unnamed_Data");
	private final SettingsModelString m_newDataPointClass = 
			new SettingsModelString(newDataPointClass, "Data");
	
	private final SettingsModelBoolean m_deleteDataPoint =
			new SettingsModelBoolean(deleteDataPoint, false);

	
    // the logger instance
    static final NodeLogger logger = NodeLogger
            .getLogger(JevisWriteDataNodeModel.class);
        
    /**
     * Constructor for the node model.
     */
    @SuppressWarnings({"static-access", "deprecation" })
	protected JevisWriteDataNodeModel() {
       
        super(1, 0);
        
        settingsModels.add(m_update);
        settingsModels.add(m_objID);
        settingsModels.add(m_newDataPoint);
        settingsModels.add(m_deleteDataPoint);
        settingsModels.add(m_objectName);
        settingsModels.add(m_newDataPointClass);
        
       logger.setLevel(NodeLogger.LEVEL.ALL);
    }

    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	connectingtojevis();
    	try{
    		if(jevis.isConnectionAlive()){
    			BufferedDataTable table = inData[IN_PORT];
            	JEVisWriter writer = new JEVisWriter(jevis);
            	try{
            	if(m_update.getBooleanValue()){
                	JEVisObject obj = jevis.getObject(m_objID.getLongValue());
            		fetchingInformationFromInPut(table, writer, obj);
            		fetchingMetaData(obj);
            		obj.commit();
            		if(obj.getAttribute("Value").hasSample()){
            			logger.info("Samples updated");
            		}
            	}
            	if(m_newDataPoint.getBooleanValue()){
            		
                	long objID = writer.createNewDatapointUnderParent(
                			m_objID.getLongValue(), m_objectName.getStringValue(), 
                			m_newDataPointClass.getStringValue());
                	JEVisObject obj = jevis.getObject(objID);
                	fetchingInformationFromInPut(table, writer, obj);
                	fetchingMetaData(obj);
                	obj.commit();
            		if(obj.getAttribute("Value").hasSample()){
            			logger.info("New Object build!");
            		}
            	}
            	if(m_deleteDataPoint.getBooleanValue()){
            		writer.clearDataPointData(m_objID.getLongValue());
            	}
        	
	        	}catch(Exception e){
	        		e.printStackTrace();
	        		logger.error("Error while trying Operation");       		
	        	}
    		}
    	}catch(JEVisException je){
    		je.printStackTrace();
    		logger.error("Connection to JEVis Lost");
    	}
    	
        return new BufferedDataTable[]{};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    
    	//no op
    	
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
    	
    	if(inSpecs.length > 0 ){

    		 DataColumnSpec c0 = inSpecs[0].getColumnSpec(0);
    		 DataColumnSpec c1 = inSpecs[0].getColumnSpec(1);
    		 
    		 if(!c0.getType().isCompatible(DateAndTimeValue.class)){
     	 		throw new InvalidSettingsException(
        				"Invalid column type at first column"); 
    		 }
    		 if(!c1.getType().isCompatible(DoubleValue.class)){
     	 		throw new InvalidSettingsException(
        				"Invalid column type at second column"); 
    		 }
    	}
    	
        return new DataTableSpec[0];
    }

  public void connectingtojevis() throws ClassNotFoundException{
    	
    	//getting Connection information from selection node if existing
    	if(getAvailableFlowVariables().containsKey("host")
    			&& getAvailableFlowVariables().containsKey("port")
    			&& getAvailableFlowVariables().containsKey("sqlSchema")
    			&& getAvailableFlowVariables().containsKey("sqlUser")
    			&& getAvailableFlowVariables().containsKey("sqlPW")
    			&& getAvailableFlowVariables().containsKey("JEVisUser")
    			&& getAvailableFlowVariables().containsKey("JEVisPW")){
	    	
	    	host = peekFlowVariableString("host");
	    	port = peekFlowVariableString("port");
	    	sqlSchema = peekFlowVariableString("sqlSchema");
	    	sqlUser = peekFlowVariableString("sqlUser");
	    	sqlPW = peekFlowVariableString("sqlPW");
	    	
	    	jevisUser = peekFlowVariableString("JEVisUser");
	    	jevisPW = peekFlowVariableString("JEVisPW");
    	}
    	
    	try{
    	//Connecting to Jevis with connection information
    	Class.forName("com.mysql.jdbc.Driver");	
    	jevis = new JEVisDataSourceSQL(host, port, sqlSchema, sqlUser, sqlPW);
    	jevis.connect(jevisUser, jevisPW);
    	}catch(JEVisException e){
    		e.printStackTrace();
    		logger.error("Connection error! Check Jevis settings and try again!");
    	}
    }
  
  private void fetchingInformationFromInPut(BufferedDataTable table, JEVisWriter writer, JEVisObject obj) throws JEVisException{
	  List<JEVisSample> samples = new ArrayList<>();
		for(DataRow row : table) {
            DataCell cell0 = row.getCell(0);
            DataCell cell1 = row.getCell(1);
            DataCell cell2 = row.getCell(2);
            if (!cell0.isMissing() && !cell1.isMissing() && !cell2.isMissing()) {
            	
            	GregorianCalendar cal = new GregorianCalendar(((DateAndTimeValue)cell0).getYear(),((DateAndTimeValue)cell0).getMonth(),((DateAndTimeValue)cell0).getDayOfMonth(),((DateAndTimeValue)cell0).getHourOfDay(), ((DateAndTimeValue)cell0).getMinute(),((DateAndTimeValue)cell0).getSecond());
            	
 	           	DateTime date = new DateTime(cal.getTimeInMillis());
 	           	
 	           	double value = ((DoubleValue) cell1).getDoubleValue();
 	           	String unit = ((StringValue) cell2).getStringValue();
 	           	logger.info(" "+ value + " "+ unit);
 	           	writer.addData(obj, date, value, unit, samples);
            
            }         	
        }
		try{
		obj.getAttribute("Value").addSamples(samples);
		obj.commit();
		}catch(JEVisException e){
			logger.error("Inserting Data failed.");

		}		
  }
        
  private void fetchingMetaData(JEVisObject object) throws JEVisException{
	  for(JEVisAttribute att: object.getAttributes()){
		 
		 if(!att.getName().equals(DataBaseConfiguration.valueAttributeName)){
			 List<JEVisSample> sampellist = new ArrayList<JEVisSample>();
			 JEVisSample sample = att.buildSample(null, peekFlowVariableString(att.getName()));
			 sampellist.add(sample);
			 att.deleteAllSample();
			 att.addSamples(sampellist);
		 }
	  }
	  
  }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

  	  for(SettingsModel model : settingsModels){
      	   model.saveSettingsTo(settings);
         }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.
    	  for(SettingsModel model : settingsModels){
       	   model.loadSettingsFrom(settings);
          }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
               	
  	  for(SettingsModel model : settingsModels){
      	   model.validateSettings(settings);
         }

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // no op
    
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
    	// no op
    	 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

