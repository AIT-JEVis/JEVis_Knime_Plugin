package org.ait.knime.selectdata;

import org.apache.commons.net.nntp.NewGroupsOrNewsQuery;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import javafx.scene.Group;

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
    	createNewTabAt("Configure Connection",0);
    	createNewGroup("Database Connection Settings");
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.host, "jevis3.ait.ac.at"), "Hostaddress"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.port, "3306"), "Port"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.sqlSchema, "jevis"), "SqlSchema"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.sqlUser, "jevis"), "SqlUser"));
    	addDialogComponent(new DialogComponentString(new SettingsModelString(
        		JevisSelectDataNodeModel.sqlPW, "vu5eS1ma"), "SqlPassword"));
    }
}

