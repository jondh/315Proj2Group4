package com.jondh.project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BasicData {
	ArrayList<Double> data = new ArrayList<Double>();
	Map<String, Double> var = new HashMap<String, Double>();
	Map<String, ArrayList<Double>> listVar = new HashMap<String, ArrayList<Double>>();
	Map<String, String> functions = new HashMap<String, String>();
	Map<String, String> functVar = new HashMap<String, String>();
	boolean illegalFormula;
	boolean subscriptError;
	boolean undefinedFunction;
	boolean outOfData;
	
	BasicData(){
		illegalFormula = false;
		subscriptError = false;
		undefinedFunction = false;
		outOfData = false;
	}
	
	public ArrayList<String> getErrors(){
		ArrayList<String> errors_ = new ArrayList<String>();
		if(illegalFormula){
			errors_.add("ILLEGAL FORMULA");
		}
		if(subscriptError){
			errors_.add("SUBSCRIPT ERROR");
		}
		if(undefinedFunction){
			errors_.add("UNDEFINED FUNCTION");
		}
		return errors_;
	}
	
	public void insertData(Double data_){
		data.add(data_);
	}
	public void insertDataArray(ArrayList<Double> data_){
		for(int i = 0; i < data_.size(); i++){
			data.add(data_.get(i));
		}
	}
	
	public Double getData(){
		if(data.size() > 0){
			return data.remove(0);
		}
		else{
			outOfData = true;
			return -1.0;
		}
	}
	
	public boolean updateVar(String var_, Double data_){
		if(outOfData){
			System.out.println("Out of data, done."); //TODO
			return false;
		}
		var.put(var_, data_);
		return true;
	}
	
	public Double getVar(String var_){
		if(var.containsKey(var_)){
			return var.get(var_);
		}
		else{
			illegalFormula = true;
			return -1.0;
		}
	}
	
	public boolean insertList(String var_, int index_, Double data_){
		ArrayList<Double> list = new ArrayList<Double>();
		if(listVar.containsKey(var_)){
			list = listVar.get(var_);
		}
//		else{
//			illegalFormula = true;
//			return false;
//		}
		if(list.size() < index_+1){
			for(int j = list.size(); j < index_+1; j++){
				list.add(0.0);
			}
		}
		list.set(index_, data_);
		if(outOfData){
			System.out.println("Out of data, done."); //TODO
			return false;
		}
		listVar.put(var_, list);
		return true;
	}
	
	public Double getList(String var_, int index_){
		ArrayList<Double> list = new ArrayList<Double>();
		if(listVar.containsKey(var_)){
			list = listVar.get(var_);
		}
		else{
			illegalFormula = true;
		}
		if(list.size() > index_){
			return list.get(index_);
		}
		else{
			subscriptError = true;
			return -1.0;
		}
	}
	
	public void insertFormula(String letter_, String var_, String formula_){
		functVar.put(letter_, var_);
		functions.put(letter_, formula_);
	}
	
	public String getFormula(String letter_, String var_){
		String functVar_;
		String function_;
		if(functVar.containsKey(letter_)){
			functVar_ = functVar.get(letter_);
			function_ = functions.get(letter_);
			return function_.replace(functVar_, var_);
		}
		else{
			undefinedFunction = true;
			return "";
		}
	}
}
