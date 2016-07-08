package org.ait.knimejevisplugin.selectdata;

import org.ait.knimejevisplugin.DataBaseConfiguration;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;

public class ResultTable {
		
	
	
	
	  protected DataTableSpec createOutputTableSpecforDatapoints(){
		  DataColumnSpec nodeIDSpec = new DataColumnSpecCreator(
				  "NodeID", LongCell.TYPE).createSpec();
		  DataColumnSpec deviceTypeSpec = new DataColumnSpecCreator(
				  DataBaseConfiguration.deviceModelName, StringCell.TYPE).createSpec();
		  DataColumnSpec componentTypeSpec = new DataColumnSpecCreator(
				  DataBaseConfiguration.projectModelName, StringCell.TYPE).createSpec();
		  DataColumnSpec locationSpec = new DataColumnSpecCreator(
				  DataBaseConfiguration.locationModelName, StringCell.TYPE).createSpec();
		  DataColumnSpec projectSpec = new DataColumnSpecCreator(
				  DataBaseConfiguration.componentModelName, StringCell.TYPE).createSpec();
		  DataColumnSpec floorSpec = new DataColumnSpecCreator(
				  "Floor", StringCell.TYPE).createSpec();
		  DataColumnSpec firstTsSpec = new DataColumnSpecCreator(
				  "First Timestamp", DateAndTimeCell.TYPE).createSpec();
		  DataColumnSpec lastTSSpec = new DataColumnSpecCreator(
				  "Last Timestamp", DateAndTimeCell.TYPE).createSpec();
	    	
		  DataTableSpec outputTableSpec = new DataTableSpec(nodeIDSpec, deviceTypeSpec, componentTypeSpec,
				  locationSpec, projectSpec, floorSpec, firstTsSpec, lastTSSpec);
	    	
	    	return outputTableSpec;
	    }
	
}
