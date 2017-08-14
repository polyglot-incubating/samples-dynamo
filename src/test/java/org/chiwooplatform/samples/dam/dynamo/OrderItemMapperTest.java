package org.chiwooplatform.samples.dam.dynamo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import org.chiwooplatform.samples.AbstractDynamoTests;
import org.chiwooplatform.samples.entity.OrderItem;
import org.chiwooplatform.samples.support.DateUtils;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
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
@Import(OrderItemMapperTest.TemplateConfiguration.class)
public class OrderItemMapperTest extends AbstractDynamoTests<OrderItem> {

	@Configuration
	static class TemplateConfiguration {
		/*
		 * @Bean public String hello() { return new String("HELLO"); }
		 */
	}

	@Autowired
	private DynamoDBMapper mapper;

	private String id = "ORD-1001";

	protected OrderItem model() {
		return model(id);
	}

	protected OrderItem model(String id) {
		OrderItem model = OrderItem.Builder.create().orderId(id).itemId("NOTE-ACC-10022")
				.order(1).options("YELLOW").build();
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
				OrderItem.TABLE_NAME);
		TableUtils.deleteTableIfExists(dynamoDB(), deleteTableRequest);
		CreateTableRequest createTableRequest = mapper
				.generateCreateTableRequest(OrderItem.class);
		createTableRequest.setProvisionedThroughput(new ProvisionedThroughput()
				.withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		TableUtils.createTableIfNotExists(dynamoDB(), createTableRequest);
	}

	@Test
	public void u1001_lisetTable() throws Exception {
		dynamoDB().listTables().getTableNames()
				.forEach((v) -> log.info("tableName: {}", v));
	}

	@Test
	public void ut1002_save() throws Exception {
		try {
			OrderItem newOrderItem = model();
			mapper.save(newOrderItem);
			printJson(newOrderItem);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1002_findOne() throws Exception {
		try {
			OrderItem model = model();
			OrderItem result = mapper.load(OrderItem.class, model.getOrderId(),
					model.getItemId());
			log.info("result: {}", result);
			printJson(result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1003_save() throws Exception {
		try {
			OrderItem model = model();
			final String orderId = model.getOrderId();
			mapper.save(OrderItem.Builder.create().orderId(orderId)
					.itemId("NOTE-ACC-2011").order(2).options("BLACK").build());
			mapper.save(OrderItem.Builder.create().orderId(orderId)
					.itemId("NOTE-CBC-10022").order(1).options("BLUE").build());
			mapper.save(
					OrderItem.Builder.create().orderId(orderId).itemId("NOTE-CBC-2003")
							.order(2).options("YELLOW", "BLUE", "BLACK").build());
			mapper.save(OrderItem.Builder.create().orderId(orderId)
					.itemId("NOTE-KBC-10022").order(1).options("BLACK").build());
			mapper.save(OrderItem.Builder.create().orderId(orderId).itemId("KBC-ABA-1001")
					.order(1).options("BLACK").build());
			mapper.save(OrderItem.Builder.create().orderId(orderId).itemId("KBC-ABA-1011")
					.order(1).options("SILVER").build());
			mapper.save(OrderItem.Builder.create().orderId(orderId).itemId("KBC-ABA-1031")
					.order(1).options("WHITE").build());
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1004_query() throws Exception {

		try {

			DynamoDBQueryExpression<OrderItem> query = new DynamoDBQueryExpression<>();
			OrderItem orderItem = OrderItem.Builder.create().orderId(id).build();
			query.withHashKeyValues(orderItem);
			PaginatedQueryList<OrderItem> result = mapper.query(OrderItem.class, query);
			printJson(result);
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1005_queryWithLimit() throws Exception {
		try {
			OrderItem orderItem = OrderItem.Builder.create().orderId(id).build();
			DynamoDBQueryExpression<OrderItem> query = new DynamoDBQueryExpression<>();
			query.withHashKeyValues(orderItem).withLimit(5);
			QueryResultPage<OrderItem> queryResult = mapper.queryPage(OrderItem.class,
					query);
			log.info("result.getCount(): {}", queryResult.getCount());
			List<OrderItem> result = queryResult.getResults();
			printJson(result);
			result.forEach(v -> System.out.println(v));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Test
	public void ut1006_queryWithCondition() throws Exception {
		try {
			OrderItem orderItem = OrderItem.Builder.create().orderId(id).build();
			DynamoDBQueryExpression<OrderItem> query = new DynamoDBQueryExpression<>();
			query.withHashKeyValues(orderItem);

			Condition condition = new Condition();
			condition.setComparisonOperator(ComparisonOperator.CONTAINS);
			condition.setAttributeValueList(Arrays.asList(new AttributeValue("BLACK")));
			query.withQueryFilterEntry("options", condition);
			QueryResultPage<OrderItem> queryResult = mapper.queryPage(OrderItem.class,
					query);
			log.info("result.getCount(): {}", queryResult.getCount());
			List<OrderItem> result = queryResult.getResults();
			printJson(result);
			result.forEach(v -> System.out.println(v));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
