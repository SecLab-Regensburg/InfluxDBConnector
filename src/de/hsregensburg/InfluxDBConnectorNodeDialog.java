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

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "InfluxDBConnector" Node.
 * An InfluxDB Connector
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author "Günther Wutz"
 */
public class InfluxDBConnectorNodeDialog extends NodeDialogPane {

	private InfluxDBConnectorConfigurationPanel configurationPanel;
    /**
     * New pane for configuring InfluxDBConnector node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected InfluxDBConnectorNodeDialog() {
        super();
        this.addTab("InfluxDB Configuration", getInfluxDBConfigurationComponent());
    }

	private Component getInfluxDBConfigurationComponent() {
		configurationPanel = new InfluxDBConnectorConfigurationPanel();
		return configurationPanel;
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		settings.addString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_URI, configurationPanel.getDatabaseURI());
		settings.addString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_NAME, configurationPanel.getDatabaseTable());
		settings.addString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_USERNAME, configurationPanel.getDatabaseUsername());
		settings.addString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_PASSWORD, configurationPanel.getDatabasePassword());
		settings.addString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_QUERY, configurationPanel.getQuery());
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException {
		try {
			configurationPanel.setDatabaseURI(settings.getString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_URI));
			configurationPanel.setDatabaseTable(settings.getString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_NAME));
			configurationPanel.setDatabaseUsername(settings.getString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_USERNAME));
			configurationPanel.setDatabasePassword(settings.getString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_PASSWORD));
			configurationPanel.setQuery(settings.getString(InfluxDBConnectorNodeModel.CFGKEY_DATABASE_QUERY));
		} catch (InvalidSettingsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

