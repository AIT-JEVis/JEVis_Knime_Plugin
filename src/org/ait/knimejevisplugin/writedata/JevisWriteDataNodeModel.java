package org.ait.knimejevisplugin.writedata;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisFile;
import org.jevis.api.JEVisMultiSelection;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.api.JEVisSelection;
import org.jevis.api.JEVisType;
import org.jevis.api.JEVisUnit;
import org.jevis.api.sql.AttributeTable;
import org.jevis.api.sql.JEVisAttributeSQL;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.api.sql.JEVisObjectSQL;
import org.jevis.api.sql.JEVisSampleSQL;
import org.jevis.commons.database.JEVisObjectDataManager;
import org.jevis.commons.driver.JEVisImporter;
import org.jevis.commons.driver.JEVisImporterAdapter;
import org.joda.time.DateTime;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;
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
	static String parentID = "ParentID";
	 List<SettingsModel> settingsModels = new ArrayList<SettingsModel>();
	
		public static String host = "jevis3.ait.ac.at";
	 	public static String port = "3306";
	 	public static String sqlSchema = "jevis";
	 	public static String sqlUser = "jevis";
	 	public static String sqlPW = "vu5eS1ma";
	 	
	 	public static String jevisUser = "BerhnardM";
	 	public static String jevisPW = "testpass01593"; 
	 
	 
	private final SettingsModelLong m_objID = new SettingsModelLong(objID, 0);	
	private final SettingsModelBoolean m_update = new SettingsModelBoolean(updateDataPoint, false);
	private final SettingsModelBoolean m_newDataPoint = new SettingsModelBoolean(newDataPoint, false);
	private final SettingsModelLong m_parentID = new SettingsModelLong(parentID, 0);
	
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(JevisWriteDataNodeModel.class);
        

    /**
     * Constructor for the node model.
     */
    protected JevisWriteDataNodeModel() {
        // TODO one incoming port and one outgoing port is assumed
        super(1, 0);
       
        settingsModels.add(m_update);
        settingsModels.add(m_objID);
        settingsModels.add(m_newDataPoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	connectingtojevis();
    	if(jevis.isConnectionAlive()){
        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");
        	JEVisWriter writer = new JEVisWriter(jevis);
        	
        	if(m_update.getBooleanValue()){
            	JEVisObject obj = jevis.getObject(m_objID.getLongValue());
        		String name = "data";
            	writer.createNewDatapointUnderParent(m_objID.getLongValue(), name);
            	
        	}
        	
        	if(m_newDataPoint.getBooleanValue()){
       

        
        	
        		
        	}
    	}

        	
        	return new BufferedDataTable[]{};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

  public void connectingtojevis(){
    	
    	//getting Connection information from selection node if existing
    	if(getAvailableFlowVariables().containsKey("host")
    			&& getAvailableFlowVariables().containsKey("port")
    			&& getAvailableFlowVariables().containsKey("sqlSchema")
    			&& getAvailableFlowVariables().containsKey("sqlUser")
    			&& getAvailableFlowVariables().containsKey("sqlPW")){
	    	
	    	host = peekFlowVariableString("host");
	    	port = peekFlowVariableString("port");
	    	sqlSchema = peekFlowVariableString("sqlSchema");
	    	sqlUser = peekFlowVariableString("sqlUser");
	    	sqlPW = peekFlowVariableString("sqlPW");
    	}
    	
    	try{
    	//Connecting to Jevis with connection information
    	jevis = new JEVisDataSourceSQL(host, port, sqlSchema, sqlUser, sqlPW);
    	jevis.connect(jevisUser, jevisPW);
    	}catch(JEVisException e){
    		e.printStackTrace();
    		logger.error("Connection error! Check Jevis settings and try again!");
    	}
    	
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.
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
            
        // TODO load (valid) settings from the config object.
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
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.

       

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
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
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

