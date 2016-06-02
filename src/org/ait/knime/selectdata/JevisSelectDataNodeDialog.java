package org.ait.knime.selectdata;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "JevisSelectData" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Monschiebl
 */
public class JevisSelectDataNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the JevisSelectData node.
     */
    protected JevisSelectDataNodeDialog() {
    	createNewTab("Configure Connection");
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.host, ""), "Hostaddress"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.port, ""), "Port"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.sqlSchema, ""), "SqlSchema"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.sqlUser, ""), "SqlUser"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.sqlPW, ""), "SqlPassword"));
    }
}

