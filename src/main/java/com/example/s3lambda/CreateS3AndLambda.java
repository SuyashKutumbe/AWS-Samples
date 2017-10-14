package com.example.s3lambda;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.*;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

public class CreateS3AndLambda {

    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    static AmazonCloudFormation amazonCloudFormation = AmazonCloudFormationClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider()).withRegion("us-west-2").build();

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void main(String[] args) throws Exception {
        createS3Bucket("Lambda");
        Thread.sleep(45000);
        updateS3("Lambda");
    }

    private static void updateS3(String stackName) {
        UpdateStackRequest updateStackRequest = new UpdateStackRequest();
        updateStackRequest.setStackName(stackName);
        updateStackRequest.setTemplateBody(
                convertStreamToString(CreateS3AndLambda.class.getResourceAsStream("/S3.template")));
        System.out.println("Creating a stack called " + updateStackRequest.getStackName() + ".");
        amazonCloudFormation.updateStack(updateStackRequest);
    }

    private static void createS3Bucket(String stackName) throws Exception {
        CreateStackRequest createStackRequest = new CreateStackRequest();
        createStackRequest.setStackName(stackName);
        createStackRequest.setTemplateBody(
                convertStreamToString(CreateS3AndLambda.class.getResourceAsStream("/Lambda.template")));
        System.out.println("Creating a stack called " + createStackRequest.getStackName() + ".");
        CreateStackResult stack = amazonCloudFormation.createStack(createStackRequest);
        System.out.println("Statck result is : "+stack);
        //System.out.println("Stack creation completed, the stack " + stackName + " completed with " +
         //       waitForCompletion(amazonCloudFormation, stackName));
    }

    public static String waitForCompletion(AmazonCloudFormation stackbuilder, String stackName) throws Exception {

        DescribeStacksRequest wait = new DescribeStacksRequest();
        wait.setStackName(stackName);
        Boolean completed = false;
        String  stackStatus = "Unknown";
        String  stackReason = "";

        System.out.print("Waiting");

        while (!completed) {
            List<Stack> stacks = stackbuilder.describeStacks(wait).getStacks();
            if (stacks.isEmpty())
            {
                completed   = true;
                stackStatus = "NO_SUCH_STACK";
                stackReason = "Stack has been deleted";
            } else {
                for (Stack stack : stacks) {
                    if (stack.getStackStatus().equals(StackStatus.CREATE_FAILED.toString())) {
                        completed = true;
                        stackStatus = stack.getStackStatus();
                        stackReason = stack.getStackStatusReason();
                        break;
                    }
                }
            }

            // Show we are waiting
            System.out.print(".");

            // Not done yet so sleep for 10 seconds.
            if (!completed) Thread.sleep(10000);
        }

        // Show we are done
        System.out.print("done\n");

        return stackStatus + " (" + stackReason + ")";
    }
}
