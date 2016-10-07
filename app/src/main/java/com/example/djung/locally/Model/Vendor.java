package com.example.djung.locally.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;


/**
 * Describes a Vendor
 * Created by David Jung on 07/10/16.
 */

/**
 * Example Use:
 // Remember to add the following import statements
 import com.amazonaws.auth.CognitoCachingCredentialsProvider;
 import com.amazonaws.regions.Regions;
 import com.amazonaws.services.dynamodbv2.*;

 // Create a Dynamo Database Client
 AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);

 // Create a mapper from the client
 DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

 // Create a Vendor object
 Vendor vendor = new Vendor();
 vendor.setId(123);
 vendor.setName("UBC Farms");

 // Save the created vendor by using the mapper
 mapper.save(vendor);

 // To retrieve the created Vendor use
 Vendor selectedVendor = mapper.load(Vendor.class, 123);

 // To edit just set the desired attribute(s)
 selectedVendor.setName("Kitsilano Tomatoes");

 // Save the changed object
 mapper.save(selectedVendor);
 */

@DynamoDBTable(tableName = "Vendor")
public class Vendor {
    private int id;
    private String name;


    @DynamoDBHashKey(attributeName="Vendor.Id")
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
