package org.ait.knimejevisplugin.writedata;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "JevisWriteData" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Monschiebl
 */
public class JevisWriteDataNodeDialog extends DefaultNodeSettingsPane {

	static String label_objID= "Put in JevisNodeID";
	static String label_update = "Update Datapoint";
	
	static String label_parentID = "Put in Parent NodeID";
	static String label_newDataPoint = "New Datapoint";
	
	static String label_delete = "Delete DataPoint data";
	
	
	private final SettingsModelLong m_objID = new SettingsModelLong(
			JevisWriteDataNodeModel.objID, 0);	
	private final SettingsModelBoolean m_update = new SettingsModelBoolean(
			JevisWriteDataNodeModel.updateDataPoint, false);
	private final SettingsModelBoolean m_newDataPoint = new SettingsModelBoolean(
			JevisWriteDataNodeModel.newDataPoint, false);
	private final SettingsModelLong m_parentID = new SettingsModelLong(
			JevisWriteDataNodeModel.parentID, 0);
	private final SettingsModelString m_objectName = new SettingsModelString(
			JevisWriteDataNodeModel.objectName," ");
	
	private final SettingsModelBoolean m_deleteDataPoint =
			new SettingsModelBoolean(JevisWriteDataNodeModel.deleteDataPoint, false);
	private final SettingsModelLong m_deleteDataPointNodeID =
			new SettingsModelLong(JevisWriteDataNodeModel.deleteDataPointNodeID, 0);
	
    /**
     * New pane for configuring JevisWriteData node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected JevisWriteDataNodeDialog() {
        super();
        createNewGroup("Select option:");
        setHorizontalPlacement(true);
        addDialogComponent(new DialogComponentBoolean(m_update, label_update));
        addDialogComponent(new DialogComponentBoolean(m_newDataPoint,label_newDataPoint));
        addDialogComponent(new DialogComponentBoolean(m_deleteDataPoint, label_delete));
        closeCurrentGroup();

        addDialogComponent(new DialogComponentNumber(m_objID, label_objID, 0));
        m_update.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(m_update.getBooleanValue()){
					m_newDataPoint.setBooleanValue(!m_update.getBooleanValue());
					m_deleteDataPoint.setBooleanValue(!m_update.getBooleanValue());
					m_objID.setEnabled(m_update.getBooleanValue());
				}
			}
		});

        addDialogComponent(new DialogComponentNumber(m_parentID, label_newDataPoint, 0));
        addDialogComponent(new DialogComponentString(m_objectName, label_newDataPoint));
        m_newDataPoint.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
		
				if(m_newDataPoint.getBooleanValue()){
					m_update.setBooleanValue(!m_newDataPoint.getBooleanValue());
					m_deleteDataPoint.setBooleanValue(!m_newDataPoint.getBooleanValue());
				}
			}
		});
        
       
        addDialogComponent(new DialogComponentNumber(m_deleteDataPointNodeID, label_delete, 0));
        m_deleteDataPoint.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				if(m_deleteDataPoint.getBooleanValue()){
					m_update.setBooleanValue(!m_deleteDataPoint.getBooleanValue());
					m_newDataPoint.setBooleanValue(!m_deleteDataPoint.getBooleanValue());
					
				}
			}
		});
        
    }
}

