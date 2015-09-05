// Following is a skeleton for Mapreduce 2.0 API, but we can use the older version too.      
package de.tu_darmstadt.hadoop.tk1.pr4;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

/**
 * This class performs word count in the NADSAQ stock csv file.
 * @author Satia Herfert
 * @author Dipika Gupta
 * @author Anadi Tyagi
 * @author Floriment Klinaku
 */
public class WordCount {

	/**
	 * This inner class is designed to do the map part of MapReduce.
	 * 
	 * @author Satia
	 */
	public static class Map extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, IntWritable> {

		// This is a constant, that will be used for each occurence of a word.
		private static final IntWritable ONE = new IntWritable(1);
		// A variable to hold the current word.
		private Text word = new Text();

		/**
		 * This method is called with each line of text (value) and is expected
		 * to put the map results into the OutputCollector.
		 */
		@Override
		public void map(LongWritable key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {

			// Copied and adapted from
			// http://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html

			// Get the current line as a String
			String line = value.toString();
			// Use a tokenizer to split the line into words
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreTokens()) {
				// Words are separated by ",", so that is the delimiter for the
				// tokenizer
				word.set(tokenizer.nextToken(","));
				// Collect the occurrence of the word.
				output.collect(word, ONE);
			}
		}
	}

	/**
	 * This inner class is designed to do the reduce part of MapReduce.
	 * 
	 * @author Satia
	 */
	public static class Reduce extends MapReduceBase implements
			Reducer<Text, IntWritable, Text, IntWritable> {

		/**
		 * This method is called with each word (key) and is expected to reduce
		 * the values and put the results into the OutputCollector.
		 */
		@Override
		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {

			// Copied from
			// http://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html

			// In the simple word count example, the values for each key are
			// simply summed up ...
			int sum = 0;
			while (values.hasNext()) {
				sum += values.next().get();
			}
			// ... and put into the OutputCollector.
			output.collect(key, new IntWritable(sum));
		}
	}

	/**
	 * The main method that performs word count (very similar to the skeleton
	 * provided).
	 */
	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf(WordCount.class);
		conf.setJobName("First_MR_PRogram");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);

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
				"/usr/fk13myvu/floriment/output3"));
		// Run the job.
		JobClient.runJob(conf);
	}
}
