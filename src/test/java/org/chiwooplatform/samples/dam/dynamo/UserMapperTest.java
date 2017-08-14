package org.chiwooplatform.samples.dam.dynamo;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import org.chiwooplatform.samples.AbstractDynamoTests;
import org.chiwooplatform.samples.entity.User;
import org.chiwooplatform.samples.support.DateUtils;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = {
		// "home",
		"default"
		// xxx
})
public class UserMapperTest extends AbstractDynamoTests<User> {

	@Autowired
	private DynamoDBMapper mapper;

	private String id = "scott";

	protected User model() {
		return model(id);
	}

	protected User model(String id) {
		User model = User.Builder.create().id(id).name(id + ", funny").joinDate().build();
		return model;
	}

	@Test
	public void testToLocalDate() throws Exception {
		log.info("toLocalDate: {}", DateUtils.toLocalDate("20170101", "yyyyMMdd"));
		log.info("amazonDynamoDB: {}", dynamoDB());
		log.info("mapper: {}", mapper);
	}

	@Test
	public void u1000_createTableIfNotExists() throws Exception {
		log.info("createTableIfNotExists");
		final DeleteTableRequest deleteTableRequest = new DeleteTableRequest(
				User.TABLE_NAME);
		TableUtils.deleteTableIfExists(dynamoDB(), deleteTableRequest);
		CreateTableRequest createTableRequest = mapper
				.generateCreateTableRequest(User.class);
		createTableRequest.setProvisionedThroughput(new ProvisionedThroughput()
				.withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		TableUtils.createTableIfNotExists(dynamoDB(), createTableRequest);
	}

	@Test
	public void u1001_lisetTable() throws Exception {
		log.info("DynamoDBMapper: {}", mapper);
		dynamoDB().listTables().getTableNames()
				.forEach((v) -> log.info("tableName: {}", v));
	}

	@Test
	public void ut1002_save() throws Exception {
		try {
			User newUser = model();
			mapper.save(newUser);
			User result = mapper.load(User.class, newUser.getId());
			printJson(result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1003_findOne() throws Exception {
		try {
			User result = mapper.load(User.class, id);
			log.info("result: {}", result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1004_save() throws Exception {
		try {

			User model = mapper.load(User.class, id);
			model.setNumberOfPlaylists(1);
			model.setPostCode("411012");
			model.setLeaveDate(DateUtils.nowInstant());
			model.setTestSet(new HashSet<>(Arrays.asList("1", "2", "3")));
			mapper.save(model);
			User result = mapper.load(User.class, id);
			log.info("result: {}", result);
			printJson(result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1005_batchSave() throws Exception {
		try {
			User u1 = User.Builder.create().joinDate().name("aider").numberOfPlaylists(1)
					.postCode("1221").testSet("1", "2").build();
			User u2 = User.Builder.create().joinDate().name("lamp").numberOfPlaylists(1)
					.postCode("1221").testSet("1").build();
			User u3 = User.Builder.create().joinDate().name("pinegreen")
					.numberOfPlaylists(1).postCode("1222").testSet("1", "2", "3").build();

			User u4 = User.Builder.create().joinDate().name("john").numberOfPlaylists(1)
					.postCode("1222").testSet("1").build();
			User u5 = User.Builder.create().joinDate().name("peter").numberOfPlaylists(1)
					.postCode("1222").testSet("1", "2", "3").build();
			User u6 = User.Builder.create().joinDate().name("carl").numberOfPlaylists(1)
					.postCode("1223").testSet("1", "2").build();
			mapper.batchSave(u1, u2, u3, u4, u5, u6);

		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1006_scan() throws Exception {
		try {

			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
			scanExpression.withConsistentRead(false);
			PaginatedScanList<User> result = mapper.scan(User.class, scanExpression);
			log.info("result.size(): ", result.size());
			printJson(result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1007_scanWithLimit() throws Exception {
		try {
			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
			scanExpression.withConsistentRead(false).withLimit(5);
			ScanResultPage<User> resultPage = mapper.scanPage(User.class, scanExpression);
			log.info("resultPage.getCount(): {}", resultPage.getCount());
			printJson(resultPage.getResults());
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1008_scanWithConditionLimit() throws Exception {
		try {
			Condition condition = new Condition();
			condition.setComparisonOperator(ComparisonOperator.CONTAINS);
			condition.setAttributeValueList(Arrays.asList(new AttributeValue("3")));
			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
			scanExpression.withConsistentRead(false).withLimit(5)
					.addFilterCondition("testSet", condition);
			ScanResultPage<User> resultPage = mapper.scanPage(User.class, scanExpression);
			log.info("resultPage.getCount(): {}", resultPage.getCount());
			printJson(resultPage.getResults());
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
