package org.ait.knimejevisplugin.writedata;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.List;

import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.api.sql.JEVisObjectSQL;
import org.jevis.api.sql.ObjectTable;
import org.joda.time.DateTime;

public class JEVisWriter {
	
	private JEVisDataSourceSQL jevis;
	private ObjectTable _ot;
	
	
	public JEVisWriter(){
		super();
	}
	
	
	public JEVisWriter(JEVisDataSourceSQL jevis) {
		super();
		this.jevis = jevis;
	}


	protected void createNewDatapointUnderParent(long parentID, String name) throws JEVisException{
		
		
		JEVisObject obj = jevis.getObject(parentID).buildObject(name, jevis.getJEVisClass("Data"));
		obj.commit();
/*		
		JEVisObjectSQL obj = new JEVisObjectSQL(jevis, args);
		List<JEVisObject> objs = getObjectTable().getObjects(jevis.getJEVisClasses());
		objs.add(obj);
		getObjectTable().insertObject(obj.getName(), obj.getJEVisClass(), jevis.getObject(parentID), 0);
	*/	
	}
	
	protected void addData(JEVisObject obj, DateTime date, Double value, String unit ) throws JEVisException{
		obj.getAttribute("Value").buildSample(date, value, unit);
	}
	
	
    protected ObjectTable getObjectTable() throws JEVisException {
        if (_ot == null) {
            _ot = new ObjectTable(jevis);
        }
        return _ot;
    }
    
    
    
    protected void clearDataPointData(long id) throws JEVisException{
    	JEVisObject object = jevis.getObject(id);
    	object.getAttribute("Value").deleteAllSample();
    }
    
    
}
