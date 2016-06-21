package org.ait.knimejevisplugin.selectdata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ait.knimejevisplugin.getdata.JevisGetDataNodeModel;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.TimestampCell;
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
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import javafx.scene.control.DateCell;

/**
 * This is the model implementation of JevisSelectData.
 * 
 *
 * @author Monschiebl
 */
public class JevisSelectDataNodeModel extends NodeModel {
    /**
     * Constructor for the node model.
     */
    protected JevisSelectDataNodeModel() {
    
        // TODO: Specify the amount of input and output ports needed.
        super(0, 1);
    }

    static final NodeLogger logger = NodeLogger
            .getLogger("SelectDataLogger");
    
    int counter = 0;
  //Jevis Connection information
  	public static String host = "Hostaddress";
   	public static String port = "Port";
   	public static String sqlSchema = "Schema";
   	public static String sqlUser = "User";
   	public static String sqlPW = "Password";
   	
   	public static String jevisUser = "BerhnardM";
   	public static String jevisPW = "testpass01593";
 
   	public static String nodeID = "NodeID";
   	public static int DEFAULT_NODEID = 483;
   	
   	//Search for Attributes information
   	public static String enableAttribute = "enableAttribute";
   	
   	public static String project = "Project";
   	public static String location = "Location";
   	public static String nodeType = "NodeType";
   	public static String devicetype = "DeviceType";
   	public static String component = "Component";
   	public static String searchNodeType = "SearchNodeType";
   	public static String searchDeviceType = "SearchDeviceType";
   	public static String searchComponentType = "SearchComponentType";
   	
   	public static String enableProject = "enableProject";
   	public static String enableLocation = "enableLocation";
   	public static String enableNodeType= "enableNodeType";
   	public static String enableComponent= "enableComponent";
   	public static String enableDevice = "enableDevice";
   	
   	public static String enableStructure = "enableStructure";
   	
   	public static String parent = "parent";
   	public static String children= "children";
   	public static String allChildren = "allChildren";
   	public static String siblings = "siblings";
   	
   	private JEVisDataSourceSQL jevis;
   	//Processing variables
	private BufferedDataContainer buf;
	
	//Settingsmodells
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
   	
   	private final SettingsModelInteger m_nodeID = new SettingsModelInteger(
   			nodeID, DEFAULT_NODEID);
   	
   	private final SettingsModelBoolean m_enableAttribute = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableAttribute,true);
   	
   	private final SettingsModelString m_project = new SettingsModelString(
   			JevisSelectDataNodeModel.project," ");
   	private final SettingsModelString m_location = new SettingsModelString(
   			JevisSelectDataNodeModel.location," ");
   	private final SettingsModelString m_nodeType = new SettingsModelString(
   			JevisSelectDataNodeModel.nodeType," ");
   	private final SettingsModelString m_devicetype = new SettingsModelString(
   			JevisSelectDataNodeModel.devicetype," ");
   	private final SettingsModelString m_component = new SettingsModelString(
   			JevisSelectDataNodeModel.component," ");
   	
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
   	
   	private final SettingsModelString m_searchNodeTyoe = new SettingsModelString(
   			JevisSelectDataNodeModel.searchNodeType, " ");
   	private final SettingsModelString m_searchDeviceType = new SettingsModelString(
   			JevisSelectDataNodeModel.searchDeviceType, " ");
   	private final SettingsModelString m_searchComponentType = new SettingsModelString(
   			JevisSelectDataNodeModel.searchComponentType, " ");
   	
   	private final SettingsModelBoolean m_enableStructure = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableStructure, true);
   	
   	private final SettingsModelBoolean m_parents = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.parent, false);
   	private final SettingsModelBoolean m_children = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.children, false);
   	private final SettingsModelBoolean m_siblings = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.siblings, false);
   	private final SettingsModelBoolean m_allChildren = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.allChildren, false);
   	
    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	logger.setLevel(NodeLogger.LEVEL.INFO);
    	
    	connectingtojevis();
    	buf = exec.createDataContainer(createOutputTableSpec());
    	
    	if(jevis.isConnectionAlive()){
    		logger.info("Connection Alive!");

    		SearchPattern searcher = new SearchPattern(jevis, m_project.getStringValue(),
    				m_location.getStringValue(),m_nodeType.getStringValue(),m_devicetype.getStringValue(),
    				m_component.getStringValue(), m_nodeID.getIntValue(), m_parents.getBooleanValue(),
    				m_children.getBooleanValue(),m_siblings.getBooleanValue(),m_allChildren.getBooleanValue(),
    				m_enableProject.getBooleanValue(), m_enableLocation.getBooleanValue(),
    				m_enableNodeType.getBooleanValue(), m_enableDevice.getBooleanValue(),
    				m_enableComponent.getBooleanValue());
    		List<JEVisObject> searchresult= searcher.searchData();
    		
    		DataTableSpec result = createOutputTableSpec();
    		for(JEVisObject search : searchresult){
    			DataCell[]cells = new DataCell[result.getNumColumns()];
 	            cells[0] = new LongCell(search.getID());
 	            cells[1] = new StringCell(search.getName());
 	            cells[2] = new StringCell("");
 	            cells[3] = new StringCell("");
 	            cells[4] = new StringCell("");
 	            cells[5] = new StringCell("");
 	        //    cells[6] = new StringCell("");
 	            if(search.getAttributes()== null){
 	            	cells[7] = new DateAndTimeCell(0, 0, 0);
 	            	cells[6]= new DateAndTimeCell(0, 0, 0);
 	            }
 	            else if(search.getAttribute("Value") != null){
 		            cells[7] = new DateAndTimeCell(
 	 	            		search.getAttribute("Value").getTimestampFromFirstSample().getYear(),
 	 	            		search.getAttribute("Value").getTimestampFromFirstSample().getMonthOfYear(), 
 	 	            		search.getAttribute("Value").getTimestampFromFirstSample().getDayOfMonth());
 	 	            cells[6] = new DateAndTimeCell(
 	 	            		search.getAttribute("Value").getTimestampFromLastSample().getYear(),
 	 	            		search.getAttribute("Value").getTimestampFromLastSample().getMonthOfYear(), 
 	 	            		search.getAttribute("Value").getTimestampFromLastSample().getDayOfMonth());
 	            }else{
 	            	cells[7] = new DateAndTimeCell(0, 0, 0);
 	            	cells[6] = new DateAndTimeCell(0, 0, 0);
 	            }

 	            counter++;
	            DataRow row = new DefaultRow("Row"+ counter, cells);
	            buf.addRowToTable(row);
    		}
    		/*
    		//TODO: Search after structure not implemented yet
    		JEVisObject jObject = jevis.getObject((long) m_nodeID.getIntValue()) ;
    		//logger.info(jObject.getJEVisClass().getType("Value").getPrimitiveType());
    		//logger.info(jObject.getAttribute("Value").getType().toString());
    		List<JEVisObject> list_Children = jObject.getChildren();
    		for(JEVisObject child : list_Children){
    			logger.info(child.getName());
    		}
    		for(JEVisObject serch :searcher.searchData())
    		logger.warn(serch.getName());*/
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
    		logger.info("Connection flow variabels found!");
    	jhost.setStringValue(peekFlowVariableString("host"));
    	jport.setStringValue(peekFlowVariableString("port"));
    	jSchema.setStringValue(peekFlowVariableString("sqlSchema"));
    	jUser.setStringValue(peekFlowVariableString("sqlUser"));
    	jPW.setStringValue(peekFlowVariableString("sqlPW"));
    	}
    	
    	try{
    	//Connecting to Jevis with connection information
    	jevis = new JEVisDataSourceSQL(jhost.getStringValue(), jport.getStringValue(), jSchema.getStringValue(), jUser.getStringValue(), jPW.getStringValue());
    	jevis.connect(jevisUser, jevisPW);
    	
    	pushFlowVariableString("host", jhost.getStringValue());
    	pushFlowVariableString("port", jport.getStringValue());
    	pushFlowVariableString("sqlSchema", jSchema.getStringValue());
    	pushFlowVariableString("sqlUser", jUser.getStringValue());
    	pushFlowVariableString("sqlPW", jPW.getStringValue());
    	}catch(JEVisException e){
    		e.printStackTrace();
    		logger.error("Connection error! Check Jevis settings and try again!");
    	}
    	
    }
 
    		
    private void getInformationFromNodeID(JEVisObject searchObject){
    	
    }
    
    private void fillTableWithAllInformation(){
    	//TODO:metzhod stub
    }
    
    private DataTableSpec createOutputTableSpec(){
    	DataColumnSpec nodeIDSpec = new DataColumnSpecCreator(
    			"NodeID", LongCell.TYPE).createSpec();
    	DataColumnSpec deviceTypeSpec = new DataColumnSpecCreator(
    			"Devicetype", StringCell.TYPE).createSpec();
    	DataColumnSpec componentTypeSpec = new DataColumnSpecCreator(
    			"Component", StringCell.TYPE).createSpec();
    	DataColumnSpec locationSpec = new DataColumnSpecCreator(
    			"Location", StringCell.TYPE).createSpec();
    	DataColumnSpec projectSpec = new DataColumnSpecCreator(
    			"Project", StringCell.TYPE).createSpec();
    	DataColumnSpec floorSpec = new DataColumnSpecCreator(
    			"Floor", StringCell.TYPE).createSpec();
    	DataColumnSpec firstTsSpec = new DataColumnSpecCreator(
    			"First Timestamp", DateAndTimeCell.TYPE).createSpec();
    	DataColumnSpec lastTSSpec = new DataColumnSpecCreator(
    			"Last Timestamp", DateAndTimeCell.TYPE).createSpec();
    	
    	DataTableSpec outputTableSpec = new DataTableSpec(nodeIDSpec, deviceTypeSpec, componentTypeSpec,
    			locationSpec, projectSpec, floorSpec, firstTsSpec, lastTSSpec);
    	
    	return outputTableSpec;
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
        //Saving ConnectionSettings
    	jhost.saveSettingsTo(settings);
    	jport.saveSettingsTo(settings);
    	jSchema.saveSettingsTo(settings);
    	jUser.saveSettingsTo(settings);
    	jPW.saveSettingsTo(settings);
    	jevUser.saveSettingsTo(settings);
    	jevPW.saveSettingsTo(settings);
    	//Saving Search Settings
    	m_project.saveSettingsTo(settings);
    	m_location.saveSettingsTo(settings);
    	m_nodeType.saveSettingsTo(settings);
    	m_devicetype.saveSettingsTo(settings);
    	m_component.saveSettingsTo(settings);
    	
    	m_parents.saveSettingsTo(settings);
    	m_children.saveSettingsTo(settings);
    	m_siblings.saveSettingsTo(settings);
    	m_allChildren.saveSettingsTo(settings);
    	
    	m_enableAttribute.saveSettingsTo(settings);
    	m_enableComponent.saveSettingsTo(settings);
    	m_enableDevice.saveSettingsTo(settings);
    	m_enableLocation.saveSettingsTo(settings);
    	m_enableNodeType.saveSettingsTo(settings);
    	m_enableProject.saveSettingsTo(settings);
    	m_enableStructure.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	jhost.loadSettingsFrom(settings);
    	jport.loadSettingsFrom(settings);
    	jSchema.loadSettingsFrom(settings);
    	jUser.loadSettingsFrom(settings);
    	jPW.loadSettingsFrom(settings);
    	jevUser.loadSettingsFrom(settings);
    	jevPW.loadSettingsFrom(settings);
    	
    	m_project.loadSettingsFrom(settings);
    	m_location.loadSettingsFrom(settings);
    	m_nodeType.loadSettingsFrom(settings);
    	m_devicetype.loadSettingsFrom(settings);
    	m_component.loadSettingsFrom(settings);
    	
    	m_parents.loadSettingsFrom(settings);
    	m_children.loadSettingsFrom(settings);
    	m_siblings.loadSettingsFrom(settings);
    	m_allChildren.loadSettingsFrom(settings);
    	
    	m_enableAttribute.loadSettingsFrom(settings);
    	m_enableComponent.loadSettingsFrom(settings);
    	m_enableDevice.loadSettingsFrom(settings);
    	m_enableLocation.loadSettingsFrom(settings);
    	m_enableNodeType.loadSettingsFrom(settings);
    	m_enableProject.loadSettingsFrom(settings);
    	m_enableStructure.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    	jhost.validateSettings(settings);
    	jport.validateSettings(settings);
    	jSchema.validateSettings(settings);
    	jUser.validateSettings(settings);
    	jPW.validateSettings(settings);
    	jevUser.validateSettings(settings);
    	jevPW.validateSettings(settings);
    	
    	m_project.validateSettings(settings);
    	m_location.validateSettings(settings);
    	m_nodeType.validateSettings(settings);
    	m_devicetype.validateSettings(settings);
    	m_component.validateSettings(settings);
    	
    	m_parents.validateSettings(settings);
    	m_children.validateSettings(settings);
    	m_siblings.validateSettings(settings);
    	m_allChildren.validateSettings(settings);

    	m_enableAttribute.validateSettings(settings);
    	m_enableComponent.validateSettings(settings);
    	m_enableDevice.validateSettings(settings);
    	m_enableLocation.validateSettings(settings);
    	m_enableNodeType.validateSettings(settings);
    	m_enableProject.validateSettings(settings);
    	m_enableStructure.validateSettings(settings);
    	
    	
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

