package org.ait.knimejevisplugin.getdata;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.text.DateFormatter;

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
import org.knime.core.data.container.RearrangeColumnsTable;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.LongCell;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;



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
	
	private JEVisObject jObject;
	
	//Dialog information
	static final int nodeID = 494;
	static final String CFGKEY_nodeID = "Node ID";


	//Timeformat yyyy-MM-dd HH:mm:ss.s
	static final String startTime = "startTime" ;// "2016-01-20 00:00:00.0";
	static final String endTime = "endTime"; // "2016-01-22 00:00:00.0";

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
     private String attributeName = "Value";
     
     private ArrayList<JEVisSample> list_timefilvalue = new ArrayList<JEVisSample>();
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
    
    private final SettingsModelString m_startTime = new SettingsModelString(
    		JevisGetDataNodeModel.startTime, "2016-01-20 00:00:00.0");
    
    private final SettingsModelString m_endTime = new SettingsModelString(
    		JevisGetDataNodeModel.endTime, "2016-01-22 00:00:00.0");
    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	//setting up Logger
    	logger.setLevel(NodeLogger.LEVEL.INFO);
    	logger.info("JevisDataInformation");
    	DataTableSpec result = new DataTableSpec();
    	buf= exec.createDataContainer(result);
    	connectingtojevis();
    	if(jevis.isConnectionAlive()){
    			
    		JEVisObject jObject = jevis.getObject((long) m_nodeID.getIntValue()) ;				
    		logger.info("ObjectName: " + jObject.getName());
    		
    		if(jObject.getAttribute(attributeName) != null){
    			
    			
	    		//Specifying outport TableSpec
	    		DataColumnSpec tsCol = new DataColumnSpecCreator(
	    				"Timestamp", StringCell.TYPE).createSpec();
	    		DataColumnSpec valueCol;
	    		//setting type of Value Column as type of value in Jevis.
	    		if(jObject.getAttribute(attributeName).getPrimitiveType() == 0){
	    			valueCol = new DataColumnSpecCreator(
	    					"Value", StringCell.TYPE).createSpec();
	    		}
	    		else if(jObject.getAttribute(attributeName).getPrimitiveType() == 1){
	    			valueCol = new DataColumnSpecCreator(
	    					"Value", DoubleCell.TYPE).createSpec();
	    		}
	    		else if(jObject.getAttribute(attributeName).getPrimitiveType() == 2){
	    			valueCol = new DataColumnSpecCreator(
	    					"Value", LongCell.TYPE).createSpec();
	    		}
	    		else if(jObject.getAttribute(attributeName).getPrimitiveType() == 4){
	    			valueCol = new DataColumnSpecCreator(
	    					"Value", BooleanCell.TYPE).createSpec();
	    		}else{
	    			valueCol = new DataColumnSpecCreator(
	    					"Value", DoubleCell.TYPE).createSpec();
	    		}	    		
	    		DataColumnSpec commentCol = new DataColumnSpecCreator(
	    				"Comment", StringCell.TYPE).createSpec();
	    		
	    		result = new DataTableSpec(tsCol,valueCol,commentCol);
	    		buf = exec.createDataContainer(result);
	    		
	    		//Pushing basic information of table into flow variables
	    		pushFlowVariableString("sensorname", jObject.getParents().get(0).getName());
	    		pushFlowVariableDouble("DataNodeID",jObject.getID());
		
					//filling Table with data
	    			logger.info("Values found!");
	    			fillingTable(jObject, result);
	    			exec.checkCanceled();
    		}else{
    			logger.error("Check NodeID! NodeID don't has an attribute called Value! "
    					+ " Or node doesn't exist.");
    			
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
    
    private void fillingTable(JEVisObject my_Object, DataTableSpec result){
    	
        List<JEVisSample> valueList;
		try {
			valueList = my_Object.getAttribute(attributeName).getAllSamples();

	        for (JEVisSample value : valueList) {
	        //Getting the data of value for input in table
	        	filterDate(value);
	        }
	        for(JEVisSample value : list_timefilvalue){
	        	
	            DateTime timestampString = value.getTimestamp();
	            DateTimeFormatter mformatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.s");
	            String timestamp = mformatter.print(timestampString);
	            //logger.info("Working on Timestamp: "+ timestamp);                     
	            //Filling the rows of the table
	            DataCell[]cells = new DataCell[result.getNumColumns()];
	            cells[0] = new StringCell(timestamp);
	            cells[1] = new DoubleCell(value.getValueAsDouble());
	            cells[2] = new StringCell("");
	            counter++;
	            DataRow row = new DefaultRow("Row"+ counter, cells);
	            buf.addRowToTable(row); 
	            
	        }

		} catch (JEVisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }
    
    private void filterDate(JEVisSample value){
    	
    	 DateTime timestampString;
		try {
			timestampString = value.getTimestamp();
	        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.s");
	        String timestamp = formatter.print(timestampString);
	        
	        DateTime start_Time = formatter.parseDateTime(m_startTime.getStringValue());
	        DateTime end_Time = formatter.parseDateTime(m_endTime.getStringValue());
	    	if(start_Time.isBefore(timestampString) && end_Time.isAfter(timestampString)){
	    		logger.info("time: " + timestamp);
	    		list_timefilvalue.add(value);
	    	}
		} catch (JEVisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    	m_startTime.saveSettingsTo(settings);
    	m_endTime.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	m_nodeID.loadSettingsFrom(settings);
    	m_startTime.loadSettingsFrom(settings);
    	m_endTime.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	m_nodeID.validateSettings(settings);
    	m_startTime.validateSettings(settings);
    	m_endTime.validateSettings(settings);
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
