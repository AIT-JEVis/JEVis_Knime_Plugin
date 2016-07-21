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

	static String label_objID= "JevisNodeID";
	static String label_update = "Update Datapoint";
	
	static String label_parentID = "Parent NodeID";
	static String label_newDataPointName = "New Datapoint Name";
	static String label_newDataPoint = "New Datapoint";
	static String label_newDataPointClass = "Select datapoint class:";
	
	static String label_delete = "Delete DataPoint data";
		
	private final SettingsModelLong m_objID = new SettingsModelLong(
			JevisWriteDataNodeModel.objID, 0);	
	private final SettingsModelBoolean m_update = new SettingsModelBoolean(
			JevisWriteDataNodeModel.updateDataPoint, false);
	private final SettingsModelBoolean m_newDataPoint = new SettingsModelBoolean(
			JevisWriteDataNodeModel.newDataPoint, false);

	private final SettingsModelString m_objectName = new SettingsModelString(
			JevisWriteDataNodeModel.objectName," ");
	private final SettingsModelString m_newDataPointClass = new SettingsModelString(
			JevisWriteDataNodeModel.newDataPointClass, "Data");

	private final SettingsModelBoolean m_deleteDataPoint =
			new SettingsModelBoolean(JevisWriteDataNodeModel.deleteDataPoint, false);
	
    protected JevisWriteDataNodeDialog() {
        super();
        createNewGroup("Select option:");
        setHorizontalPlacement(true);
        addDialogComponent(new DialogComponentBoolean(m_update, label_update));
        addDialogComponent(new DialogComponentBoolean(m_newDataPoint,label_newDataPoint));
        addDialogComponent(new DialogComponentBoolean(m_deleteDataPoint, label_delete));
        closeCurrentGroup();
        createNewGroup("Put in Information:");
        addDialogComponent(new DialogComponentNumber(m_objID, label_objID, 0));
        addDialogComponent(new DialogComponentString(m_objectName, label_newDataPointName));
        addDialogComponent(new DialogComponentString(
        		m_newDataPointClass,label_newDataPointClass));
        m_update.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if(m_update.getBooleanValue()){
					m_newDataPoint.setBooleanValue(!m_update.getBooleanValue());
					m_deleteDataPoint.setBooleanValue(!m_update.getBooleanValue());
					m_objectName.setEnabled(false);
					m_newDataPointClass.setEnabled(false);
					
				}
			}
		});

        
        m_newDataPoint.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
		
				if(m_newDataPoint.getBooleanValue()){
					m_update.setBooleanValue(!m_newDataPoint.getBooleanValue());
					m_deleteDataPoint.setBooleanValue(!m_newDataPoint.getBooleanValue());
					m_objectName.setEnabled(true);
					m_newDataPointClass.setEnabled(true);
				}
			}
		});
        
       
        m_deleteDataPoint.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				if(m_deleteDataPoint.getBooleanValue()){
					m_update.setBooleanValue(!m_deleteDataPoint.getBooleanValue());
					m_newDataPoint.setBooleanValue(!m_deleteDataPoint.getBooleanValue());
					m_objectName.setEnabled(false);
					m_newDataPointClass.setEnabled(false);
				}
			}
		});
        
    }
}

