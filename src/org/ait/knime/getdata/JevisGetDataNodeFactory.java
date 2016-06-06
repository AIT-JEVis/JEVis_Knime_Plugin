package org.ait.knime.getdata;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "JevisGetData" Node.
 * 
 *
 * @author Monschiebl
 */
public class JevisGetDataNodeFactory 
        extends NodeFactory<JevisGetDataNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JevisGetDataNodeModel createNodeModel() {
        return new JevisGetDataNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<JevisGetDataNodeModel> createNodeView(final int viewIndex,
            final JevisGetDataNodeModel nodeModel) {
        return new JevisGetDataNodeView(nodeModel);
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
        return new JevisGetDataNodeDialog();
    }

}

