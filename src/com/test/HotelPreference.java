package com.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.test.model.HotelPVRecord;
import com.test.model.UserHotelPreference;
import com.test.util.Constant;

public class HotelPreference {

	public static void main(String[] args) {
		Configuration conf = new Configuration();
		try{
			Job job = new Job(conf);
			
			job.setJarByClass(HotelPreference.class);
			job.setMapperClass(HotelPreferenceMapper.class);
			job.setReducerClass(HotelPreferenceReducer.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
			FileInputFormat.setInputPaths(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[1]));
			
			System.exit(job.waitForCompletion(true)?0:1);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	public static class HotelPreferenceMapper extends Mapper<LongWritable, Text, Text, Text>{
		private Text keyText = new Text();
		private Text valueText = new Text();
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String valueStr = value.toString();
			String[] valArray = valueStr.split(Constant.TAB);
			// uid	hotel	price	star	city	zone	time
			String uid = valArray[0];
			String price = valArray[2];
			String star = valArray[3];
			String city = valArray[4];
			String zone = valArray[5];
			String time = valArray[6];
			
			keyText.set(uid);
			valueText.set("price:" + price + ",star:" + star + ",city:" + city + ",zone:" + zone + ",time:" + time);
			
			context.write(keyText, valueText);
		}
	}
	
	public static class HotelPreferenceReducer extends Reducer<Text, Text, Text, Text>{

		private Text keyText = new Text();
		private Text valueText = new Text();
		
		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			
			List<HotelPVRecord> records = new ArrayList<HotelPVRecord>();
			// parse record
			for(Text value : values){
				HotelPVRecord record = new HotelPVRecord();
				record.parseFromText(value.toString());
				records.add(record);
			}
			
			// sort by time
			Collections.sort(records);
			
			// calculate weight
			
			UserHotelPreference preference = new UserHotelPreference();
			int N = records.size();
			for(HotelPVRecord record : records){
				double weight = Math.pow(Math.E, Constant.COEF*(N-1));
				preference.addRecord(record, weight);  // add record to calculate
				N--;
			}
			
			//
			keyText.set(key);
			valueText.set(preference.toString());
			context.write(keyText, valueText);
		}
		
	}
}
