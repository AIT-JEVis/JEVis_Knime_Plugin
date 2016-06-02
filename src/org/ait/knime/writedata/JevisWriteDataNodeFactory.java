package org.ait.knime.writedata;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "JevisWriteData" Node.
 * 
 *
 * @author Monschiebl
 */
public class JevisWriteDataNodeFactory 
        extends NodeFactory<JevisWriteDataNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JevisWriteDataNodeModel createNodeModel() {
        return new JevisWriteDataNodeModel();
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
    public NodeView<JevisWriteDataNodeModel> createNodeView(final int viewIndex,
            final JevisWriteDataNodeModel nodeModel) {
        return new JevisWriteDataNodeView(nodeModel);
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
        return new JevisWriteDataNodeDialog();
    }

}

