package com.geektrust.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import org.json.simple.JSONObject;
@SuppressWarnings({"unused", "unchecked","resource"})
public class FamilyOperations {
	public static void getFamilyOperations() throws IOException {
		File tempFile =getInput();
	    boolean exists = tempFile.exists();
	    if(exists ==false) {
	    	System.out.println("File does not exist!!");
	    }
	    BufferedReader reader;
	    reader = new BufferedReader(new FileReader(tempFile));
	    String line =  reader.readLine();
	    ChildAdditionResponse additionStatus =new ChildAdditionResponse();
	    
	    while(line!=null) {
	    String operationName =getOperationName(line);
	    if(operationName.equalsIgnoreCase("ADD_CHILD")) {
	    	String[] data = getPersonData(line);
	    	String motherName = data[1];
	    	String childName =data[2];
	    	String gender = data[3];
	    	JSONObject jsonObj =null;
	    	 if(additionStatus.getJsonObj()!=null && !additionStatus.getJsonObj().isEmpty()) {
		    	  jsonObj = additionStatus.getJsonObj();
		     }
		     if(additionStatus.getJsonObj()==null || additionStatus.getJsonObj().isEmpty()) {
		    	  jsonObj = getExistingFamilyData();
		     }
	         additionStatus = addChild(motherName,childName,gender,jsonObj);
	         if(additionStatus.getAddResponse()!=null && !additionStatus.getAddResponse().isEmpty()) {
	    	 System.out.println(additionStatus.getAddResponse());
	     }
	    }

	    if(operationName.equalsIgnoreCase("GET_RELATIONSHIP")) {
		     String[] data = getPersonData(line);	
		     String name = data[1];
		     String relation = data[2];
		     System.out.println(name);
		     System.out.println(relation);
		     JSONObject jsonObj=null;
		     if(additionStatus.getJsonObj()!=null && !additionStatus.getJsonObj().isEmpty()) {
		    	  jsonObj = additionStatus.getJsonObj();
		     }
		     if(additionStatus.getJsonObj()==null || additionStatus.getJsonObj().isEmpty()) {
		    	  jsonObj = getExistingFamilyData();
		     }
		     Set<String> relativesNames = getRelationsShip(name,relation,jsonObj); 
		     System.out.println(relativesNames);
		     line = reader.readLine();
		     if(line==null || line.equals("")) {
		    	 FamilyOperations.getFamilyOperations();}
		    	}
	    		if(!operationName.equalsIgnoreCase("GET_RELATIONSHIP")) {
	    			line = reader.readLine();
	    		}
	    	}
	}
	
	private static File getInput() {
		Scanner in = new Scanner(System.in); 
		System.out.println("Please Enter file Path");
		String filePath = in.nextLine();
		String fileSubStrings[] = filePath.split("\\\\");
		int fileSubStringsSize = fileSubStrings.length;
		
		StringBuffer fileDirecotry = new StringBuffer();
		for(int i=0; i<fileSubStringsSize-1; i++) {
			fileDirecotry.append(fileSubStrings[i]+"\\");
		}
		String fileName = fileSubStrings[fileSubStringsSize-1];
	    File tempFile = new File(fileDirecotry+fileName);
	    boolean exists = tempFile.exists();
	    if(exists==true) {
	    	return tempFile;
	    }
	    if(exists ==false) {
	    	System.out.println("File does not exist!!");
	    	 tempFile =getInputData();
	    }
	    return tempFile;
	}

	private static File getInputData() {
		Scanner in = new Scanner(System.in); 
		System.out.println("Please Enter file Path");
		String filePath = in.nextLine();
		String fileSubStrings[] = filePath.split("\\\\");
		int fileSubStringsSize = fileSubStrings.length;
		
		StringBuffer fileDirecotry = new StringBuffer();
		for(int i=0; i<fileSubStringsSize-1; i++) {
			fileDirecotry.append(fileSubStrings[i]+"\\");
		}
		String fileName = fileSubStrings[fileSubStringsSize-1];
	    File tempFile = new File(fileDirecotry+fileName);
	    boolean exists = tempFile.exists();
	    if(exists==true) {
	    	return tempFile;
	    }
	    if(exists ==false) {
	    	System.out.println("File does not exist!!");
	    	tempFile =getInput();
	    }
	    return tempFile;
	}

	private static ChildAdditionResponse addChild(String motherName, String childName, String gender, JSONObject jsonObj) {
		HashMap<String, List<String>> fatherSonMap =  (HashMap<String,List<String>>) jsonObj.get("FatherSonReleation");
		HashMap<String, List<String>> fatherDaughterMap =  (HashMap<String,List<String>>) jsonObj.get("FatherDaugherRelation");
		HashMap<String, String> husbandWifeMapObj = (HashMap<String, String>) jsonObj.get("HusbandWife");
		List<String> fatherList = (List<String>) jsonObj.get("Fathers");
		List<String> motherList = (List<String>) jsonObj.get("Mothers");
		     String husbandName =null;
		     for (Entry<String, String> entry : husbandWifeMapObj.entrySet()){
					if(entry.getValue().equals(motherName)){
						husbandName = entry.getKey();
					}
		     }
		     ChildAdditionResponse res = new ChildAdditionResponse();
		     if(husbandName==null) {
		    	 res.setAddResponse("PERSON_NOT_FOUND");
		    	 res.setJsonObj(jsonObj);
		    	 return res;
		     }
		     
		     for (Entry<String, String> entry : husbandWifeMapObj.entrySet()){
					if(entry.getKey().equals(motherName)) {
						 res.setAddResponse("CHILD_ADDITION_FAILED");
						 res.setJsonObj(jsonObj);
				    	 return res;
					}
		     }
		     
		   if(gender.equalsIgnoreCase("Male")) {
			   List<String> sonList = fatherSonMap.get(husbandName);
			   if(sonList==null) {
				   sonList = new ArrayList<String>();
			   }
			   if(sonList!=null) {
				   sonList.add(childName);
			   }
			   fatherSonMap.put(husbandName, sonList);
			   jsonObj.put("FatherSonReleation", fatherSonMap);
			   res.setJsonObj(jsonObj);
			   res.setAddResponse("CHILD_ADDITION_SUCCEEDED");
			   return res;
		   } 
		   
		   if(gender.equalsIgnoreCase("Female")) {
			   List<String> daughterList = fatherDaughterMap.get(husbandName);
			   if(daughterList==null) {
				   daughterList = new ArrayList<String>();
			   }
			   if(daughterList!=null) {
				   daughterList.add(childName);
			   }
			   fatherDaughterMap.put(husbandName, daughterList);
			   jsonObj.put("FatherDaugherRelation", fatherDaughterMap);
			   res.setJsonObj(jsonObj);
			   res.setAddResponse("CHILD_ADDITION_SUCCEEDED");
			   return res;
		   }
		   res.setAddResponse("CHILD_ADDITION_FAILED");
		   res.setJsonObj(jsonObj);
		   return res;
	}

	private static Set<String> getRelationsShip(String name, String relation, JSONObject jsonObj) {
		HashMap<String, List<String>> fatherSonMap =  (HashMap<String,List<String>>) jsonObj.get("FatherSonReleation");
		HashMap<String, List<String>> fatherDaughterMap =  (HashMap<String,List<String>>) jsonObj.get("FatherDaugherRelation");
		HashMap<String, String> husbandWifeMapObj = (HashMap<String, String>) jsonObj.get("HusbandWife");
		List<String> fatherList = (List<String>) jsonObj.get("Fathers");
		List<String> motherList = (List<String>) jsonObj.get("Mothers");
			 
		  String  fatherName = getFatherName(fatherSonMap,name);
		     if(fatherName==null) {
			 fatherName = getFatherNameForDaugher(fatherDaughterMap,name);
		     }
		   
		  if(relation.equalsIgnoreCase("Sister-In-Law")){  
		  LinkedHashSet<String> sisterInLawNames = getSisterInLawNames(fatherSonMap,fatherName,husbandWifeMapObj,name,fatherDaughterMap);
		  if(sisterInLawNames!=null && !sisterInLawNames.isEmpty()){
			  return sisterInLawNames;
		    } 
		  }
		  
		  if(relation.equalsIgnoreCase("Brother-In-Law")){  
			  LinkedHashSet<String> brotherInLawNames = getBrotherInLawNames(fatherSonMap,fatherName,husbandWifeMapObj,name,fatherDaughterMap);
			  if(brotherInLawNames!=null && !brotherInLawNames.isEmpty()){
				  return brotherInLawNames;
			  } 
		  }
		  
		  if(relation.equalsIgnoreCase("Paternal-Uncle")){
		  LinkedHashSet<String>  paternalUncleNames = getPaternalUncles(fatherSonMap,fatherName,fatherDaughterMap);
		  if(paternalUncleNames!=null && !paternalUncleNames.isEmpty()){
			  return paternalUncleNames;
		  }
		  }
		  
		  if(relation.equalsIgnoreCase("Paternal-Aunt")){
		  LinkedHashSet<String> paternalAuntNames = getPaternalAunts(fatherSonMap,fatherName,husbandWifeMapObj,fatherDaughterMap);
		  if(paternalAuntNames!=null && !paternalAuntNames.isEmpty()) {
			  return paternalAuntNames;
		  }
		  }
		   
		  if(relation.equalsIgnoreCase("Maternal-Uncle")){
		  LinkedHashSet<String> maternalUncleNames = getMaternalUncles(fatherSonMap,fatherName,husbandWifeMapObj,fatherDaughterMap);
		  if(maternalUncleNames!=null && !maternalUncleNames.isEmpty()) {
			  return maternalUncleNames;
		  }
		  }
		  if(relation.equalsIgnoreCase("Maternal-Aunt")) {
			  LinkedHashSet<String> maternalAuntNames  = new LinkedHashSet<String>();
			  maternalAuntNames = getMaternalAunts(fatherSonMap,fatherName,husbandWifeMapObj,fatherDaughterMap,maternalAuntNames);
			  if(maternalAuntNames!=null && !maternalAuntNames.isEmpty()) {
				  return maternalAuntNames;
			  }
		  }
		  
		  if(relation.equalsIgnoreCase("Son")) {
			  List<String> sonList = fatherSonMap.get(name);
			  if(sonList!=null && !sonList.isEmpty()) {
			  return new LinkedHashSet<String>(sonList);}
			  
			  sonList = new ArrayList<String>();
			  String husbandName=null;
			  for(Entry<String, String> entry : husbandWifeMapObj.entrySet()) {
				  if(entry.getValue().equals(name)) {
					  husbandName = entry.getKey();
				  }
			  }
			  sonList = fatherSonMap.get(husbandName);
			  if(sonList!=null && !sonList.isEmpty()) {
				  return new LinkedHashSet<String>(sonList);
			  }
		  }
		  
		  if(relation.equalsIgnoreCase("Daughter")) {
			  List<String> daughterList = fatherDaughterMap.get(name);
			  if(daughterList!=null && !daughterList.isEmpty()) {
			  return new LinkedHashSet<String>(daughterList);}
			  daughterList = new ArrayList<String>();
			  String husbandName=null;
			  for(Entry<String, String> entry : husbandWifeMapObj.entrySet()) {
				  if(entry.getValue().equals(name)) {
					  husbandName = entry.getKey();
				  }
			  }
			  daughterList = fatherDaughterMap.get(husbandName);
			  if(daughterList!=null && !daughterList.isEmpty()) {
				  return new LinkedHashSet<String>(daughterList);
			  }
		  }
		  
		  LinkedHashSet<String> siblingsSet = new LinkedHashSet<String>();
		  if(relation.equalsIgnoreCase("Siblings")) {
			  List<String> sonList = fatherSonMap.get(fatherName);
			  if(sonList!=null && !sonList.isEmpty()) {
				  List<String> sonList1 = new ArrayList<String>();
				  for(String son : sonList) {
					  if(!son.equals(name)) {
						  sonList1.add(son);
					  }
				  }
				  
				  siblingsSet.addAll(sonList1);
			  }
			  List<String> daugherList = fatherDaughterMap.get(fatherName);
			  if(daugherList!=null && !daugherList.isEmpty()) {
				  List<String> daughtherList1 = new ArrayList<String>();
				  for(String daughter : daugherList) {
					  if(!daughter.equals(name)) {
						  daughtherList1.add(daughter);
					  }
				  }
				  siblingsSet.addAll(daughtherList1);
			  }
			  return new LinkedHashSet<String>(siblingsSet);
		  }
		  return null;
	}
	
	private static LinkedHashSet<String> getBrotherInLawNames(HashMap<String, List<String>> fatherSonMap,
			String fatherName, HashMap<String, String> husbandWifeMapObj, String name,
			HashMap<String, List<String>> fatherDaughterMap) {
		//Boy's Wife's Brothers 
		LinkedHashSet<String> brotherInLawSet =new LinkedHashSet<String>();
		String wifeName = husbandWifeMapObj.get(name);
		String wifeFatherName = getFatherNameForDaugher(fatherDaughterMap, wifeName);
		List<String> brotherInLawsList = new ArrayList<String>();
		brotherInLawsList =  fatherSonMap.get(wifeFatherName);
		if(brotherInLawsList!=null && !brotherInLawsList.isEmpty()) {
		brotherInLawSet.addAll(brotherInLawsList);
		}
		
		//Girl, husband brother
		String husbandName=null;
		for(Entry<String,String> entry : husbandWifeMapObj.entrySet()) {
			if(entry.getValue().equals(name)) {
				husbandName = entry.getKey();
			}
		}
		String father = getFatherName(fatherSonMap, husbandName);
		List<String> brothersList = fatherSonMap.get(father);
		if(brothersList!=null && !brothersList.isEmpty()) {
			List<String> brothersList1 = new ArrayList<String>();
			for(String brother : brothersList) {
				if(!brother.equals(husbandName)) {
					brothersList1.add(brother);
				}
			}
			brotherInLawSet.addAll(brothersList1);
		}
		
		//Boys's/Girls Sisters Husbands
		List<String> sistersList = fatherDaughterMap.get(fatherName);
		 brotherInLawsList = new ArrayList<String>();
		if(sistersList!=null && !sistersList.isEmpty()) {
		for(String sister : sistersList) {
		for(Entry<String, String> entry : husbandWifeMapObj.entrySet()) {
			if(entry.getValue().equals(sister) && !entry.getValue().equals(name)){
				brotherInLawsList.add(entry.getKey());
			}
		}}}
		if(brotherInLawsList!=null && !brotherInLawsList.isEmpty()) {
			brotherInLawSet.addAll(brotherInLawsList);
		}
		return brotherInLawSet;
	}

	private static LinkedHashSet<String> getMaternalAunts(HashMap<String, List<String>> fatherSonMap, String fatherName,
			HashMap<String, String> husbandWifeMapObj, HashMap<String, List<String>> fatherDaughterMap, LinkedHashSet<String> maternalAuntNames) {
		    LinkedHashSet<String> maternalUncleNames = getMaternalUncles(fatherSonMap,fatherName,husbandWifeMapObj,fatherDaughterMap);
		    for(String maternalUncle : maternalUncleNames) {
			String aunt =  husbandWifeMapObj.get(maternalUncle);
			if(aunt!=null) {
				 maternalAuntNames.add(aunt); 
			 }
		  }
		  return maternalAuntNames;
	}

	private static LinkedHashSet<String> getMaternalUncles(HashMap<String, List<String>> fatherSonMap, String fatherName,
			HashMap<String, String> husbandWifeMapObj, HashMap<String, List<String>> fatherDaughterMap) {
			String motherName=null;
			motherName =husbandWifeMapObj.get(fatherName);
			String motherFatherName=null;  
			motherFatherName = getFatherNameForDaugher(fatherDaughterMap, motherName);
		  
			List<String> maternalUncleNamesList = new ArrayList<String>();
			maternalUncleNamesList = fatherSonMap.get(motherFatherName);
			return new LinkedHashSet<String>(maternalUncleNamesList);
	}

	private static LinkedHashSet<String> getPaternalAunts(HashMap<String, List<String>> fatherSonMap, String fatherName,
			HashMap<String, String> husbandWifeMapObj, HashMap<String, List<String>> fatherDaughterMap) {
			LinkedHashSet<String> petarnalAunts = new LinkedHashSet<String>();	
			String fName = fatherName;
		    List<String> fatherSonList=null;
		    List<String> fatherDaughterList =null;
			LinkedHashSet<String> paternalUncleNames=new LinkedHashSet<String>();
			
			fatherName = getFatherName(fatherSonMap, fatherName);
			if(fatherName==null) {
				fatherName = getFatherNameForDaugher(fatherDaughterMap, fatherName);
			}
			fatherSonList = fatherSonMap.get(fatherName);
			fatherDaughterList = fatherDaughterMap.get(fatherName);
			String removedFather = null;
			if(fatherSonList!=null && !fatherSonList.isEmpty()) {   
			for(String father : fatherSonList) {
					  if(father.equals(fName)) {
						  removedFather=father;
						  break;
					  }
			   }
			List<String> fatherSonList1 = new ArrayList<String>();
			for(String fatherSon : fatherSonList) {
				if(!fatherSon.equals(removedFather)) {
					fatherSonList1.add(fatherSon);
				}
			}
			paternalUncleNames = new LinkedHashSet<String>(fatherSonList1);
			}
		
			for(String uncle : paternalUncleNames) {
				String auntName = husbandWifeMapObj.get(uncle);
				if(auntName!=null && !auntName.isEmpty()) {
				petarnalAunts.add(auntName);}
			}
			petarnalAunts.addAll(fatherDaughterList);
			return petarnalAunts;
	}
	private static LinkedHashSet<String> getSisterInLawNames(HashMap<String, List<String>> fatherSonMap,
			String fatherName, HashMap<String, String> husbandWifeMapObj, String name, HashMap<String, List<String>> fatherDaughterMap) {
			List<String> fatherSonList=null;
			//Wife's Sisters (When Input is Boy)
			String wifeName = husbandWifeMapObj.get(name);
			String wifeFatherName = getFatherNameForDaugher(fatherDaughterMap, wifeName);
			List<String> fatherDaughtersName = fatherDaughterMap.get(wifeFatherName);
			LinkedHashSet<String> sisterInLawSet = new LinkedHashSet<String>();
			if(fatherDaughtersName!=null && !fatherDaughtersName.isEmpty()) {
			for(String daughter : fatherDaughtersName) {
				if(!daughter.equals(wifeName)) {
					sisterInLawSet.add(daughter);
				}
			}}
			
			// Husband's Sister(When Input is Girl)
			String husbandName = null;
			for (Entry<String, String> entry : husbandWifeMapObj.entrySet()){
		    	String wife  = entry.getValue();
		    		if(wife.equals(name)) {
		    			husbandName=entry.getKey();
		    		}
		    }
		String husbandFatherName = getFatherName(fatherSonMap, husbandName);	
		List<String>husbandSisters = fatherDaughterMap.get(husbandFatherName);
		if(husbandSisters!=null && !husbandSisters.isEmpty()) {
		sisterInLawSet.addAll(husbandSisters);}
		
		
		//Wives of Siblings When input is Boy/girl
		List<String> brothersName = fatherSonMap.get(fatherName);
		LinkedHashSet<String> sisterInLawsSubSet = new LinkedHashSet<String>();
		if(brothersName!=null && !brothersName.isEmpty()) {
		for(String brother : brothersName) {
			if(!brother.equals(name)) {
			String sisterInLaw = husbandWifeMapObj.get(brother);
			if(sisterInLaw!=null) {
			sisterInLawsSubSet.add(sisterInLaw);}
			}
		}}
		if(sisterInLawsSubSet!=null && !sisterInLawsSubSet.isEmpty()) {
			sisterInLawSet.addAll(sisterInLawsSubSet);
		}
		return sisterInLawSet;
	}

	private static LinkedHashSet<String> getPaternalUncles(HashMap<String, List<String>> fatherSonMap,
			String fatherName, HashMap<String, List<String>> fatherDaughterMap) {
			String fName = fatherName;
		    List<String> fatherSonList=null;
			LinkedHashSet<String> paternalUncleNames=new LinkedHashSet<String>();
			
			fatherName = getFatherName(fatherSonMap, fatherName);
			if(fatherName==null) {
				fatherName = getFatherNameForDaugher(fatherDaughterMap, fatherName);
			}
			fatherSonList = fatherSonMap.get(fatherName);
			String removedFather = null;
			if(fatherSonList!=null && !fatherSonList.isEmpty()) {   
			for(String father : fatherSonList) {
					  if(father.equals(fName)) {
						  removedFather=father;
						  break;
					  }
			   }
			List<String> fatherSonList1 = new ArrayList<String>();
			for(String fatherSon : fatherSonList) {
				if(!fatherSon.equals(removedFather)) {
					fatherSonList1.add(fatherSon);
				}
			}
			paternalUncleNames = new LinkedHashSet<String>(fatherSonList1);
			}
		   return paternalUncleNames; 
	}

	private static String getFatherNameForDaugher(HashMap<String, List<String>> fatherDaughterMap, String name) {
		String motherName =null;
		for (Entry<String, List<String>> entry : fatherDaughterMap.entrySet()){
	    	List<String> daughterList = entry.getValue();
	    	for(String daughter : daughterList) {
	    		if(daughter.equals(name)) {
	    			motherName=entry.getKey();
	    		}
	    	}
	    }
		return motherName;
	}

	private static String getFatherName(HashMap<String, List<String>> fatherSonMap, String name) {
		String fatherName =null;
		for (Entry<String, List<String>> entry : fatherSonMap.entrySet()) {
	    	List<String> sonsList = entry.getValue();
	    	for(String son : sonsList) {
	    		if(son.equals(name)) {
	    			fatherName=entry.getKey();
	    			break;
	    		}
	    	}
	    }
		return fatherName;
	}

	private static String[] getPersonData(String line) {
		String data[] = line.split("\\s+");
		return data;
	}

	private static String getOperationName(String line) {
		if(line.startsWith("GET_RELATIONSHIP")) {
			return "GET_RELATIONSHIP";
		}
		if(line.startsWith("ADD_CHILD")) {
			return "ADD_CHILD";
		}
		return "Wrong Format";
	}

	private static JSONObject getExistingFamilyData() {
		List<String> fathersList = new ArrayList<String>();
		List<String> mothersList = new ArrayList<String>();

		fathersList.add("King Shan");
		fathersList.add("Chit");
		fathersList.add("Vich");
		fathersList.add("Aras");
		fathersList.add("Vyan");
		fathersList.add("Jaya");
		fathersList.add("Arit");
		fathersList.add("Asva");
		fathersList.add("Vyas");
			
		mothersList.add("Queen Anga");
		mothersList.add("Amba");
		mothersList.add("Lika");
		mothersList.add("Chitra");
		mothersList.add("Satya");
		mothersList.add("Dritha");
		mothersList.add("Jnki");
		mothersList.add("Satvy");
		mothersList.add("Krpi");
 
 HashMap<String, List<String>> fatherSonRelationShipMap = new HashMap<String, List<String>>();
 HashMap<String, List<String>> fatherDaughterRelationShipMap = new HashMap<String, List<String>>();
 List<String> sonList = new ArrayList<String>();
 List<String> daughterList = new ArrayList<String>();
 sonList.add("Chit");
 sonList.add("Ish");
 sonList.add("Vich");
 sonList.add("Aras");
 
 daughterList.add("Satya");
 fatherSonRelationShipMap.put("King Shan", sonList);
 
 sonList = new ArrayList<String>();
 sonList.add("Vritha");
 fatherSonRelationShipMap.put("Chit", sonList);
 
 sonList = new ArrayList<String>();
 sonList.add("Ahit");
 fatherSonRelationShipMap.put("Aras", sonList);
 
 sonList = new ArrayList<String>();
 sonList.add("Asva");
 sonList.add("Vyas");
 fatherSonRelationShipMap.put("Vyan", sonList);
 
 
 sonList = new ArrayList<String>();
 sonList.add("Yodhan");
 fatherSonRelationShipMap.put("Jaya", sonList);
 
 
 sonList = new ArrayList<String>();
 sonList.add("Laki");
 fatherSonRelationShipMap.put("Arit", sonList);
 
 sonList = new ArrayList<String>();
 sonList.add("Vasa");
 fatherSonRelationShipMap.put("Asva", sonList);
 
 sonList = new ArrayList<String>();
 sonList.add("Kriya");
 fatherSonRelationShipMap.put("Vyas", sonList);
 
 
 
 fatherDaughterRelationShipMap.put("King Shan", daughterList);
 daughterList = new ArrayList<String>();
 daughterList.add("Dritha");
 daughterList.add("Tritha");
 fatherDaughterRelationShipMap.put("Chit", daughterList);
 
 daughterList = new ArrayList<String>();
 daughterList.add("Vila");
 daughterList.add("Chika");
 fatherDaughterRelationShipMap.put("Vich", daughterList);
 
 daughterList = new ArrayList<String>();
 daughterList.add("Jnki");
 fatherDaughterRelationShipMap.put("Aras", daughterList);
 
 daughterList = new ArrayList<String>();
 daughterList.add("Atya");
 fatherDaughterRelationShipMap.put("Vyan", daughterList);
 
 daughterList = new ArrayList<String>();
 daughterList.add("Lavnya");
 fatherDaughterRelationShipMap.put("Arit", daughterList);
 
 daughterList = new ArrayList<String>();
 daughterList.add("Krithi");
 fatherDaughterRelationShipMap.put("Vyas", daughterList);
 
 
 HashMap<String, String> husbandWifeMap = new HashMap<String, String>();
 husbandWifeMap.put("King Shan", "Queen Anga");
 husbandWifeMap.put("Chit", "Amba");
 husbandWifeMap.put("Vich", "Lika");
 husbandWifeMap.put("Aras", "Chitra");
 husbandWifeMap.put("Vyan", "Satya");
 husbandWifeMap.put("Jaya", "Dritha");
 husbandWifeMap.put("Arit", "Jnki");
 husbandWifeMap.put("Asva", "Satvy");
 husbandWifeMap.put("Vyas", "Krpi");
 
 
 JSONObject jsonObj = new JSONObject();
 jsonObj.put("FatherSonReleation", fatherSonRelationShipMap);
 jsonObj.put("FatherDaugherRelation", fatherDaughterRelationShipMap);
 jsonObj.put("HusbandWife", husbandWifeMap);
 jsonObj.put("Fathers", fathersList);
 jsonObj.put("Mothers", mothersList);
 return jsonObj;
}
}
