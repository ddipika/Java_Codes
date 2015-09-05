// Following is a skeleton for Mapreduce 2.0 API, but we can use the older version too.      
package de.tu_darmstadt.hadoop.tk1.pr4;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

/**
 * This class computes the volume for each stock in the NADSAQ stock csv file.
 * 
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class StockVolume {

	/**
	 * This inner class is designed to do the map part of MapReduce.
	 
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
	 */
	public static class Map extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, LongWritable> {

		// A variable to hold the current word.
		private Text word = new Text();

		/**
		 * This method is called with each line of text (value) and is expected
		 * to put the map results into the OutputCollector.
		 */
		@Override
		public void map(LongWritable key, Text value,
				OutputCollector<Text, LongWritable> output, Reporter reporter)
				throws IOException {

			// Get the line
			String line = value.toString();
			// Ignore the very first line, that starts with "exchange"
			if (line.startsWith("exchange")) {
				return;
			}
			
			// Split the line by ","
			String[] splitedLine = line.split(",");
			// We are interested in splitedLine[1]/stockName and
			// splitedLine[7]/stockVolume

			// At index 1, the Stock name is given
			word.set(splitedLine[1]);
			// At index 7, the volume is given
			// Put this tuple into the OutputCollector
			output.collect(word,
					new LongWritable(Long.parseLong(splitedLine[7])));
		}
	}

	/**
	 * This inner class is designed to do the reduce part of MapReduce.
	 * 
	 * @author Satia
	 */
	public static class Reduce extends MapReduceBase implements
			Reducer<Text, LongWritable, Text, LongWritable> {

		/**
		 * This method is called with each stock (key) and is expected to reduce
		 * the values and put the results into the OutputCollector.
		 */
		public void reduce(Text key, Iterator<LongWritable> values,
				OutputCollector<Text, LongWritable> output, Reporter reporter)
				throws IOException {

			// Copied from
			// http://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html

			// Again, we only need the sum of the values.
			long sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}
			output.collect(key, new LongWritable(sum));
		}
	}

	/**
	 * The main method that performs stock volume (very similar to the skeleton
	 * provided).
	 */
	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf(StockVolume.class);
		conf.setJobName("First_MR_PRogram");

		conf.setOutputKeyClass(Text.class);
		// We had to change the OutputValueClass to LongWritable,
		// as we expected overflows with IntWritable
		conf.setOutputValueClass(LongWritable.class);

		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		// Configure Hadoop to use our configuration, so that
		// out DFS will be accesses. Path is relative to the environment
		// variable $HADOOP_HOME
		String hadoop_home = System.getenv("HADOOP_HOME");
		conf.addResource(new Path(hadoop_home + "/etc/hadoop/core-site.xml"));
		conf.addResource(new Path(hadoop_home + "/etc/hadoop/hdfs-site.xml"));

		// Set the input path (the file to work with).
		FileInputFormat.setInputPaths(conf, new Path(
				"/usr/fk13myvu/floriment/NASDAQ_daily_prices_A.csv"));
		// Set the output path (the file to save to).
		FileOutputFormat.setOutputPath(conf, new Path(
				"/usr/fk13myvu/floriment/stock_volume_long"));
		// Run the job.
		JobClient.runJob(conf);
	}
}
