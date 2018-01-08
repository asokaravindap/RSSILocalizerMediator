package com.loc.mediator.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.loc.mediator.MediatorCore;
import com.loc.mediator.beans.CellCenter;
import com.loc.mediator.beans.LocResult;

public class MediatorCoreImpl implements MediatorCore {

	private String RegionsFile;
	private String CentersFile;
	private String WeightedAvgFile;
	
	private List<CellCenter> centerList = null;
	private List<LocResult> locResults;
	
	public MediatorCoreImpl(){
       
		locResults = new ArrayList<LocResult>();
		
		for(int i = 0 ; i < 27 ; i++){
			
			LocResult locResObj = new LocResult();
			locResObj.setCellID(i);
			locResObj.setProbability(0.5);
			
		locResults.add(locResObj);
		
		}
	}
	
	public String getRegions() {
		
		String regionsString = "";
		
		try {
			regionsString = readRegionsFile();
			
		} catch (IOException e) {
			
			regionsString = "IO ERROR";
			e.printStackTrace();
		}
		
		return regionsString;
	}

	public String getRegionsFile() {
		return RegionsFile;
	}

	public void setRegionsFile(String regionsFile) {
		RegionsFile = regionsFile;
	}
	
	public String getCentersFile() {
		return CentersFile;
	}

	public void setCentersFile(String centersFile) {
		CentersFile = centersFile;
	}

	public String getWeightedAvgFile() {
		return WeightedAvgFile;
	}

	public void setWeightedAvgFile(String WeightedAvgFile) {
		this.WeightedAvgFile = WeightedAvgFile;
	}

	private String readRegionsFile() throws IOException{
		
		InputStream is = new FileInputStream(RegionsFile); 
		BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
		
		String line = buf.readLine(); 
		StringBuilder sb = new StringBuilder(); 
		
		while(line != null){ 
			sb.append(line); 
			line = buf.readLine(); 
		} 
		
		return sb.toString();
	}

	public List<LocResult> getLocResults() {
		
		return locResults;
	}

	public boolean setLocResults(List<LocResult> locResult) {
		
		this.locResults = locResult;
		
		// averaging indices...
		
		if(centerList == null){
			try {
				centerList = readCenters();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		int maxIdx = getMaxCell(locResult);
		int maxFloor = centerList.get(maxIdx).getFloor(); 
		
		double avgX = 0.0; 
		double avgY = 0.0;
		double sumProb = 0.0;
		
		for(LocResult loc : locResult){
			
			int cellID = loc.getCellID();
			
			if(centerList.get(cellID).getFloor() == maxFloor){
				
				double probability = loc.getProbability();
				sumProb = sumProb + probability;
				
				double currX = centerList.get(cellID).getX();
				double currY = centerList.get(cellID).getY();
				
				avgX = avgX + (probability*currX);
				avgY = avgY + (probability*currY);
			}
		}
		
		avgX = avgX / sumProb;
		avgY = avgY / sumProb;
				
		String timestamp = String.valueOf(System.currentTimeMillis());
		
		appendToWeightedAvgFile(String.valueOf(timestamp + "," +avgX + "," + avgY + "\n")); 
		
		//...
		
		return true;
	}
	
	private int getMaxCell(List<LocResult> locResult){
		
		double max = 0.0;
		int maxIdx = -1;
		
		for(LocResult loc : locResult){
			
			if(loc.getProbability() > max){
				max = loc.getProbability();
				maxIdx = loc.getCellID();
			}
		}
		
		return maxIdx;
	}
	
	private List<CellCenter> readCenters() throws IOException{
		
		InputStream is = new FileInputStream(CentersFile); 
		BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
		
		String line = buf.readLine(); 
		
		List<CellCenter> centerList = new ArrayList<CellCenter>();
		
		while(line != null){ 
			
			String[] cellFields = line.split(",");
			
			CellCenter cellCenter = new CellCenter();
			cellCenter.setId(Integer.parseInt(cellFields[0]));
			cellCenter.setX(Double.parseDouble(cellFields[1]));
			cellCenter.setY(Double.parseDouble(cellFields[2]));
			cellCenter.setFloor(Integer.parseInt(cellFields[3]));
			
			centerList.add(cellCenter);
			
			line = buf.readLine(); 
		} 
		
		return centerList;
	}
	
	private void appendToWeightedAvgFile(String data){
		
		BufferedWriter buffWriter = null;
		FileWriter fileWriter = null;

		try {
			
			File opFile = new File(WeightedAvgFile);

			if (!opFile.exists()) {
				opFile.createNewFile();
			}

			fileWriter = new FileWriter(opFile.getAbsoluteFile(), true);
			buffWriter = new BufferedWriter(fileWriter);

			buffWriter.write(data);

		} catch (IOException e) {
			  e.printStackTrace();
			  
		} finally {
			try {
			  if (buffWriter != null)
				  buffWriter.close();

			  if (fileWriter != null)
				  fileWriter.close();

			} catch (IOException ex) {
				  ex.printStackTrace();
			}
		}
	}

}
