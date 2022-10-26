package com.dealer.service;

import com.dealer.model.DealerLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DealerLoginService {
    Logger logger = LoggerFactory.getLogger(DealerLoginService.class);

    private Region REGION = Region.US_WEST_2;
    private DynamoDbClient dynamoDb;
    @Autowired
    PasswordEncoder encoder;

    public DealerLogin login(DealerLogin input, String tableName) throws ResourceNotFoundException{
        DealerLogin response = new DealerLogin();
        initDynamoDbClient();
        Map<String, AttributeValue> returnedItem = getDynamoDBItem(this.dynamoDb, tableName,"username", input.getUsername());
        this.dynamoDb.close();
        logger.info("encoded password" + encoder.encode(input.getPassword()));
        if(returnedItem.get("password").equals(encoder.encode(input.getPassword()))){
            response.setDealerId(returnedItem.get("dealerId").toString());
            response.setMessage("Login Success");
        } else {
            logger.error("Invalid Credentials");
            response.setMessage("Invalid Credentials");
        }
        return response;
    }

    private void initDynamoDbClient() {
        String region = System.getProperty("DYNAMODB_REGION");
        if (null == region) {
            logger.info("Region not set, default \"" + Region.US_EAST_1.toString() + "\" is used");
            region = Region.US_EAST_1.toString();
        }
        logger.info("DynamoDB region: " + region);

        this.dynamoDb = DynamoDbClient.builder()
                .region(REGION)
                .build();
    }

    public  Map<String, AttributeValue> getDynamoDBItem(DynamoDbClient ddb, String tableName, String key, String keyVal ) {

        HashMap<String,AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(key, AttributeValue.builder()
                .s(keyVal)
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String,AttributeValue> returnedItem = ddb.getItem(request).item();
            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");

                return returnedItem;
//                for (String key1 : keys) {
//                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
//                }
            } else {
                logger.error("No item found with the key %s!\n", key);
            }

        } catch (DynamoDbException e) {
            logger.info(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}

