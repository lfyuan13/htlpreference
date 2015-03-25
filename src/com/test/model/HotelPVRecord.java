package com.test.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.test.util.Constant;

/**
 * HotelPV Bean
 * @author Administrator
 *
 */
public class HotelPVRecord implements Comparable<HotelPVRecord>{

	public static final String PRICE = "price";
	public static final String STAR = "star";
	public static final String CITY = "city";
	public static final String ZONE = "zone";
	public static final String TIME = "time";
	
	
	private double price;
	private int star;
	private int city;
	private int zone;
	private Date time;
	
	public HotelPVRecord(){
	}
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	public int getCity() {
		return city;
	}
	public void setCity(int city) {
		this.city = city;
	}

	public int getZone() {
		return zone;
	}
	public void setZone(int zone) {
		this.zone = zone;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	// parse from text
	public void parseFromText(String text){
		String[] items = text.split(Constant.COMMA);
		for(String item : items){
			String[] itemSplit = item.split(Constant.COLONN);
			switch(itemSplit[0]){
			case PRICE:
				setPrice(Double.parseDouble(itemSplit[1]));
				break;
			case STAR:
				setStar(Integer.parseInt(itemSplit[1]));
				break;
			case CITY:
				setCity(Integer.parseInt(itemSplit[1]));
				break;
			case ZONE:
				setZone(Integer.parseInt(itemSplit[1]));
				break;
			case TIME:
				SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
				try {
					setTime(format.parse(itemSplit[1]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	
	@Override
	public int compareTo(HotelPVRecord other) {
		if(time.before(other.getTime()))
			return -1;
		else if(time.after(other.getTime()))
			return 1;
		else
			return 0;
	}
	
	
}
