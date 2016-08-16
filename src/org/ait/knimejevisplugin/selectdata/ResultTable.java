package org.ait.knimejevisplugin.selectdata;

import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.ait.knimejevisplugin.DataBaseConfiguration;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;

public class ResultTable {
	
	DataCell[] cells;
	int counter;
	boolean structure;
	
	public ResultTable(boolean structure){
		this.structure = structure;
	}
	
	protected DataTableSpec createOutputTableSpecforDatapoints(){
		DataColumnSpec nodeIDSpec = new DataColumnSpecCreator(
				"NodeID", IntCell.TYPE).createSpec();
		DataColumnSpec deviceTypeSpec = new DataColumnSpecCreator(
				DataBaseConfiguration.deviceModelName, StringCell.TYPE).createSpec();
		
		DataColumnSpec projectSpec = null;
		if(structure){
			 projectSpec = new DataColumnSpecCreator(
					"Type",StringCell.TYPE).createSpec();
		}
		else{
			 projectSpec = new DataColumnSpecCreator(
					DataBaseConfiguration.projectModelName, StringCell.TYPE).createSpec();
		}
		
		DataColumnSpec locationSpec = new DataColumnSpecCreator(
				DataBaseConfiguration.locationModelName, StringCell.TYPE).createSpec();
		
		DataColumnSpec componentSpec = new DataColumnSpecCreator(
				DataBaseConfiguration.componentModelName, StringCell.TYPE).createSpec();
		DataColumnSpec commentSpec = new DataColumnSpecCreator(
				"Comment", StringCell.TYPE).createSpec();
		DataColumnSpec firstTsSpec = new DataColumnSpecCreator(
				"First Timestamp", DateAndTimeCell.TYPE).createSpec();
		DataColumnSpec lastTSSpec = new DataColumnSpecCreator(
				"Last Timestamp", DateAndTimeCell.TYPE).createSpec();
	    	
		DataTableSpec outputTableSpec = new DataTableSpec(nodeIDSpec, deviceTypeSpec, projectSpec,
				locationSpec, componentSpec, commentSpec, firstTsSpec, lastTSSpec);
	    	
		return outputTableSpec;
	    }
	
	protected BufferedDataContainer fillResultTable(BufferedDataContainer buf, DataTableSpec spec, List<JEVisObject> list_projects,
			List<JEVisObject> list_location, List<JEVisObject> list_component, List<JEVisObject> list_datapoint, List<String> list_Comment) throws JEVisException{
		
		counter = 0;
		cells = new DataCell[spec.getNumColumns()];

		for(int i= 0; i< list_datapoint.size();i++ ){
			cells[0] = new IntCell(list_datapoint.get(i).getID().intValue());
	        cells[1] = new StringCell(list_datapoint.get(i).getName());
	        try{
	        	cells[2] = new StringCell(list_projects.get(i).getName());
	        }catch(Exception e){
	        	handleEmptyObjectListException(list_projects, 2, "String");
	        }
	        try{
				cells[3] = new StringCell(list_location.get(i).getName());
	        }catch(Exception e){
	        	handleEmptyObjectListException(list_location, 3, "String");
	        }
	        try{
	        	cells[4] = new StringCell(list_component.get(i).getName());
	        }catch(Exception e){
	        	handleEmptyObjectListException(list_component, 4, "String");
	        }
	        try{
	        	cells[5] = new StringCell(list_Comment.get(i));
	        }catch(Exception e){
	        	handleEmptyStringListException(list_Comment, 5);
	        }
	        try{
				fillTableWithDateTime(list_datapoint.get(i));
	        }catch(Exception e){
	        	if(list_datapoint.get(i).getAttribute(
	        			DataBaseConfiguration.valueAttributeName) != null && 
	        			list_datapoint.get(i).getAttribute(
	        					DataBaseConfiguration.valueAttributeName).hasSample()){	        		
	        		fillTableWithDateTime(list_datapoint.get(i));
	        	}
	        	else{
		        	cells[6] = new DateAndTimeCell(0,0,0);
		        	cells[7] = new DateAndTimeCell(0,0,0);
	        	}
	        }
	        fillTable(buf);
		}
		return buf;
	}
	
	private void fillTableWithDateTime(JEVisObject child) throws JEVisException{
        if(child.getAttribute(DataBaseConfiguration.valueAttributeName)!= null && 
        		child.getAttribute(DataBaseConfiguration.valueAttributeName).hasSample())
			{
        	            
        		GregorianCalendar cal =  child.getAttribute(
         				DataBaseConfiguration.valueAttributeName)
        					.getTimestampFromFirstSample().toGregorianCalendar();
                GregorianCalendar calLas =  child.getAttribute(
         				DataBaseConfiguration.valueAttributeName)
        					.getTimestampFromLastSample().toGregorianCalendar();
        		cells[6] = new DateAndTimeCell(
		 	            		cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
		 	            		cal.get(Calendar.DAY_OF_MONTH));
         		cells[7] = new DateAndTimeCell(
         				calLas.get(Calendar.YEAR), calLas.get(Calendar.MONTH),
 	            		calLas.get(Calendar.DAY_OF_MONTH));

         	}
         	else{
	            cells[6] = new DateAndTimeCell(0, 0, 0);
	            cells[7] = new DateAndTimeCell(0, 0, 0);
         	}
	}
	
	private BufferedDataContainer fillTable(BufferedDataContainer buf){
		counter++;
		for( int i = 0; i < cells.length; i++ ){
			if(cells[i]== null){
				cells[i] = new StringCell(" ");
			}
		}
		DataRow row = new DefaultRow("Row"+counter, cells);
		buf.addRowToTable(row);
        return buf;
	}
	
	private void handleEmptyObjectListException(List<JEVisObject> list, int column, String type){
		if(list.isEmpty()&& type == "String"){
			cells[column] = new StringCell(" ");
		}
	}
	private void handleEmptyStringListException(List<String> list, int column){
		if(list.isEmpty()){
			cells[column] = new StringCell(" ");
		}
	}
}
