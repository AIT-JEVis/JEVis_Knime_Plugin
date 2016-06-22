package org.ait.knimejevisplugin.selectdata;

import java.util.ArrayList;
import java.util.List;

import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;

public class SearchForStructure {
	
	JEVisDataSourceSQL jevis;
	
	int nodeID;
	boolean parent; 
	boolean children;
	boolean siblings;
	boolean allChildren;
	

	List<JEVisObject> resultlist = new ArrayList<JEVisObject>();
	
	public List<JEVisObject> searchForStructure() throws JEVisException{
		JEVisObject startingObject = jevis.getObject((long) nodeID);
		searchelement(startingObject);
		return resultlist;
	}
	
	public void searchelement(JEVisObject startingObject) throws JEVisException{
	
		if(parent){
			if(!startingObject.getJEVisClass().equals(jevis.getJEVisClass(
					JevisSelectDataNodeModel.configuration.projectLevelName))){
				List<JEVisObject> list_parents = startingObject.getParents();
				List<JEVisObject> listUsefulParents = getUsefullParent(list_parents);
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
				List<JEVisObject> listUsefulChildren = getUsefullChildren(list_children);
				fillResultList(listUsefulChildren);
				
			}else{
				JevisSelectDataNodeModel.logger.error("Object "+startingObject.getID().toString() 
						+ " is a datapoint. There are no useful children.");
				
			}

		}
		if(siblings){
			List<JEVisObject> list_siblings = startingObject.getParents().get(0).getChildren();
			
		}
		if(allChildren){
			//TODO: Insert function for getting allChildren
			List<JEVisObject> list_allChildren = startingObject.getChildren();

		}
		
		
	}
	
	private void fillResultList(List<JEVisObject> listUseful){
		for(JEVisObject useful : listUseful){
			resultlist.add(useful);
		}
	}
	
	private List<JEVisObject> getUsefullParent(List<JEVisObject> list_parents) throws JEVisException{
		List<JEVisObject> foundparents = new ArrayList<JEVisObject>();
		for(JEVisObject parent : list_parents){
			for(String level : JevisSelectDataNodeModel.configuration.levels){
				if(parent.getJEVisClass().equals(jevis.getJEVisClass(level))){					
					foundparents.add(parent);
				}
				else{
					getUsefullParent(parent.getParents());
				}
			}
		}
		return foundparents;
	}
	
	private List<JEVisObject> getUsefullChildren(List<JEVisObject> list_children) throws JEVisException{
		List<JEVisObject> foundChildren = new ArrayList<JEVisObject>();
		for(JEVisObject child : list_children){
			for(String level : JevisSelectDataNodeModel.configuration.levels){
				if(child.getJEVisClass().equals(jevis.getJEVisClass(level))){
					foundChildren.add(child);
				}
				else{
					getUsefullChildren(child.getChildren());
				}
			}
		}
		return foundChildren;
	}
	private List<JEVisObject> getAllChildren(List<JEVisObject> list_allChildren) throws JEVisException{
		List<JEVisObject> foundChildren = new ArrayList<JEVisObject>();
		for(JEVisObject child : list_allChildren){
			for(String level : JevisSelectDataNodeModel.configuration.levels){
				if(child.getJEVisClass().equals(jevis.getJEVisClass(level))){
					foundChildren.add(child);
					getAllChildren(child.getChildren());
				}
				else{
					getAllChildren(child.getChildren());
				}
			}
		}
		return foundChildren;
		
	}
	
}
