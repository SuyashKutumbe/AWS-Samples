package com.example.elasticbeanstalk;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import org.springframework.stereotype.Service;

@Service
public class ElasticBeanStalk {
    static AmazonCloudFormation amazonCloudFormation = AmazonCloudFormationClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider()).withRegion("us-west-2").build();

    /*public static void main(String[] args) {
        createElasticBeanStalk("ElasticBeanStalkStack");
    }*/

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    public static void createElasticBeanStalk(String stackName) {
        CreateStackRequest createRequest = new CreateStackRequest();
        createRequest.setStackName(stackName);
        createRequest.setTemplateBody(
                convertStreamToString(ElasticBeanStalk.class.getResourceAsStream("/ElasticBeanStalk.template")));
        System.out.println("Creating a stack called " + createRequest.getStackName() + ".");
        amazonCloudFormation.createStack(createRequest);

        // Wait for stack to be created
        // Note that you could use SNS notifications on the CreateStack call to
        // track the progress of the stack creation
        System.out.println("Stack creation completed, the stack " + stackName + " completed.");
    }
}
