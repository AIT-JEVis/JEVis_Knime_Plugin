package org.ait.knimejevisplugin.writedata;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelLong;

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
	
	private final SettingsModelLong m_objID = new SettingsModelLong(
			JevisWriteDataNodeModel.objID, 0);	
	private final SettingsModelBoolean m_update = new SettingsModelBoolean(
			JevisWriteDataNodeModel.updateDataPoint, false);
	private final SettingsModelBoolean m_newDataPoint = new SettingsModelBoolean(
			JevisWriteDataNodeModel.newDataPoint, false);
	private final SettingsModelLong m_parentID = new SettingsModelLong(
			JevisWriteDataNodeModel.parentID, 0);
    /**
     * New pane for configuring JevisWriteData node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected JevisWriteDataNodeDialog() {
        super();
        createNewGroup(label_update);
        setHorizontalPlacement(true);
        addDialogComponent(new DialogComponentBoolean(m_update, label_update));
        addDialogComponent(new DialogComponentNumber(m_objID, label_objID, 1));
        m_update.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				if(m_update.getBooleanValue()){
					m_update.setBooleanValue(!m_newDataPoint.getBooleanValue());
					m_objID.setEnabled(m_update.getBooleanValue());
				}

			}
		});
        

       
        createNewGroup(label_newDataPoint);
        addDialogComponent(new DialogComponentBoolean(m_newDataPoint,label_newDataPoint));
        m_newDataPoint.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				if(m_newDataPoint.getBooleanValue()){
					m_newDataPoint.setBooleanValue(!m_update.getBooleanValue());
				}
				
			}
		});
    }
}

