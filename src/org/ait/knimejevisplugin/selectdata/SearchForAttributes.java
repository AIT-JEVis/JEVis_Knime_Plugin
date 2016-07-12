package org.ait.knimejevisplugin.selectdata;

import java.util.ArrayList;
import java.util.List;

import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;

public class SearchForAttributes {

	int counter= 0;
	JEVisDataSourceSQL jevis;
	
	boolean enabledProject;
	String projectLevelName;
	
	boolean enabledLocation;
	String locationLevelName; 
	
	boolean enabledComponent;
	String componentLevelName;
	
	boolean enabledNodeType;
	String nodeType;
 
	DataTableSpec spec;
	
	DataCell[] cells= new DataCell[spec.getNumColumns()];;
	
	List<JEVisObject> fillInList = new ArrayList<JEVisObject>();
	
	/*
	 * Constructor
	 */
	public SearchForAttributes(JEVisDataSourceSQL jevis, boolean enabledProject, String projectLevelName,
			boolean enabledLocation, String locationLevelName, boolean enabledComponent, String componentLevelName,
			boolean enabledNodeType, String nodeType, DataTableSpec spec) {
		super();
		this.jevis = jevis;
		this.enabledProject = enabledProject;
		this.projectLevelName = projectLevelName;
		this.enabledLocation = enabledLocation;
		this.locationLevelName = locationLevelName;
		this.enabledComponent = enabledComponent;
		this.componentLevelName = componentLevelName;
		this.enabledNodeType = enabledNodeType;
		this.nodeType = nodeType;
		this.spec = spec;
	}

	/*
	 * Methods for Nodetype Search:
	 */
	public BufferedDataContainer searchForNodetypes(BufferedDataContainer buf) throws JEVisException{
		List<JEVisObject> list_nodetype = jevis.getObjects(jevis.getJEVisClass(nodeType), true);
		for(JEVisObject nodeTypeObject : list_nodetype){
			findAllImportantParents(nodeTypeObject);
		}
		buf = fillInInformation(buf);
		return buf;
	}
	
	/*
	 * UsefullMethods:
	 */
	void findAllImportantParents(JEVisObject jObject) throws JEVisException{
		
		for(JEVisObject object :  jObject.getParents()){
			if(SearchForNodes.checkLevel(object, projectLevelName)){
				fillInList.add(object);
				
			}
			else if(SearchForNodes.checkLevel(object, locationLevelName)){
				fillInList.add(object);
				findAllImportantParents(object);
			}
			else if(SearchForNodes.checkLevel(object, componentLevelName)){
				fillInList.add(object);
				findAllImportantParents(object);
			}
			else{
				findAllImportantParents(object);
			}
		}
	}
	
	BufferedDataContainer fillInInformation(BufferedDataContainer buf) throws JEVisException{
		
		for(JEVisObject object : fillInList){
			if(SearchForNodes.checkLevel(object, projectLevelName))
				buf = filterforLevel(object, buf, enabledProject, projectLevelName, 2);
			else if(SearchForNodes.checkLevel(object, locationLevelName))
				buf = filterforLevel(object, buf, enabledLocation, locationLevelName, 3);
			else if(SearchForNodes.checkLevel(object, componentLevelName))
				buf = filterforLevel(object, buf, enabledComponent, componentLevelName, 4);
			else if(object.getJEVisClass().equals(jevis.getJEVisClass(nodeType)))
				buf = fillTableWithBasicObjectInfo(object, buf);
			
		}

		return buf;
	}
	
	
	private BufferedDataContainer filterforLevel(JEVisObject jObject, BufferedDataContainer buf,
			boolean enabled, String levelname, int column) throws JEVisException{
		if(enabled){
			if(jObject.getJEVisClass().equals(jevis.getJEVisClass(levelname))){
				cells[column] = new StringCell(jObject.getName());
			}
		}else{
			cells[column] = new StringCell(jObject.getName());
		}
		return buf; 
	}
	
	private BufferedDataContainer fillTableWithBasicObjectInfo(JEVisObject child, BufferedDataContainer buf){

		cells[0] = new LongCell(child.getID());
		cells[1] = new StringCell(child.getName());
		buf = fillBufferedDataContainer(buf);
	
	return buf;
	}
	
	private BufferedDataContainer fillBufferedDataContainer(BufferedDataContainer buf){
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
	
}
