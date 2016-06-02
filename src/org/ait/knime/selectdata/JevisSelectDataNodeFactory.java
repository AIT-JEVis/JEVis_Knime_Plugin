package org.ait.knime.selectdata;

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
        return new JevisSelectDataNodeModel();
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
        return new JevisSelectDataNodeDialog();
    }

}

