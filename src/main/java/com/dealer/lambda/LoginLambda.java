package com.dealer.lambda;

import com.dealer.model.DealerLogin;
import com.dealer.service.DealerLoginService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.iam.model.ContextEntry;

@SpringBootApplication
public class LoginLambda {
    private DynamoDbClient dynamoDb;
    private String DYNAMODB_TABLE_NAME = "login";


    public DealerLogin handleRequest(
            DealerLogin loginRequest, ContextEntry context) {

        DealerLoginService service = new DealerLoginService();
        return service.login(loginRequest, DYNAMODB_TABLE_NAME);
    }
}
