package org.ait.knimejevisplugin.selectdata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ait.knimejevisplugin.getdata.JevisGetDataNodeModel;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.knime.core.data.DataTableSpec;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;

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

    private static final NodeLogger logger = NodeLogger
            .getLogger("SelectDataLogger");
    
    
  //Jevis Connection information
  	public static String host = "Hostaddress";
   	public static String port = "Port";
   	public static String sqlSchema = "Schema";
   	public static String sqlUser = "User";
   	public static String sqlPW = "Password";
   	
   	public static String jevisUser = "BerhnardM";
   	public static String jevisPW = "testpass01593";
 
   	public static String m_NodeID = "NodeID";
   	public static int DEFAULT_NODEID = 483;
   	
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
   			m_NodeID, DEFAULT_NODEID);
   	
    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	logger.setLevel(NodeLogger.LEVEL.INFO);
    	
    	connectingtojevis();
    	DataTableSpec spec = new DataTableSpec();
    	buf = exec.createDataContainer(spec);
    	
    	if(jevis.isConnectionAlive()){
    		logger.info("Connection Alive!");
    		JEVisObject jObject = jevis.getObject((long) m_nodeID.getIntValue()) ;
    		//logger.info(jObject.getJEVisClass().getType("Value").getPrimitiveType());
    		//logger.info(jObject.getAttribute("Value").getType().toString());
    		List<JEVisObject> list_Children = jObject.getChildren();
    		for(JEVisObject child : list_Children){
    			logger.info(child.getName());
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
    	jhost.saveSettingsTo(settings);
    	jport.saveSettingsTo(settings);
    	jSchema.saveSettingsTo(settings);
    	jUser.saveSettingsTo(settings);
    	jPW.saveSettingsTo(settings);
    	jevUser.saveSettingsTo(settings);
    	jevPW.saveSettingsTo(settings);
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

