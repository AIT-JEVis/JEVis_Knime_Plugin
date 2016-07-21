package org.ait.knimejevisplugin.getdata;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.text.DateFormatter;

import org.ait.knimejevisplugin.DataBaseConfiguration;
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
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.RearrangeColumnsTable;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.date.DateAndTimeValue;
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
import org.knime.core.node.defaultnodesettings.SettingsModel;
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
	protected static final NodeLogger logger = NodeLogger
            .getLogger("GetDataLogger");
	
	private JEVisObject jObject;
	
	private List<SettingsModel> settingsmodels= new ArrayList<SettingsModel>();
	
	//Dialog information
	static final int nodeID = 494;
	static final String CFGKEY_nodeID = "Node ID";


	//Timeformat yyyy-MM-dd HH:mm:ss.s
	String startTime = "startTime" ;// "2016-01-20 00:00:00.0";
	String endTime = "endTime"; // "2016-01-22 00:00:00.0";
	static final String startDate = "startDate"; //"2016-01-20"
	static final String endDate = "endDate"; //"2016-01-22"
	static final String startDateyear = "start year";
	static final String startDateMonth = "start month";
	static final String startDateDay = "start day"; 
	
	static final String startHour = "Hour";
	static final String startMinute = "Minute";
	static final String startSecond = "Second";
	
	static final String endDateyear = "end year";
	static final String endDateMonth = "end month";
	static final String endDateDay = "end day"; 
	
	static final String endHour = "endHour";
	static final String endMinute = "endMinute";
	static final String endSecond = "endSecond";
	
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
    
    private JEVisObject found; 
    private ArrayList<JEVisSample> list_timefilvalue = new ArrayList<JEVisSample>();
    /**
     * Constructor for the node model.
     */
    protected JevisGetDataNodeModel() {
           //one input and one output table
        super(1, 1);
        
        settingsmodels.add(m_endDateDay);
        settingsmodels.add(m_endDateMonth);
        settingsmodels.add(m_endDateYear);
        settingsmodels.add(m_endHour);
        settingsmodels.add(m_endMinute);
        settingsmodels.add(m_endSeconds);
        settingsmodels.add(m_nodeID);
        
        settingsmodels.add(m_startDateDay);
        settingsmodels.add(m_startDateMonth);
        settingsmodels.add(m_startDateYear);
        settingsmodels.add(m_startMinute);
        settingsmodels.add(m_startHour);
        settingsmodels.add(m_startSeconds);
    }

    
    //setting up the settingsmodel 
    private final SettingsModelInteger m_nodeID = new SettingsModelInteger(
    		JevisGetDataNodeModel.CFGKEY_nodeID, JevisGetDataNodeModel.nodeID);
    
    //private final SettingsModelString m_startTime = new SettingsModelString(
    //		JevisGetDataNodeModel.startTime, "2016-01-20 00:00:00.0");
    
    //private final SettingsModelString m_endTime = new SettingsModelString(
    //		JevisGetDataNodeModel.endTime, "2016-01-22 00:00:00.0");
    private final SettingsModelString m_startDate = new SettingsModelString(
    		JevisGetDataNodeModel.startDate, "2016-01-20");
    private final SettingsModelString m_endDate = new SettingsModelString(
    		JevisGetDataNodeModel.endDate, "2016-01-22");
    private final SettingsModelString m_startDateYear = new SettingsModelString(
    		JevisGetDataNodeModel.startDateyear, "2000");
    private final SettingsModelString m_startDateMonth = new SettingsModelString(
    		JevisGetDataNodeModel.startDateMonth, "01");
    private final SettingsModelString m_startDateDay = new SettingsModelString(
    		JevisGetDataNodeModel.startDateDay, "01");
    private final SettingsModelString m_startHour= new SettingsModelString(
    		JevisGetDataNodeModel.startHour, "0");
    private final SettingsModelString m_startMinute= new SettingsModelString(
    		JevisGetDataNodeModel.startMinute, "0");		
    private final SettingsModelString m_startSeconds= new SettingsModelString(
    		JevisGetDataNodeModel.startSecond, "0");
    
    private final SettingsModelString m_endDateYear = new SettingsModelString(
    		JevisGetDataNodeModel.endDateyear, "2016");
    private final SettingsModelString m_endDateMonth = new SettingsModelString(
    		JevisGetDataNodeModel.endDateMonth, "01");
    private final SettingsModelString m_endDateDay = new SettingsModelString(
    		JevisGetDataNodeModel.endDateDay, "01");
    private final SettingsModelString m_endHour= new SettingsModelString(
    		JevisGetDataNodeModel.endHour, "0");
    private final SettingsModelString m_endMinute= new SettingsModelString(
    		JevisGetDataNodeModel.endMinute, "0");		
    private final SettingsModelString m_endSeconds= new SettingsModelString(
    		JevisGetDataNodeModel.endSecond, "0");
    /**
     * 
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
    			    			
    			if(!m_startDateMonth.getStringValue().matches("\\d\\d")){
    				m_startDateMonth.setStringValue("0"+ m_startDateMonth.getStringValue());
    			}
    			if(!m_startDateDay.getStringValue().matches("\\d\\d")){
    				m_startDateDay.setStringValue("0"+ m_startDateDay.getStringValue());
    			}
    			if(!m_startHour.getStringValue().matches("\\d\\d")){
    				m_startHour.setStringValue("0"+ m_startHour.getStringValue());
    			}
    			if(!m_startMinute.getStringValue().matches("\\d\\d")){
    				m_startMinute.setStringValue("0"+ m_startMinute.getStringValue());
    			}
    			if(!m_startSeconds.getStringValue().matches("\\d\\d")){
    				m_startSeconds.setStringValue("0"+ m_startSeconds.getStringValue());
    			}
    		
    			startTime = m_startDateYear.getStringValue()+"-"+
    				m_startDateMonth.getStringValue()+"-"+
    				m_startDateDay.getStringValue()+" "+
    				m_startHour.getStringValue()+":"+
    				m_startMinute.getStringValue()+":"+
    				m_startSeconds.getStringValue()+".0";
    			
    			if(!m_endDateMonth.getStringValue().matches("\\d\\d")){
    				m_endDateMonth.setStringValue("0"+ m_endDateMonth.getStringValue());
    			}
    			if(!m_endDateDay.getStringValue().matches("\\d\\d")){
    				m_endDateDay.setStringValue("0"+ m_endDateDay.getStringValue());
    			}
    			if(!m_endHour.getStringValue().matches("\\d\\d")){
    				m_endHour.setStringValue("0"+ m_endHour.getStringValue());
    			}
    			if(!m_endMinute.getStringValue().matches("\\d\\d")){
    				m_endMinute.setStringValue("0"+ m_endMinute.getStringValue());
    			}
    			if(!m_endSeconds.getStringValue().matches("\\d\\d")){
    				m_endSeconds.setStringValue("0"+ m_endSeconds.getStringValue());
    			}
    			
    			endTime = m_endDateYear.getStringValue()+"-"+
    				m_endDateMonth.getStringValue()+"-"+
    				m_endDateDay.getStringValue()+" "+
    				m_endHour.getStringValue()+":"+
    				m_endMinute.getStringValue()+":"+
    				m_endSeconds.getStringValue()+".0";
    			
    			startTime.trim();
    			endTime.trim();
    			if(startTime.matches(
    					"\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d.\\d")
    					&&endTime.matches(
    					"\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d.\\d")){
    				//yyyy-MM-dd HH:mm:ss.s"
    				logger.info("Matching");
    			}
    			
	    		//Specifying outport TableSpec
	    		DataColumnSpec tsCol = new DataColumnSpecCreator(
	    				"Timestamp", DateAndTimeCell.TYPE).createSpec();
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
	    		logger.info("generarting MetaData");
	    		pushFlowVariableString("sensorname", jObject.getParents().get(0).getName());
	    		pushFlowVariableDouble("DataNodeID",jObject.getID());
	    		pushFlowVariableDouble("ParentNodeID", jObject.getParents().get(0).getID());	    		
	    		pushFlowVariableString("Project", getParent(
	    				jObject, DataBaseConfiguration.projectLevelName).getName());
	    		if(getParent(jObject, DataBaseConfiguration.locationLevelName).
	    				getAttribute("Location").hasSample()){
		    		pushFlowVariableString("Location", getParent(
		    				jObject, DataBaseConfiguration.locationLevelName)
		    				.getAttribute("Location").getLatestSample().getValueAsString());
	    		}else{
	    			pushFlowVariableString("Location", " ");
	    		}
	    	
	    		pushFlowVariableString("Building", getParent(
	    				jObject, DataBaseConfiguration.locationLevelName).getName());
	    		//ParentNode ID
	    		//Project, Location, Building,
	    		
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
    
    private JEVisObject getParent(JEVisObject input, String level) throws JEVisException{
    	
    	for(JEVisObject parent: input.getParents()){
    		if(checkLevel(parent, level)){
    			found = parent;
    			return parent;
    			
    		}
    		else{
    			getParent(parent, level);
    		}
    	}
    	return found;
    }
    
	protected boolean checkLevel(JEVisObject object, String level) throws JEVisException{
		if(object.getJEVisClass().equals(jevis.getJEVisClass(level))){
			return true;
		}
		return false;
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
	        	
//	            DateTime timestampString = value.getTimestamp();
	            //DateTimeFormatter mformatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.s");
	            //String timestamp = mformatter.print(timestampString);
	            //logger.info("Working on Timestamp: "+ timestamp);                     
	            //Filling the rows of the table
	            GregorianCalendar cal = value.getTimestamp().toGregorianCalendar();
	           //Put here the Greogrian Calendar
	            
	            DataCell[]cells = new DataCell[result.getNumColumns()];
	            cells[0] = new DateAndTimeCell(cal.get(Calendar.YEAR), 
	            		cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
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
	        
	        DateTime start_Time = formatter.parseDateTime(startTime);
	        DateTime end_Time = formatter.parseDateTime(endTime);
		    if(start_Time.isBefore(timestampString) && end_Time.isAfter(timestampString)){
		    	//logger.info("time: " + timestamp);
		    	list_timefilvalue.add(value);
		    }
	        
		} catch (JEVisException e) {
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
    	/*
    	for(int i = 0; i< inSpecs[IN_PORT].getNumColumns(); i++){
    		DataColumnSpec c = inSpecs[IN_PORT].getColumnSpec(i);
    		if(i == 0){
        		if(!c.getType().isCompatible(LongValue.class)){
        	 		throw new InvalidSettingsException(
            				"Invalid column type at first column"); 
        		}
    		}
    	}*/
    	
        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	for(SettingsModel settingsmodel : settingsmodels){
    		settingsmodel.saveSettingsTo(settings);
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	for(SettingsModel settingsmodel : settingsmodels){
    		settingsmodel.loadSettingsFrom(settings);
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	
    	for(SettingsModel settingsmodel : settingsmodels){
    		settingsmodel.validateSettings(settings);
    	}    	
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
