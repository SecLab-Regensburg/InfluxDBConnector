/*******************************************************************************
 * Copyright (C) 2017 "Günther Wutz"
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.hsregensburg;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "InfluxDBConnector" Node.
 * An InfluxDB Connector
 *
 * @author pasiem
 */
public class InfluxDBConnectorNodeFactory 
        extends NodeFactory<InfluxDBConnectorNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public InfluxDBConnectorNodeModel createNodeModel() {
        return new InfluxDBConnectorNodeModel();
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
    public NodeView<InfluxDBConnectorNodeModel> createNodeView(final int viewIndex,
            final InfluxDBConnectorNodeModel nodeModel) {
        return null;
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
        return new InfluxDBConnectorNodeDialog();
    }

}

