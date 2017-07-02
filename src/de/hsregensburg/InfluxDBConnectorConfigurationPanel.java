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

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;

@SuppressWarnings("serial")
public class InfluxDBConnectorConfigurationPanel extends JPanel {
	private JTextField dbURI;
	private JTextField dbTableName;
	private JTextField dbUserName;
	private JTextField dbPassword;
	private JTextArea  query;

	/**
	 * Create the panel.
	 */
	public InfluxDBConnectorConfigurationPanel() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblDatabaseUri = new JLabel("Database URI");
		springLayout.putConstraint(SpringLayout.NORTH, lblDatabaseUri, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblDatabaseUri, 10, SpringLayout.WEST, this);
		add(lblDatabaseUri);
		
		dbURI = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, dbURI, -2, SpringLayout.NORTH, lblDatabaseUri);
		springLayout.putConstraint(SpringLayout.WEST, dbURI, 6, SpringLayout.EAST, lblDatabaseUri);
		springLayout.putConstraint(SpringLayout.EAST, dbURI, -10, SpringLayout.EAST, this);
		add(dbURI);
		dbURI.setColumns(10);
		
		JLabel lblTableName = new JLabel("Table Name");
		springLayout.putConstraint(SpringLayout.NORTH, lblTableName, 6, SpringLayout.SOUTH, lblDatabaseUri);
		springLayout.putConstraint(SpringLayout.EAST, lblTableName, 0, SpringLayout.EAST, lblDatabaseUri);
		add(lblTableName);
		
		dbTableName = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, dbTableName, -2, SpringLayout.NORTH, lblTableName);
		springLayout.putConstraint(SpringLayout.WEST, dbTableName, 0, SpringLayout.WEST, dbURI);
		springLayout.putConstraint(SpringLayout.EAST, dbTableName, 0, SpringLayout.EAST, dbURI);
		add(dbTableName);
		dbTableName.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username");
		springLayout.putConstraint(SpringLayout.NORTH, lblUsername, 6, SpringLayout.SOUTH, lblTableName);
		springLayout.putConstraint(SpringLayout.EAST, lblUsername, 0, SpringLayout.EAST, lblDatabaseUri);
		add(lblUsername);
		
		dbUserName = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, dbUserName, 2, SpringLayout.SOUTH, dbTableName);
		springLayout.putConstraint(SpringLayout.WEST, dbUserName, 6, SpringLayout.EAST, lblUsername);
		springLayout.putConstraint(SpringLayout.EAST, dbUserName, 0, SpringLayout.EAST, dbURI);
		add(dbUserName);
		dbUserName.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		springLayout.putConstraint(SpringLayout.NORTH, lblPassword, 6, SpringLayout.SOUTH, lblUsername);
		springLayout.putConstraint(SpringLayout.EAST, lblPassword, 0, SpringLayout.EAST, lblDatabaseUri);
		add(lblPassword);
		
		dbPassword = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, dbPassword, 2, SpringLayout.SOUTH, dbUserName);
		springLayout.putConstraint(SpringLayout.WEST, dbPassword, 0, SpringLayout.WEST, dbURI);
		springLayout.putConstraint(SpringLayout.EAST, dbPassword, 0, SpringLayout.EAST, dbURI);
		add(dbPassword);
		dbPassword.setColumns(10);
		
		JLabel lblQuery = new JLabel("Query");
		springLayout.putConstraint(SpringLayout.NORTH, lblQuery, 6, SpringLayout.SOUTH, lblPassword);
		springLayout.putConstraint(SpringLayout.EAST, lblQuery, 0, SpringLayout.EAST, lblDatabaseUri);
		add(lblQuery);
		
		query = new JTextArea();
		springLayout.putConstraint(SpringLayout.NORTH, query, 6, SpringLayout.SOUTH, dbPassword);
		springLayout.putConstraint(SpringLayout.WEST, query, 6, SpringLayout.EAST, lblQuery);
		springLayout.putConstraint(SpringLayout.SOUTH, query, 10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, query, 0, SpringLayout.EAST, dbURI);
		add(query);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{dbURI, dbTableName, dbUserName, dbPassword, query}));

	}
	
	public String getDatabaseURI() {
		return dbURI.getText();
	}
	
	public void setDatabaseURI(String uri) {
		if (uri.isEmpty() || uri.equals("")) {
			dbURI.setText("http://");
		} else {
			dbURI.setText(uri);			
		}
	}
	
	public String getDatabaseTable() {
		return dbTableName.getText();
	}
	
	public void setDatabaseTable(String tablename) {
		dbTableName.setText(tablename);
	}
	
	public String getDatabaseUsername() {
		return dbUserName.getText();
	}
	
	public void setDatabaseUsername(String name) {
		dbUserName.setText(name);
	}
	
	public String getDatabasePassword() {
		return dbPassword.getText();
	}
	
	public void setDatabasePassword(String pw) {
		dbPassword.setText(pw);
	}
	
	public String getQuery() {
		return query.getText();
	}
	
	public void setQuery(String qry) {
		query.setText(qry);
	}
}
