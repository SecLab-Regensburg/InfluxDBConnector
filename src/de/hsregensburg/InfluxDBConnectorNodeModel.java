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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of InfluxDBConnector. An InfluxDB Connector
 *
 * @author pasiem
 */
public class InfluxDBConnectorNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(InfluxDBConnectorNodeModel.class);

	/**
	 * the settings key which is used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */
	static final String CFGKEY_COUNT = "Count";

	static final String CFGKEY_DATABASE_URI = "URI";
	static final String CFGKEY_DATABASE_NAME = "Databasename";
	static final String CFGKEY_DATABASE_USERNAME = "Username";
	static final String CFGKEY_DATABASE_PASSWORD = "Password";
	static final String CFGKEY_DATABASE_QUERY = "Query";

	private final SettingsModelString m_database_uri = new SettingsModelString(
			InfluxDBConnectorNodeModel.CFGKEY_DATABASE_URI, "");

	private final SettingsModelString m_database_username = new SettingsModelString(
			InfluxDBConnectorNodeModel.CFGKEY_DATABASE_USERNAME, "");

	private final SettingsModelString m_database_password = new SettingsModelString(
			InfluxDBConnectorNodeModel.CFGKEY_DATABASE_PASSWORD, "");

	private final SettingsModelString m_database_name = new SettingsModelString(
			InfluxDBConnectorNodeModel.CFGKEY_DATABASE_NAME, "");

	private final SettingsModelString m_database_query = new SettingsModelString(
			InfluxDBConnectorNodeModel.CFGKEY_DATABASE_QUERY, "");

	private Map<String, String> typeForField = new HashMap<String, String>();
	private List<String> orderedTypes = new ArrayList<String>();

	/**
	 * Constructor for the node model.
	 */
	protected InfluxDBConnectorNodeModel() {

		// TODO one incoming port and one outgoing port is assumed
		super(0, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		InfluxDB db = InfluxDBFactory.connect(m_database_uri.getStringValue(), m_database_username.getStringValue(),
				m_database_password.getStringValue());
		QueryResult typedata = db.query(new Query("SHOW FIELD KEYS", m_database_name.getStringValue()));
		QueryResult results = db.query(new Query(m_database_query.getStringValue(), m_database_name.getStringValue()));

		initTypeMap(typedata);
		initOrderedTypes(results);

		if (results == null || results.getResults().size() > 1) {
			throw new Exception("There is more then one Resultset");
		}

		Result result = results.getResults().get(0);

		if (result == null || result.getSeries() == null || result.getSeries().size() > 1) {
			throw new Exception("There is more then one Series");
		}

		Series series = result.getSeries().get(0);
		List<String> columns = series.getColumns();

		int size_columns = series.getColumns().size();
		int size_rows = series.getValues().size();
		//
		// DataColumnSpec[] allColSpecs = new DataColumnSpec[size_columns];
		//
		// List<Object> firstRow = series.getValues().get(0);
		// for (int i = 0; i < size_columns; i++) {
		// DataType type = getDataTypeForObject(firstRow.get(i));
		//
		// allColSpecs[i] = new DataColumnSpecCreator(columns.get(i),
		// type).createSpec();
		// }

		// the data table spec of the single output table,
		// the table will have three columns:
		// DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
		// allColSpecs[0] =
		// new DataColumnSpecCreator("Column 0", StringCell.TYPE).createSpec();
		// allColSpecs[1] =
		// new DataColumnSpecCreator("Column 1", DoubleCell.TYPE).createSpec();
		// allColSpecs[2] =
		// new DataColumnSpecCreator("Column 2", IntCell.TYPE).createSpec();
		DataColumnSpec[] allColSpecs = new DataColumnSpec[size_columns];
		for (int i = 0; i < size_columns; i++) {
			allColSpecs[i] = new DataColumnSpecCreator(columns.get(i), getDataTypeForString(orderedTypes.get(i))).createSpec();
		}
		
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
		// the execution context will provide us with storage capacity, in this
		// case a data container to which we will add rows sequentially
		// Note, this container can also handle arbitrary big data tables, it
		// will buffer to disc if necessary.
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		// let's add m_count rows to it

		for (int i = 0; i < size_rows; i++) {
			RowKey key = new RowKey("Row " + i);

			DataCell[] cells = new DataCell[size_columns];
			List<Object> celldata = series.getValues().get(i);
			for (int j = 0; j < celldata.size(); j++) {
				Object data = celldata.get(j);

				cells[j] = getDataCellForObject(data, orderedTypes.get(j));
			}
			DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);
		}

		// for (int i = 0; i < m_count.getIntValue(); i++) {
		// RowKey key = new RowKey("Row " + i);
		// // the cells of the current row, the types of the cells must match
		// // the column spec (see above)
		// DataCell[] cells = new DataCell[3];
		// cells[0] = new StringCell("String_" + i);
		// cells[1] = new DoubleCell(0.5 * i); Series series =
		// result.getSeries().get(0);
//		List<String> columns = series.getColumns();

		// cells[2] = new IntCell(i);
		// DataRow row = new DefaultRow(key, cells);
		// container.addRowToTable(row);
		//
		// // check if the execution monitor was canceled
		// exec.checkCanceled();
		// exec.setProgress(i / (double)m_count.getIntValue(),
		// "Adding row " + i);
		// }
		// once we are done, we close the container and return its table
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	private void initTypeMap(QueryResult query) {
		typeForField.clear();
		
		List<List<Object>> columns = query.getResults().get(0).getSeries().get(0).getValues();

		for (int i = 0; i < columns.size(); i++) {
			String field = (String) columns.get(i).get(0);
			String type = (String) columns.get(i).get(1);

			typeForField.put(field, type);
		}
	}

	private void initOrderedTypes(QueryResult query) {
		orderedTypes.clear();
		List<String> fields = query.getResults().get(0).getSeries().get(0).getColumns();
		
		for (String field : fields) {
			if (field.equals("time")) {
				orderedTypes.add("string");
			} else {
				orderedTypes.add(typeForField.get(field));
			}
		}
	}

	private DataType getDataTypeForString(String typename) {
		if (typename.equals("string")) {
			return StringCell.TYPE;
		}

		return DoubleCell.TYPE;
	}

	// private DataType getDataTypeForObject (Object obj) {
	// if (obj == null) {
	// return StringCell.TYPE;
	// } else if (obj.getClass() == String.class) {
	// return StringCell.TYPE;
	// } else if (obj.getClass() == Double.class) {
	// return DoubleCell.TYPE;
	// }
	//
	// return null;
	// }

	private DataCell getDataCellForObject(Object obj, String type) {
		
		if (type.equals("string")) {
			return new StringCell((String) obj);
		} else if (type.equals("integer") || type.equals("float")) {
			return new DoubleCell((double) obj);
		}

		return new StringCell("");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {

		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message

		return new DataTableSpec[] { null };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		// TODO save user settings to the config object.

		m_database_uri.saveSettingsTo(settings);
		m_database_name.saveSettingsTo(settings);
		m_database_username.saveSettingsTo(settings);
		m_database_password.saveSettingsTo(settings);
		m_database_query.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.

		// m_count.loadSettingsFrom(settings);
		m_database_uri.loadSettingsFrom(settings);
		m_database_name.loadSettingsFrom(settings);
		m_database_username.loadSettingsFrom(settings);
		m_database_password.loadSettingsFrom(settings);
		m_database_query.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

		// m_count.validateSettings(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO load internal data.
		// Everything handed to output ports is loaded automatically (data
		// returned by the execute method, models loaded in loadModelContent,
		// and user settings set through loadSettingsFrom - is all taken care
		// of). Load here only the other internals that need to be restored
		// (e.g. data used by the views).

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

}
