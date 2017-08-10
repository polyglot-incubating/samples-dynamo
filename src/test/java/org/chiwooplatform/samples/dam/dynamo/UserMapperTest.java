package org.chiwooplatform.samples.dam.dynamo;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.samples.AbstractDynamoTests;
import org.chiwooplatform.samples.model.User;
import org.chiwooplatform.samples.support.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = {
		// "home",
		"default"
		// xxx
})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractDynamoTests.class,
		UserMapperTest.TemplateConfiguration.class })
public class UserMapperTest {

	@Configuration
	static class TemplateConfiguration {
	}

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	@Autowired
	private DynamoDBMapper mapper;

	protected CreateTableRequest createTable(Class<?> entity) {
		CreateTableRequest createRequest = mapper.generateCreateTableRequest(entity);
		createRequest.setProvisionedThroughput(new ProvisionedThroughput()
				.withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		return createRequest;
	}

	@Test
	public void u1000_createUserTable() throws Exception {
		log.info("DynamoDBMapper: {}", mapper);
		amazonDynamoDB.createTable(createTable(User.class));
	}

	private String id = "scott";

	protected User model() {
		return model(id);
	}

	protected User model(String id) {
		User model = User.Builder.create().id(id).name(id + ", funny").joinDate().build();
		return model;
	}

	@Test
	public void ut1001_save() throws Exception {
		try {
			User newUser = model();
			mapper.save(newUser);
			User result = mapper.load(User.class, newUser.getId());
			log.info("result: {}", result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1002_findOne() throws Exception {
		try {
			User result = mapper.load(User.class, id);
			log.info("result: {}", result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1003_save() throws Exception {
		try {

			User model = mapper.load(User.class, id);
			model.setNumberOfPlaylists(1);
			model.setPostCode("411012");
			model.setLeaveDate(DateUtils.nowInstant());
			model.setTestSet(new HashSet<>(Arrays.asList("1", "2", "3")));
			mapper.save(model);
			User result = mapper.load(User.class, id);
			log.info("result: {}", result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1004_query() throws Exception {

		try {

			// String company, String factory
			//
			// Map<String,String> expressionAttributesNames = new HashMap<>();
			// expressionAttributesNames.put("#company", "company");
			// expressionAttributesNames.put("#factory", "factory");
			//
			// Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
			// expressionAttributeValues.put(":company",new
			// AttributeValue().withS(company));
			// expressionAttributeValues.put(":factory",new
			// AttributeValue().withS(factory));
			//
			// DynamoDBQueryExpression<Supervisor> dynamoDBQueryExpression = new
			// DynamoDBQueryExpression<Supervisor>()
			// .withIndexName("FactoryIndex")
			// .withKeyConditionExpression("#company = :company and #factory = :factory ")
			// .withExpressionAttributeNames(expressionAttributesNames)
			// .withExpressionAttributeValues(expressionAttributeValues)
			// .withConsistentRead(false);

			// List<User> result = mapper.find(null);
			// log.info("result: {}", result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
