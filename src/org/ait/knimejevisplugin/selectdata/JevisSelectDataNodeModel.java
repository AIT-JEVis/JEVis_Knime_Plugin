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
   	
   	public static SearchConfiguration configuration = new SearchConfiguration();
   	
   	private JEVisDataSourceSQL jevis;
   	//Processing variables
	private BufferedDataContainer buf;
	
	//Settingsmodells
    private final SettingsModelString jhost = new SettingsModelString(
    		JevisSelectDataNodeModel.configuration.hostModelName,
    		JevisSelectDataNodeModel.configuration.DEFAULT_Host);
    private final SettingsModelString jport = new SettingsModelString(
    		JevisSelectDataNodeModel.configuration.portModelName,
    		JevisSelectDataNodeModel.configuration.DEFAULT_port);
   	private final SettingsModelString jSchema = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.sqlSchemaModelName,
   			JevisSelectDataNodeModel.configuration.DEFAULT_sqlShema);
   	private final SettingsModelString jUser = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.sqlUserModelName,
   			JevisSelectDataNodeModel.configuration.DEFAULT_sqlUserName);
   	private final SettingsModelString jPW = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.sqlPWModelName, 
   			JevisSelectDataNodeModel.configuration.DEFAULT_sqlPW);
   	
   	private final SettingsModelString jevUser = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.jevisUserModelName, 
   			JevisSelectDataNodeModel.configuration.DEFAULT_jevisUserName);
   	private final SettingsModelString jevPW = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.jevisPWModelName,
   			JevisSelectDataNodeModel.configuration.DEFAULT_jevisPW);
   	
   	private final SettingsModelBoolean m_enableNodeSearch = new SettingsModelBoolean(
   			JevisSelectDataNodeModel.enableNodeSearch, true);
   	
   	private final SettingsModelString m_project = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.projectModelName," ");
   	private final SettingsModelString m_location = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.locationModelName," ");
   	private final SettingsModelString m_nodeType = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.nodeType," ");
   	private final SettingsModelString m_devicetype = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.deviceModelName," ");
   	private final SettingsModelString m_component = new SettingsModelString(
   			JevisSelectDataNodeModel.configuration.componentModelName," ");
   	
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
    @SuppressWarnings({ "static-access", "deprecation" })
	@Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	logger.setLevel(NodeLogger.LEVEL.INFO);
    	
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
    			SearchForNodes nodetypesearcher = new SearchForNodes(jevis, m_project.getStringValue(),
        				m_location.getStringValue(),m_nodeType.getStringValue(),m_devicetype.getStringValue(),
        				m_component.getStringValue(), m_enableProject.getBooleanValue(),
        				m_enableLocation.getBooleanValue(), m_enableNodeType.getBooleanValue(),
        				m_enableDevice.getBooleanValue(), m_enableComponent.getBooleanValue(),
        				structureResult);
    			buf = nodetypesearcher.searchforInformation(structuresearcher.searchForStructure(), buf);
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
    					configuration.projectLevelName, m_enableLocation.getBooleanValue(),
    					configuration.locationLevelName, m_enableComponent.getBooleanValue(),
    					configuration.componentLevelName, m_enableNodeType.getBooleanValue(), 
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
    			configuration.deviceModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec componentTypeSpec = new DataColumnSpecCreator(
    			configuration.projectModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec locationSpec = new DataColumnSpecCreator(
    			configuration.locationModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec projectSpec = new DataColumnSpecCreator(
    			configuration.componentModelName, StringCell.TYPE).createSpec();
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
    			configuration.projectModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec locationSpec = new DataColumnSpecCreator(
    			configuration.locationModelName, StringCell.TYPE).createSpec();
    	DataColumnSpec componentSpec = new DataColumnSpecCreator(
    			configuration.componentModelName, StringCell.TYPE).createSpec();

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
    	
    	m_nodeId.saveSettingsTo(settings);
    	m_parents.saveSettingsTo(settings);
    	m_children.saveSettingsTo(settings);
    	m_siblings.saveSettingsTo(settings);
    	m_allChildren.saveSettingsTo(settings);
    	
    	m_enableNodeSearch.saveSettingsTo(settings);
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
    	
    	m_nodeId.loadSettingsFrom(settings);
    	m_parents.loadSettingsFrom(settings);
    	m_children.loadSettingsFrom(settings);
    	m_siblings.loadSettingsFrom(settings);
    	m_allChildren.loadSettingsFrom(settings);
    	
    	m_enableNodeSearch.loadSettingsFrom(settings);
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
    	
    	m_nodeId.validateSettings(settings);
    	m_parents.validateSettings(settings);
    	m_children.validateSettings(settings);
    	m_siblings.validateSettings(settings);
    	m_allChildren.validateSettings(settings);

    	m_enableNodeSearch.validateSettings(settings);
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

