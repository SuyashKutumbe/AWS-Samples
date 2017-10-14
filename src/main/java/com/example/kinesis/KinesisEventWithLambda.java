package com.example.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.types.UserRecord;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;

import java.util.Arrays;
import java.util.stream.Stream;

public class KinesisEventWithLambda {
    public void processEvent(KinesisEvent event) {
        System.out.println("Event received : " + event);
        try {
            Stream<UserRecord> userRecordStream = event.getRecords().stream()
                    .map(x -> UserRecord.deaggregate(Arrays.asList(x.getKinesis())))
                    .flatMap(y -> y.stream());

            System.out.println(userRecordStream);

        } catch (Exception e) {
            System.out.println("Error processing kinesis record." + e);
        }
    }

}
