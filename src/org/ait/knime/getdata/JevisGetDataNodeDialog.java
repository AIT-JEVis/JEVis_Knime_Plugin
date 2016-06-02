package org.ait.knime.getdata;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.port.PortObjectSpec;

/**
 * <code>NodeDialog</code> for the "JevisGetData" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Monschiebl
 */
public class JevisGetDataNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the JevisGetData node.
     */
    protected JevisGetDataNodeDialog() {
    	super();
    	
    	addDialogComponent(new DialogComponentNumber(
    			new SettingsModelInteger(
    					JevisGetDataNodeModel.CFGKEY_nodeID, JevisGetDataNodeModel.nodeID),
    			"NodeID",
    			0));
			
		
    }
}

