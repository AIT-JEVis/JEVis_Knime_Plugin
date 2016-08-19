package org.ait.knimejevisplugin.selectdata;

import java.util.ArrayList;

import org.ait.knimejevisplugin.DataBaseConfiguration;
import org.jevis.api.JEVisException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "JevisSelectData" Node.
 * 
 *
 * @author Monschiebl
 */
public class JevisSelectDataNodeFactory 
        extends NodeFactory<JevisSelectDataNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JevisSelectDataNodeModel createNodeModel() {
    	DataBaseConfiguration configuration = new DataBaseConfiguration();
        return new JevisSelectDataNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<JevisSelectDataNodeModel> createNodeView(final int viewIndex,
            final JevisSelectDataNodeModel nodeModel) {
        return new JevisSelectDataNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        try {
        	
        	JevisSelectDataNodeDialog dialog = new JevisSelectDataNodeDialog();
        	//dialog.initializeData();
			return dialog;
		} catch (JEVisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

}

