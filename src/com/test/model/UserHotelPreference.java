package com.test.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UserHotelPreference {

	private Map<Double, Double> priceResult = new HashMap<Double, Double>();
	private Map<Integer, Double> starResult = new HashMap<Integer, Double>();
	private HashMap<Integer, HashMap<Integer, Double>> cityZoneResult = new HashMap<Integer, HashMap<Integer, Double>>();
	
	public UserHotelPreference(){}
	
	/**
	 * @param record PV记录
	 * @param weight 当前记录权重
	 */
	public void addRecord(HotelPVRecord record, double weight){
		double price = record.getPrice();
		int star = record.getStar();
		int city = record.getCity();
		int zone = record.getZone();
		
		if(price != -1){
			if(!priceResult.containsKey(price))
				priceResult.put(price, 0.0);
			priceResult.put(price, priceResult.get(price) + weight);
		}
		
		if(star != -1){
			if(!starResult.containsKey(star))
				starResult.put(star, 0.0);
			starResult.put(star, starResult.get(star) + weight);
		}
		
		if(city!=-1 && zone!=-1){
			if(!cityZoneResult.containsKey(city))
				cityZoneResult.put(city, new HashMap<Integer, Double>());
			if(!cityZoneResult.get(city).containsKey(zone))
				cityZoneResult.get(city).put(zone, 0.0);
			cityZoneResult.get(city).put(zone, cityZoneResult.get(city).get(zone) + weight);
			
		}
	}
	
	private String normalizeStar(){
		List<Map.Entry<Integer, Double>> stars = new ArrayList<Map.Entry<Integer, Double>>(starResult.entrySet());
		
		double starMin = Double.MAX_VALUE;
		double starMax = 0.0;
		
		// Get Max and Min
		// normalized: (x-Min) / (Max - Min)
		for(Map.Entry<Integer, Double> star : stars){
			if(star.getValue() < starMin)
				starMin = star.getValue();
			if(star.getValue() > starMax)
				starMax = star.getValue();
		}
		for(Map.Entry<Integer, Double> star : stars){
			star.setValue((star.getValue()-starMin)/(starMax-starMin));
		}
		
		// sort by weight value
		Collections.sort(stars, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Entry<Integer, Double> o1,
					Entry<Integer, Double> o2) {
				double re = o1.getValue() - o2.getValue();
				if(re < 0 )
					return 1;  // 倒排
				else if(re > 0)
					return -1;
				else 
					return 0;
			}
		});
		
		//
		StringBuffer sb = new StringBuffer();
		sb.append("star:{");
		for(int i=0; i<stars.size()-1; i++){
			Map.Entry<Integer, Double> star = stars.get(i);
			sb.append(star.getKey() + ":" + star.getValue() + ",");
		}
		Map.Entry<Integer, Double> star = stars.get(stars.size()-1);
		sb.append(star.getKey() + ":" + star.getValue());
		
		sb.append("}");
		return sb.toString();
	}
	
	
	private String normalizeZone(){
		return null;
	}

	private String normalizePrice(){
		List<Map.Entry<Double, Double>> prices = new ArrayList<Map.Entry<Double, Double>>(priceResult.entrySet());
		// calculate sum
		double weightSum = 0.0;
		for(Map.Entry<Double, Double> price : prices)
			weightSum += price.getValue();
		// 
		for(Map.Entry<Double, Double> price : prices)
			price.setValue(price.getValue()/weightSum);
		//
		double averagePrice = 0.0;
		for(Map.Entry<Double, Double> price : prices)
			averagePrice += price.getKey()*price.getValue();
			
		
		return "price:" + averagePrice;
	}
	
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(normalizeStar() + ",");
		sb.append(normalizePrice() + ",");
		sb.append(normalizeZone() );
		return sb.toString();
	}
	
	
}
