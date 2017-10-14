package com.example.dynamodb;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.s3.model.Region;

public class CloudWatch {

	public static void main(String[] args) throws ParseException {
		retrieveMetrics();
	}

	/**
	 * Perform the actual work of retrieving the metrics
	 * 
	 * @throws ParseException
	 */
	public static Object retrieveMetrics() throws ParseException {
		// the cloudwatch client to use
		AmazonCloudWatch cloudWatch = null;
		// the request to send to cloudwatch
		GetMetricStatisticsRequest request;
		// the metric stats result.
		GetMetricStatisticsResult result;

		// create credentials using the BasicAWSCredentials class
		/*
		 * BasicAWSCredentials credentials = new
		 * BasicAWSCredentials(connectionData.get("accessKey"),
		 * connectionData.get("secretAccessKey"));
		 */
		// create a cloudwatch client
		try {
			cloudWatch = AmazonCloudWatchClientBuilder.standard().withCredentials(new ProfileCredentialsProvider())
					.withRegion("us-west-2").build();
		} catch (AmazonServiceException amazonServiceException) {
			// if an error response is returned by AmazonIdentityManagement
			// indicating either a
			// problem with the data in the request, or a server side issue.
			System.err.println(new Object() {
			}.getClass().getName() + "  Exception:  " + amazonServiceException.getMessage());
			return amazonServiceException;
		} catch (AmazonClientException amazonClientException) {
			// If any internal errors are encountered inside the client while
			// attempting to make
			// the request or handle the response. For example if a network
			// connection is not
			// available.
			System.err.println(new Object() {
			}.getClass().getName() + "  Exception:  " + amazonClientException.getMessage());
			return amazonClientException;
		}
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

/*		String string = "2017-03-17 06:25";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = format.parse(string);
		SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date dateTime = dateParser.parse(format.format(date));
*/
		// prepare request
		request = new GetMetricStatisticsRequest();
		// request.setStartTime(new Date(System.currentTimeMillis() -
		// (60000*50)));
		int nMinutes = 500;
		request.setStartTime(new Date(System.currentTimeMillis() - (60000 * nMinutes)));
		request.setEndTime(new Date());
		System.out.println(request.getStartTime() + ":" + request.getEndTime());
		request.setPeriod(60);
		request.setNamespace("AWS/DynamoDB");
		// request.setMetricName("SuccessfulRequestLatency");
		request.setMetricName("ReturnedItemCount");

		Dimension dimension = new Dimension();
		dimension.setName("TableName");
		dimension.setValue("Movies");
		
		Dimension dimension2 = new Dimension();
		dimension2.setName("Operation");
		dimension2.setValue("Scan");

		request.setStatistics(Arrays.asList("Average", "Sum", "Minimum", "Maximum"));
		request.setDimensions(Arrays.asList(dimension, dimension2));

		// get the monitoring result!
		try {
			result = cloudWatch.getMetricStatistics(request);
		} catch (AmazonServiceException amazonServiceException) {
			// if an error response is returned by AmazonIdentityManagement
			// indicating either a
			// problem with the data in the request, or a server side issue.
			System.err.println(new Object() {
			}.getClass().getName() + " Exception: " + amazonServiceException.getMessage());
			return amazonServiceException;
		} catch (AmazonClientException amazonClientException) {
			// If any internal errors are encountered inside the client while
			// attempting to make
			// the request or handle the response. For example if a network
			// connection is not
			// available.
			System.err.println(new Object() {
			}.getClass().getName() + " Exception: " + amazonClientException.getMessage());
			return amazonClientException;
		}

		System.out.println("result: " + result);
		// get the data and print it out.
		List<Datapoint> data = result.getDatapoints();
		for (Datapoint datum : data) {
			System.out.println(" Datum: " + datum.getAverage());
		}

		// return the sorted data
		return data;
	}
}
