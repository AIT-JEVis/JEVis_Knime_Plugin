package org.ait.knimejevisplugin.writedata;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.List;

import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.api.JEVisUnit;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.api.sql.JEVisObjectSQL;
import org.jevis.api.sql.ObjectTable;
import org.joda.time.DateTime;

public class JEVisWriter {
	
	private JEVisDataSourceSQL jevis;
	
	//Constructors
	public JEVisWriter(){
		super();
	}
		
	public JEVisWriter(JEVisDataSourceSQL jevis) {
		super();
		this.jevis = jevis;
	}

	//Methods for Writing back into JEVis Database
	/*
	 * New Datapoint is inserted under the given Parent Node ID.
	 */
	protected long createNewDatapointUnderParent(long parentID, String name)
			throws JEVisException{
		
		JEVisObject obj = jevis.getObject(parentID).buildObject(
				name, jevis.getJEVisClass("Data"));
		obj.commit();
		return obj.getID();
	}
	
	/*
	 * adding Samples to the given JEVisObject. 
	 */
	protected void addData(JEVisObject obj, DateTime date, Double value, String unit )
			throws JEVisException{
		obj.getAttribute("Value").buildSample(date, value, unit);
		obj.commit();
	}
	
	/*
	 * Clearing all Samples from given DataPoint Node ID. 
	 */
    protected void clearDataPointData(long id) throws JEVisException{
    	JEVisObject object = jevis.getObject(id);
    	object.getAttribute("Value").deleteAllSample();
    }
    
    
}
