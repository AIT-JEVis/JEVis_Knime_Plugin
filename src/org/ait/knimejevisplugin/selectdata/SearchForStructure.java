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

public class SearchForStructure {
	
	JEVisDataSourceSQL jevis;
	
	long nodeID;
	boolean parent; 
	boolean children;
	boolean siblings;
	boolean allChildren;
	

	List<JEVisObject> resultlist = new ArrayList<JEVisObject>();
	
	
	
	public SearchForStructure(JEVisDataSourceSQL jevis, long nodeID, boolean parent, boolean children,
			boolean siblings, boolean allChildren) {
		super();
		this.jevis = jevis;
		this.nodeID = nodeID;
		this.parent = parent;
		this.children = children;
		this.siblings = siblings;
		this.allChildren = allChildren;
	}
	
	public BufferedDataContainer structureSearch(BufferedDataContainer buf, DataTableSpec result) throws JEVisException{
		DataCell[] cells = new DataCell[result.getNumColumns()];
		int counter = 0;
		for(JEVisObject foundObject : searchForStructure()){
			cells[0] = new LongCell(foundObject.getID());
			cells[1] = new StringCell(foundObject.getName());
			cells[2] = new StringCell(foundObject.getJEVisClass().getName());
			counter++;
			DataRow row = new DefaultRow("Row"+ counter, cells);
			buf.addRowToTable(row);
		}
		return buf;
	}

	public List<JEVisObject> searchForStructure() throws JEVisException{
		JEVisObject startingObject = jevis.getObject(nodeID);
		JevisSelectDataNodeModel.configuration.fillLevel();
		List<JEVisObject> foundObjects = new ArrayList<JEVisObject>();
		searchelement(startingObject, foundObjects);
		return resultlist;
	}
	
	public void searchelement(JEVisObject startingObject, List<JEVisObject> foundObjects) throws JEVisException{
	
		if(parent){
			if(!startingObject.getJEVisClass().equals(jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.projectLevelName))){
				List<JEVisObject> list_parents = startingObject.getParents();
				List<JEVisObject> listUsefulParents = getUsefullParent(list_parents, foundObjects);
				fillResultList(listUsefulParents);
			
			}else{
				JevisSelectDataNodeModel.logger.error("Object "+startingObject.getID().toString() 
						+ " is a Project. There are no useful parents.");
				
			}

		}
		if(children){
			if(!startingObject.getJEVisClass().equals(jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.deviceLevelName))){
				List<JEVisObject> list_children = startingObject.getChildren();
				List<JEVisObject> listUsefulChildren = getUsefullChildren(list_children, foundObjects);
				fillResultList(listUsefulChildren);
				
			}else{
				JevisSelectDataNodeModel.logger.error("Object "+startingObject.getID().toString() 
						+ " is a datapoint. There are no useful children.");
				
			}

		}
		if(siblings){
			List<JEVisObject> list_siblings = startingObject.getParents().get(0).getChildren();
			fillResultList(list_siblings);
		}
		if(allChildren){
			//TODO: Insert function for getting allChildren
			List<JEVisObject> list_allChildren = startingObject.getChildren();
			fillResultList(getAllChildren(list_allChildren, foundObjects));
		}
		
		
	}
	
	private void fillResultList(List<JEVisObject> listUseful){
		for(JEVisObject useful : listUseful){
			resultlist.add(useful);
		}
	}
	
	private List<JEVisObject> getUsefullParent(List<JEVisObject> list_parents, List<JEVisObject> foundObjects) 
			throws JEVisException{
		boolean haslevel = false;
		for(JEVisObject parent : list_parents){
			for(String level : JevisSelectDataNodeModel.configuration.levels){
				if(parent.getJEVisClass().equals(jevis.getJEVisClass(level))){					
					foundObjects.add(parent);
					haslevel = true;
				}
			}
			if(!haslevel){
				getUsefullParent(parent.getParents(), foundObjects);
			}
		}
		return foundObjects;
	}
	
	private List<JEVisObject> getUsefullChildren(List<JEVisObject> list_children, 
			List<JEVisObject> foundObjects) throws JEVisException{
		boolean haslevel = false;
		for(JEVisObject child : list_children){
			for(String level : JevisSelectDataNodeModel.configuration.levels){
				if(child.getJEVisClass().equals(jevis.getJEVisClass(level))){
					foundObjects.add(child);
					haslevel = true;
				}
			}
			if(!haslevel){
				getUsefullChildren(child.getChildren(), foundObjects);
			}
		}
		return foundObjects;
	}
	private List<JEVisObject> getAllChildren(List<JEVisObject> list_allChildren, 
			List<JEVisObject> foundObjects)
			throws JEVisException{

		for(JEVisObject child : list_allChildren){
			for(String level : JevisSelectDataNodeModel.configuration.levels){
				if(child.getJEVisClass().equals(jevis.getJEVisClass(level))){
					foundObjects.add(child);
				}
			}
			getAllChildren(child.getChildren(), foundObjects);
		}
		return foundObjects;
		
	}
	
}
