package com.example.EC2;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;

public class CreateEC2UsingCloudFormation {
    static AmazonCloudFormation amazonCloudFormation = AmazonCloudFormationClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider()).withRegion("us-west-2").build();


    public static void main(String[] args) {
        createEC2("EC2Stack");
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    public static void createEC2(String stackName) {
        CreateStackRequest createRequest = new CreateStackRequest();
        createRequest.setStackName(stackName);
        createRequest.setTemplateBody(
                convertStreamToString(CreateEC2UsingCloudFormation.class.getResourceAsStream("/EC2.template")));
        System.out.println("Creating a stack called " + createRequest.getStackName() + ".");
        amazonCloudFormation.createStack(createRequest);

        // Wait for stack to be created
        // Note that you could use SNS notifications on the CreateStack call to
        // track the progress of the stack creation
        System.out.println("Stack creation completed, the stack " + stackName + " completed.");
    }
}
