package org.ait.knime.getdata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisRelationship;
import org.jevis.api.JEVisSample;
import org.jevis.api.JEVisType;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;



/**
 * This is the model implementation of JevisGetData.
 * 
 *
 * @author Monschiebl
 */
public class JevisGetDataNodeModel extends NodeModel {
    
	//Setting Table Port
	static final int IN_PORT = 0;
	
	//Creating logger
	private static final NodeLogger logger = NodeLogger
            .getLogger("GetDataLogger");
	
	//Dialog information
	static final int nodeID = 50;
	static final String CFGKEY_nodeID = "Node ID";

	//Jevis Connection information
	public static String host = "jevis3.ait.ac.at";
 	public static String port = "3306";
 	public static String sqlSchema = "jevis";
 	public static String sqlUser = "jevis";
 	public static String sqlPW = "vu5eS1ma";
 	
 	public static String jevisUser = "BerhnardM";
 	public static String jevisPW = "testpass01593";
	
 	//Setting up variables for needed Processing
 	 private JEVisDataSourceSQL jevis; 
     private BufferedDataContainer buf;
     private int counter = 0;
    /**
     * Constructor for the node model.
     */
    protected JevisGetDataNodeModel() {
           //one input and one output table
        super(1, 1);
    }

    //setting up the settingsmodel 
    private final SettingsModelInteger m_nodeID = new SettingsModelInteger(
    		JevisGetDataNodeModel.CFGKEY_nodeID, JevisGetDataNodeModel.nodeID);
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	//setting up Logger
    	logger.setLevel(NodeLogger.LEVEL.INFO);
    	logger.info("JevisDataInformation");
    	
    	connectingtojevis();
    	if(jevis.isConnectionAlive()){
    			
    		//Specifying outport TableSpec
    		DataColumnSpec colSpec1 = new DataColumnSpecCreator(
    				"Timestamp", StringCell.TYPE).createSpec();
    		DataColumnSpec colSpec2 = new DataColumnSpecCreator(
    				"Value", DoubleCell.TYPE).createSpec();
    		DataColumnSpec colSpec3 = new DataColumnSpecCreator(
    				"Comment", StringCell.TYPE).createSpec();
    		
    		DataTableSpec result = new DataTableSpec(colSpec1,colSpec2,colSpec3);
    		buf = exec.createDataContainer(result); 
    		
    		//TODO: Insert table data here
    		JEVisObject my_Object = jevis.getObject((long) m_nodeID.getIntValue()) ;				
    		logger.info("ObjectName: " + my_Object.getName());
    		pushFlowVariableString("sensorname", my_Object.getParents().get(0).getName());
    		pushFlowVariableDouble("DataNodeID", my_Object.getID());
    		
            List<JEVisSample> valueList = my_Object.getAttribute("Value").getAllSamples();  
            for (JEVisSample value : valueList) {
            //Getting the data of value for input in table
            	logger.info("Value: "+ value.getValueAsString());              	
                DateTime timestampString = value.getTimestamp();
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.s");
                String timestamp = formatter.print(timestampString);
                logger.info("Timestamp: "+ timestamp);                     
                //Filling the rows of the table
                DataCell[]cells = new DataCell[result.getNumColumns()];
                cells[0] = new StringCell(timestamp);
                cells[1] = new DoubleCell(value.getValueAsDouble());
                cells[2] = new StringCell("");
                counter++;
                DataRow row = new DefaultRow("Row"+ counter, cells);
                buf.addRowToTable(row);              
            }
    	}else{
    		logger.error("Jevis connection error!");
    	}
    	buf.close();
    	BufferedDataTable out = buf.getTable();
        // TODO: Return a BufferedDataTable for each output port 
        return new BufferedDataTable[]{out};
    }

    public void connectingtojevis(){
    	
    	if(getAvailableFlowVariables().containsKey("host")
    			&& getAvailableFlowVariables().containsKey("port")
    			&& getAvailableFlowVariables().containsKey("sqlSchema")
    			&& getAvailableFlowVariables().containsKey("sqlUser")
    			&& getAvailableFlowVariables().containsKey("sqlPW")){
    	//getting Connection information from first node
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
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        // TODO: generated method stub
        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         // TODO: generated method stub
    	m_nodeID.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	m_nodeID.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	m_nodeID.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

}
