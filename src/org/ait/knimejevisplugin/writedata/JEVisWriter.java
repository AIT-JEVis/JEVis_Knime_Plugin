package org.ait.knimejevisplugin.writedata;


import java.util.ArrayList;
import java.util.List;

import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.api.sql.JEVisDataSourceSQL;

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
	protected long createNewDatapointUnderParent(long parentID, String name, String className)
			throws JEVisException{
		
		JEVisObject obj = jevis.getObject(parentID).buildObject(
				name, jevis.getJEVisClass(className));
		obj.commit();
		return obj.getID();
	}
	
	/*
	 * Adding Samples to the given JEVisObject. 
	 */
	protected void addData(JEVisObject obj, DateTime date, Double value, 
			String unit, List<JEVisSample> samples) throws JEVisException{
		
		JEVisSample sample = obj.getAttribute("Value").buildSample(date, value, unit);
		samples.add(sample);
		if(obj.getAttribute("Value").hasSample()){
			JevisWriteDataNodeModel.logger.error("Sample");
		}

	}
	
	/*
	 * Clearing all Samples from given DataPoint Node ID. 
	 */
    protected void clearDataPointData(long id) throws JEVisException{
    	JEVisObject object = jevis.getObject(id);
    	object.getAttribute("Value").deleteAllSample();
    	if(object.getAttribute("Value").hasSample()){
    		JevisWriteDataNodeModel.logger.error("Samples still exist");
    	}
    	object.commit();
    	if(object.getAttribute("Value").hasSample()){
    		JevisWriteDataNodeModel.logger.error("Samples still exist");
    	}
    }
     
}
