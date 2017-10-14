package com.example.s3lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {

    public static final Predicate<String> startWithAA = s -> s.startsWith("AA");


    public static void main(String[] args) {

        Person p1=new Person("shashi",Arrays.asList("ss@gmail.com","ss@yahoo.com"));
        Person p2=new Person("ddd",Arrays.asList("dd@gmail.com","dd@yahoo.com"));
        Person p3=new Person("gggg",Arrays.asList("ssjj@gmail.com"));

        List<Person> persons=Arrays.asList(p1,p2,p3);
        List<String> emails = persons.stream()
                .flatMap(person -> person.getEmails().stream())
                .filter(s -> s.startsWith("ss")).map(s -> s.toUpperCase())
                .collect(Collectors.toList());
        System.out.println(emails);

    }
}
