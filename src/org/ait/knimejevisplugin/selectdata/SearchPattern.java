package org.ait.knimejevisplugin.selectdata;

import java.util.List;

import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.JEVisDataSourceSQL;
import org.jevis.commons.cli.CommonCLIOptions.JEVis;

public class SearchPattern {
	
	JEVisDataSourceSQL jevis;
	String project;
	String location;
	String nodeType;
	String devicetype;
	String component;
	int nodeID;
	boolean parent; 
	boolean children;
	boolean siblings;
	boolean allChildren;
	
	List<String> fProject;
	List<String> fLocation;
	List<String> fNodeType;
	List<String> fDevicetype;
	List<String> fComponent;
	
	List<JEVisObject> searchresult;
	
	public SearchPattern(JEVisDataSourceSQL jevis, String project, String location, String nodeType, String devicetype,
			String component, int nodeID, boolean parent, boolean children, boolean siblings, boolean allChildren) {
		super();
		this.jevis = jevis;
		this.project = project;
		this.location = location;
		this.nodeType = nodeType;
		this.devicetype = devicetype;
		this.component = component;
		this.nodeID = nodeID;
		this.parent = parent;
		this.children = children;
		this.siblings = siblings;
		this.allChildren = allChildren;
	}


	public void searchProject() throws JEVisException{
		
		List<JEVisObject> projectlist = jevis.getObjects(jevis.getJEVisClass("Organization"),true);
		if(!project.isEmpty()){
			for(JEVisObject sproject : projectlist){
				if(sproject.getName().equals(project)){
					searchLocation(sproject);
				}
			}
		}else{
			for(JEVisObject project:projectlist){
				searchLocation(project);
			}
		}		
	}
	
	private void searchLocation(JEVisObject sproject) throws JEVisException{
		List<JEVisObject> locationlist = sproject.getChildren();
		if(!location.isEmpty()){
			for(JEVisObject slocation: locationlist){
				if(slocation.getName().equals(location)){
					
					searchNodeType(sproject, slocation);
				}				
			}	
		}else{
			for(JEVisObject slocation: locationlist){
				searchNodeType(sproject, slocation);
			}
		}
	}
	
	private void searchNodeType(JEVisObject sproject, JEVisObject slocation) throws JEVisException{
		List<JEVisClass> nodetypeList = jevis.getJEVisClasses();
		if(!nodeType.isEmpty()){
			for(JEVisClass snodetype: nodetypeList){
				if(snodetype.getName().equals(nodeType)){
					
					searchComponent(sproject, slocation, snodetype);		
				}
			}
		}else{
			
		}
	}
	
	private void searchComponent(JEVisObject sproject, JEVisObject slocation, JEVisClass snodetype)
			throws JEVisException{
		List<JEVisObject> componentlist = slocation.getChildren();
		if(!location.isEmpty()){
			for(JEVisObject scomponent : componentlist){
				if(scomponent.getName().equals(component)){
					searchDeviceType(sproject, slocation, snodetype, scomponent);
				}
			}
		}else{
			for(JEVisObject scomponent : componentlist){
				searchDeviceType(sproject, slocation, snodetype, scomponent);
			}
		}
		
	}
	
	private void searchDeviceType(JEVisObject sproject, JEVisObject slocation, JEVisClass snodetype,
			JEVisObject scomponent )
			throws JEVisException{
		List<JEVisObject> devices = scomponent.getChildren(jevis.getJEVisClass("Data"), true);
			if(!devicetype.isEmpty()){
				for(JEVisObject device: devices){
					searchresult.add(device);
				}
			}else{
				
			}
	}
	
	
	private JEVisObject searchProject1() throws JEVisException{
		
		List<JEVisObject> list_project = jevis.getObjects(jevis.getJEVisClass("Organization"), true);
		JEVisObject notfoundObject= null;
		
		for(JEVisObject pro: list_project){
			if(pro.getName().equals(project)){		
				return pro;
			}
				
		}
		return notfoundObject;
	}
	
	private List<JEVisObject> searchNodetype() throws JEVisException{
		List<JEVisObject> list_nodetype = jevis.getObjects(jevis.getJEVisClass(nodeType), true);
		return list_nodetype;
		
	}
	
	private void search() throws JEVisException{
		

		List<JEVisObject> searchlist = jevis.getObjects(jevis.getJEVisClass("Organization"), true);
		while(!searchlist.get(1).getJEVisClass().equals(jevis.getJEVisClass("Data"))){
			for(JEVisObject search:searchlist){
				if(search.getName()==project){
					JEVisObject organization = search;
					searchlist= search.getChildren();
					break;
				}	
			}
		}			
	}
	
	
	private long searchLocation() throws JEVisException{
		JEVisObject searchObject = jevis.getObject((long) nodeID);
		
		JEVisClass finder= null;
		do{
			List<JEVisObject> search1 = searchObject.getChildren();
			for(JEVisObject object : search1){
				finder = object.getJEVisClass();
				List<JEVisObject> search2= null;
				if(object.getJEVisClass()==jevis.getJEVisClass("Building")){
					if(object.getName().equals(location)){
						long locationid= object.getID();
					}else{
						
					}
				}else{
					search2 = object.getChildren();
				}
				
				for(JEVisObject object2 : search2){
					if(finder == jevis.getJEVisClass("Data")){
						break;
					}
					search1= object2.getChildren();
				}
			}
		}while(finder != jevis.getJEVisClass("Data"));
			
		
	}
}
