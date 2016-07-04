package org.ait.knimejevisplugin.writedata;

import java.util.List;

import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.api.sql.ObjectTable;

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


	protected void writeObject(JEVisObject obj, long parentID) throws JEVisException{
		
		List<JEVisObject> objs = getObjectTable().getObjects(jevis.getJEVisClasses());
		objs.add(obj);
		getObjectTable().insertObject(obj.getName(), obj.getJEVisClass(), jevis.getObject(parentID), 0);
		
	}
	
	protected void updateObject(JEVisObject obj) throws JEVisException{
		getObjectTable().updateObject(obj);
	}
	
	
	
    protected ObjectTable getObjectTable() throws JEVisException {
        if (_ot == null) {
            _ot = new ObjectTable(jevis);
        }
        return _ot;
    }
}
