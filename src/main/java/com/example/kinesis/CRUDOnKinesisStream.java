package com.example.kinesis;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.model.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CRUDOnKinesisStream implements IRecordProcessorFactory, IRecordProcessor {

	private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	private final static String STREAM_NAME="TS-GreetingStreamTemplate-1DFVKMU0NUTYQ";

	static AmazonCloudFormation amazonCloudFormation = AmazonCloudFormationClientBuilder.standard()
			.withCredentials(new ProfileCredentialsProvider()).withRegion("us-west-2").build();

	static AmazonKinesis amazonKinesis = AmazonKinesisClientBuilder.standard()
			.withCredentials(new ProfileCredentialsProvider()).withRegion("us-west-2").build();

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public static void createKinesisStream(String stackName) {
		CreateStackRequest createRequest = new CreateStackRequest();
		createRequest.setStackName(stackName);
		createRequest.setTemplateBody(
				convertStreamToString(CRUDOnKinesisStream.class.getResourceAsStream("/GreetingStream.template")));
		System.out.println("Creating a stack called " + createRequest.getStackName() + ".");
		amazonCloudFormation.createStack(createRequest);

		// Wait for stack to be created
		// Note that you could use SNS notifications on the CreateStack call to
		// track the progress of the stack creation
		System.out.println("Stack creation completed, the stack " + stackName + " completed.");
	}

	public static void deleteKinesisStream(String streamName) {
		DeleteStackRequest deleteStackRequest = new DeleteStackRequest().withStackName(streamName);
		amazonCloudFormation.deleteStack(deleteStackRequest);

	}

	public static void putDataOnStream() {

		PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
		putRecordsRequest.setStreamName(STREAM_NAME);
		List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			PutRecordsRequestEntry putRecordsRequestEntry = new PutRecordsRequestEntry();
			putRecordsRequestEntry.setData(ByteBuffer.wrap(String.valueOf(i).getBytes()));
			putRecordsRequestEntry.setPartitionKey(String.format("partitionKey-%d", i));
			putRecordsRequestEntryList.add(putRecordsRequestEntry);
		}

		putRecordsRequest.setRecords(putRecordsRequestEntryList);
		PutRecordsResult putRecordsResult = amazonKinesis.putRecords(putRecordsRequest);
		System.out.println("Put Result" + putRecordsResult);
	}

	public static void getDataFromStream() throws UnknownHostException {
		int exitCode;
		String workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + UUID.randomUUID();
		KinesisClientLibConfiguration kinesisClientLibConfiguration = new KinesisClientLibConfiguration("Counter", // the
																													// application
																													// name
				STREAM_NAME, // the name of the stream we're connecting to
				new ProfileCredentialsProvider(), // AWS fun
				workerId); // a unique identifier for this
												// worker instance
		kinesisClientLibConfiguration.withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);
		kinesisClientLibConfiguration.withRegionName("us-west-2");

		IRecordProcessorFactory recordProcessorFactory = new CRUDOnKinesisStream();
		Worker worker = new Worker(recordProcessorFactory, kinesisClientLibConfiguration);
		System.out.printf("Running %s to process stream %s as worker %s...\n",
                "Counter",
                STREAM_NAME,
                workerId);
		exitCode = 0;
		try {
		worker.run();
		} catch(Exception e) {
			e.printStackTrace();
			exitCode = 1;
		}

		System.exit(exitCode);
	}

	public static void main(String[] args) throws UnknownHostException {
		 //createKinesisStream("TS");
		
		putDataOnStream();
//		 getDataFromStream();
		//deleteKinesisStream("TS");
		
		
				
		//checkKinesisStreamStatus("mars-consumer-ehoo-ingestion");
	}

	private static void checkKinesisStreamStatus(String streamName) {
		DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest();
		describeStreamRequest.setStreamName(streamName);
		DescribeStreamResult describeStreamResult = amazonKinesis.describeStream(describeStreamRequest);
		System.out.println("\n describeStreamResult.getStreamDescription().getStreamStatus(): "
				+ describeStreamResult.getStreamDescription().getStreamStatus());
	}


	@Override
	public IRecordProcessor createProcessor() {
		return new CRUDOnKinesisStream();
	}

	@Override
	public void initialize(String shardId) {
		System.out.println("Initializing record processor for shard: " + shardId);
	}

	@Override
	public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {
		System.out.println(records.size());

		for (Record record : records) {
			boolean processedSuccessfully = false;
			processSingleRecord(record);

			processedSuccessfully = true;


			if (!processedSuccessfully) {
				System.err.println("Couldn't process record " + record + ". Skipping the record.");
			}
		}
	}

	private void processSingleRecord(Record record) {
		// TODO Add your own record processing logic here

		String data = null;
		try {
			// For this app, we interpret the payload as UTF-8 chars.
			data = decoder.decode(record.getData()).toString();
			// Assume this record came from AmazonKinesisSample and log its age.
			//long recordCreateTime = new Long(data.substring("testData-".length()));
			//long ageOfRecordInMillis = System.currentTimeMillis() - recordCreateTime;

			System.out.println(record.getSequenceNumber() + ", " + record.getPartitionKey() + ", " + data + ", Created "
					 + " milliseconds ago.");
		} catch (NumberFormatException e) {
			System.out.println("Record does not match sample record format. Ignoring record with data; " + data);
		} catch (CharacterCodingException e) {
			System.err.println("Malformed data: " + data + "  " + e);
		}
	}

	@Override
	public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
		// TODO Auto-generated method stub

	}
}
