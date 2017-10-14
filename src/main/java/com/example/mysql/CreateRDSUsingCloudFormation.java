package com.example.mysql;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.CreateStackRequest;
import com.amazonaws.services.cloudformation.model.CreateStackResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreateRDSUsingCloudFormation {
    static AmazonCloudFormation amazonCloudFormation = AmazonCloudFormationClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider()).withRegion("us-west-2").build();

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void main(String[] args) throws SQLException {

        createMySqlDB("sqlStack");
        //getEmp();
    }

    public static void createMySqlDB(String stackName) {
        CreateStackRequest createRequest = new CreateStackRequest();
        createRequest.setStackName(stackName);
        createRequest.setTemplateBody(
                convertStreamToString(CreateRDSUsingCloudFormation.class.getResourceAsStream("/MySql.template")));
        System.out.println("Creating a stack called " + createRequest.getStackName() + ".");
        CreateStackResult response = amazonCloudFormation.createStack(createRequest);

        //System.out.println("Stack ID: "+response.getStackId());
        System.out.println("Stack creation completed, the stack " + stackName + " completed.");
        // Wait for stack to be created
        // Note that you could use SNS notifications on the CreateStack call to
        // track the progress of the stack creation

    }

    public static List<Employee> getEmp() throws SQLException {
        Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement("select * from employee");
        ResultSet rs = stmt.executeQuery();
        Employee emp = new Employee();
        List<Employee> empList = new ArrayList<>();
        while (rs.next()) {
            System.out.println(rs.getInt(1) + "  " + rs.getString(2));
            emp.setId(rs.getInt(1));
            emp.setName(rs.getString(2));
            empList.add(emp);
        }
        con.close();
        return empList;
    }


    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/employeedb", "root", "admin123");
            return con;

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
