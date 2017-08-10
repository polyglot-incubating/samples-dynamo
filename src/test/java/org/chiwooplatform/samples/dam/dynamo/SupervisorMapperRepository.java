package org.chiwooplatform.samples.dam.dynamo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chiwooplatform.samples.entity.Supervisor;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class SupervisorMapperRepository {

    private final DynamoDBMapper mapper;

    @Autowired
    public SupervisorMapperRepository(AmazonDynamoDB amazonDynamoDB) {
        this.mapper = new DynamoDBMapper(amazonDynamoDB);
    }

    public void insertSupervisor(Supervisor supervisor) {
        mapper.save(supervisor);
    }

    public Supervisor getSupervisor(String company, String factory) {

        Map<String, String> expressionAttributesNames = new HashMap<>();
        expressionAttributesNames.put("#company", "company");
        expressionAttributesNames.put("#factory", "factory");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":company", new AttributeValue().withS(company));
        expressionAttributeValues.put(":factory", new AttributeValue().withS(factory));

        DynamoDBQueryExpression<Supervisor> dynamoDBQueryExpression = new DynamoDBQueryExpression<Supervisor>()
                .withIndexName("FactoryIndex")
                .withKeyConditionExpression(
                        "#company = :company and #factory = :factory ")
                .withExpressionAttributeNames(expressionAttributesNames)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);

        List<Supervisor> supervisor = mapper.query(Supervisor.class,
                dynamoDBQueryExpression);

        if (supervisor.size() > 0) {
            return supervisor.get(0);
        }
        else {
            return null;
        }
    }
}
