package org.ait.knimejevisplugin.selectdata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ait.knimejevisplugin.DataBaseConfiguration;
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
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import javafx.scene.control.DateCell;

/**
 * This is the model implementation of JevisSelectData.
 * 
 *
 * @author Monschiebl
 */
public class JevisSelectDataNodeModel extends NodeModel {
    List<SettingsModel> settingsmodels = new ArrayList<SettingsModel>();
	
	/**
     * Constructor for the node model.
     */
    protected JevisSelectDataNodeModel() {
    
        // TODO: Specify the amount of input and output ports needed.
        super(0, 1);
        settingsmodels.add(jSchema);
        settingsmodels.add(jevPW);
        settingsmodels.add(jUser);
        settingsmodels.add(jevUser);
        settingsmodels.add(m_AttributeSearch);
        settingsmodels.add(m_allChildren);
        settingsmodels.add(m_children);
        settingsmodels.add(m_component);
        settingsmodels.add(m_devicetype);
        settingsmodels.add(m_enableAttribute);
        settingsmodels.add(m_enableDevice);
        settingsmodels.add(m_enableComponent);
        settingsmodels.add(m_enableLocation);
        settingsmodels.add(m_enableNodeSearch);
        settingsmodels.add(m_enableNodeType);
        settingsmodels.add(m_enableProject);
        settingsmodels.add(m_enableStructure);
        settingsmodels.add(m_nodeId);
        settingsmodels.add(m_nodeType);
        settingsmodels.add(m_parents);
        settingsmodels.add(m_project);
        settingsmodels.add(jPW);
        settingsmodels.add(jhost); 
        settingsmodels.add(jport);
        settingsmodels.add(m_location);
        settingsmodels.add(m_siblings);
        
    }

    static final NodeLogger logger = NodeLogger
            .getLogger("SelectDataLogger");
    
    int counter = 0;
 
   	public static String nodeID = "NodeID";
   	public static int DEFAULT_NODEID = 483;
   	
   	//Search for Specific Datapoints information
   	public static String enableNodeSearch = "enableDatapointSearch";
   	
   	public static String searchNodeType = "SearchNodeType";
   	public static String searchDeviceType = "SearchDeviceType";
   	public static String searchComponentType = "SearchComponentType";
   	
   	public static String enableProject = "enableProject";
   	public static String enableLocation = "enableLocation";
   	public static String enableNodeType= "enableNodeType";
   	public static String enableComponent= "enableComponent";
   	public static String enableDevice = "enableDevice";
   	
   	public static String enableAttributeSearch = "enableAttributeSearch";
   	
   	public static String attributeModelName = "Attribute";
   	
   	public static String enableStructure = "enableStructureSearch";
   	
   	public static String parent = "parent";
   	public static String children= "children";
   	public static String allChildren = "allChildren";
   	public static String siblings = "siblings";
   	
   	private JEVisDataSourceSQL jevis;
   	//Processing variables
	private BufferedDataContainer buf;
	
	//Settingsmodells
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
   			JevisSelectDataNodeModel.enableNodeSearch, true);
   	
   	private final SettingsModelString m_project = new SettingsModelString(
   			DataBaseConfiguration.projectModelName," ");
   	private final SettingsModelString m_location = new SettingsModelString(
   			DataBaseConfiguration.locationModelName," ");
   	private final SettingsModelString m_nodeType = new SettingsModelString(
   			DataBaseConfiguration.nodeType," ");
   	private final SettingsModelString m_devicetype = new SettingsModelString(
   			DataBaseConfiguration.deviceModelName," ");
   	private final SettingsModelString m_component = new SettingsModelString(
   			DataBaseConfiguration.componentModelName," ");
   	
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
   	
   	private final SettingsModelBoolean m_enableAttribute = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableAttributeSearch, true);
   	
   	private final SettingsModelString m_AttributeSearch = new SettingsModelString(
   			JevisSelectDataNodeModel.attributeModelName, " ");
   	
   	private final SettingsModelBoolean m_enableStructure = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableStructure, true);
   	
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
   	
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "deprecation" })
	@Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	NodeLogger.setLevel(NodeLogger.LEVEL.INFO);
    	
    	connectingtojevis();
    	
    	
    	if(jevis.isConnectionAlive()){
    		logger.info("Connection Alive!");
    		
    		
    		if(m_enableNodeSearch.getBooleanValue()){
    			buf = exec.createDataContainer(createOutputTableSpecforDatapoints());
    			DataTableSpec datapointResult = createOutputTableSpecforDatapoints();
        		SearchForNodes datapointsearcher = new SearchForNodes(jevis, m_project.getStringValue(),
        				m_location.getStringValue(),m_nodeType.getStringValue(),m_devicetype.getStringValue(),
        				m_component.getStringValue(), m_enableProject.getBooleanValue(),
        				m_enableLocation.getBooleanValue(), m_enableNodeType.getBooleanValue(),
        				m_enableDevice.getBooleanValue(), m_enableComponent.getBooleanValue(),
        				datapointResult);
        		buf = datapointsearcher.searchforDataPoints(buf);
    		}
    		if(m_enableStructure.getBooleanValue()){
    			buf = exec.createDataContainer(createOuputTableSpecforStructure());
    			DataTableSpec structureResult = createOuputTableSpecforStructure();
    			SearchForStructure structuresearcher = new SearchForStructure(jevis, m_nodeId.getLongValue(),
    					m_parents.getBooleanValue(), m_children.getBooleanValue(),
    					m_siblings.getBooleanValue(), m_allChildren.getBooleanValue(),
    					m_enableNodeSearch.getBooleanValue());
    			buf = structuresearcher.fillTableWithStructureSearchResult(buf, structureResult);
    			
    			/*
    			SearchForNodes nodetypesearcher = new SearchForNodes(jevis, m_project.getStringValue(),
        				m_location.getStringValue(),m_nodeType.getStringValue(),m_devicetype.getStringValue(),
        				m_component.getStringValue(), m_enableProject.getBooleanValue(),
        				m_enableLocation.getBooleanValue(), m_enableNodeType.getBooleanValue(),
        				m_enableDevice.getBooleanValue(), m_enableComponent.getBooleanValue(),
        				structureResult);
    			buf = nodetypesearcher.searchforInformation(structuresearcher.searchForStructure(), buf);
    		*/
    			
    		}
    		
    		if(m_enableNodeType.getBooleanValue()){
    			buf = exec.createDataContainer(createOuputTableSpecforStructure());
    			DataTableSpec spec = createOuputTableSpecforStructure();
    			/*SearchForNodes nodetypesearcher = new SearchForNodes(jevis, m_project.getStringValue(),
        				m_location.getStringValue(),m_nodeType.getStringValue(),m_devicetype.getStringValue(),
        				m_component.getStringValue(), m_enableProject.getBooleanValue(),
        				m_enableLocation.getBooleanValue(), m_enableNodeType.getBooleanValue(),
        				m_enableDevice.getBooleanValue(), m_enableComponent.getBooleanValue(),
        				nodeTypeResult);
    			buf = nodetypesearcher.fillTableWithNodetypeSearchResult(buf);
    			*/
    			SearchForAttributes searcher = new SearchForAttributes(jevis, m_enableProject.getBooleanValue(),
    					DataBaseConfiguration.projectLevelName, m_enableLocation.getBooleanValue(),
    					DataBaseConfiguration.locationLevelName, m_enableComponent.getBooleanValue(),
    					DataBaseConfiguration.componentLevelName, m_enableNodeType.getBooleanValue(), 
    					m_nodeType.getStringValue(), spec);
    			buf = searcher.searchForNodetypes(buf);
    		}		    		
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
    	jevis.connect(jevUser.getStringValue(), jevPW.getStringValue());
    	
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
 
    
    private DataTableSpec createOutputTableSpecforDatapoints(){
    	DataColumnSpec nodeIDSpec = new DataColumnSpecCreator(
    			"NodeID", LongCell.TYPE).createSpec();
    	DataColumnSpec deviceTypeSpec = new DataColumnSpecCreator(
    			DataBaseConfiguration.deviceModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec componentTypeSpec = new DataColumnSpecCreator(
    			DataBaseConfiguration.projectModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec locationSpec = new DataColumnSpecCreator(
    			DataBaseConfiguration.locationModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec projectSpec = new DataColumnSpecCreator(
    			DataBaseConfiguration.componentModelName, StringCell.TYPE).createSpec();
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
    
    private DataTableSpec createOuputTableSpecforStructure(){
    	
    	DataColumnSpec nodeIDSpec = new DataColumnSpecCreator("NodeID", LongCell.TYPE).createSpec();
    	DataColumnSpec nameSpec = new DataColumnSpecCreator("Name", StringCell.TYPE).createSpec();
    	DataColumnSpec projectTypeSpec = new DataColumnSpecCreator(
    			DataBaseConfiguration.projectModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec locationSpec = new DataColumnSpecCreator(
    			DataBaseConfiguration.locationModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec componentSpec = new DataColumnSpecCreator(
    			DataBaseConfiguration.componentModelName, StringCell.TYPE).createSpec();

    	DataTableSpec outputTableSpec = new DataTableSpec(nodeIDSpec, nameSpec, projectTypeSpec,
    			locationSpec, componentSpec);
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

