package org.ait.knimejevisplugin.writedata;


import java.util.List;

import org.ait.knimejevisplugin.DataBaseConfiguration;
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
		
		JEVisSample sample = obj.getAttribute(DataBaseConfiguration.valueAttributeName).buildSample(date, value, unit);
		samples.add(sample);
		
	}
	
	/*
	 * Clearing all Samples from given DataPoint Node ID. 
	 */
    protected void clearDataPointData(long id) throws JEVisException{
    	JEVisObject object = jevis.getObject(id);
    	object.getAttribute(DataBaseConfiguration.valueAttributeName).deleteAllSample();
    	if(object.getAttribute(DataBaseConfiguration.valueAttributeName).hasSample()){
    		JevisWriteDataNodeModel.logger.error("Samples still exist at Node: "+ object.getID());
    	}
    	object.commit();
    	if(object.getAttribute("Value").hasSample()){
    		JevisWriteDataNodeModel.logger.error("Samples still exist");
    	}
    }
     
}
