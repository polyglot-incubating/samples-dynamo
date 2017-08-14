package org.chiwooplatform.samples.dam.dynamo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.chiwooplatform.samples.AbstractDynamoTests;
import org.chiwooplatform.samples.entity.LanLocale;
import org.chiwooplatform.samples.support.ConditionBuilder;
import org.chiwooplatform.samples.support.DateUtils;
import org.chiwooplatform.samples.support.ExpressionBuilder;
import org.chiwooplatform.samples.support.ScanPageTemplate;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
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
@Import(LanLocaleMapperTest.Config.class)
public class LanLocaleMapperTest extends AbstractDynamoTests<LanLocale> {

    @Configuration
    static class Config {
        /*
         * @Bean public String hello() { return new String("HELLO"); }
         */
    }

    private String id = "hello";

    protected LanLocale model() {
        return model(id);
    }

    protected LanLocale model(String id) {
        LanLocale model = LanLocale.builder().code(id).lang("en").value("hello")
                .creator("lamp").createTime(DateUtils.now().getTime()).build();
        return model;
    }

    @Test
    public void ut1000_createTableIfNotExists() throws Exception {
        log.info("createTableIfNotExists");
        final DeleteTableRequest deleteTableRequest = new DeleteTableRequest(
                LanLocale.TABLE_NAME);
        TableUtils.deleteTableIfExists(dynamoDB(), deleteTableRequest);
        CreateTableRequest createTableRequest = mapper()
                .generateCreateTableRequest(LanLocale.class);
        createTableRequest.setProvisionedThroughput(new ProvisionedThroughput()
                .withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
        TableUtils.createTableIfNotExists(dynamoDB(), createTableRequest);
    }

    @Test
    public void ut1001_lisetTable() throws Exception {
        dynamoDB().listTables().getTableNames()
                .forEach((v) -> log.info("tableName: {}", v));
    }

    @Test
    public void ut1002_save() throws Exception {
        try {
            LanLocale newItem = model();
            mapper().save(newItem);
            printJson(newItem);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void ut1002_findOne() throws Exception {
        try {
            LanLocale model = model();
            LanLocale result = mapper().load(LanLocale.class, model.getCode(),
                    model.getLang());
            log.info("result: {}", result);
            printJson(result);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<LanLocale> toList(final String filename, final String lang)
            throws IOException {
        InputStream in = LanLocaleMapperTest.class.getResourceAsStream(filename);
        String en = IOUtils.toString(in, "UTF-8");
        List<String> list = Arrays.asList(en.split("\n"));
        return list.stream().map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", ""))
                .map(m -> {
                    String[] msg = m.split("=");
                    final String code = msg[0];
                    final String value = msg[1];
                    final LanLocale model = LanLocale.builder().code(code).lang(lang)
                            .value(value).createTime(DateUtils.now().getTime())
                            .creator("Tester").build();
                    return model;
                }).collect(Collectors.toList());
    }

    @Test
    public void ut1003_save() throws Exception {
        try {
            final List<LanLocale> english = toList("/en.txt", "en");
            final List<LanLocale> germany = toList("/de.txt", "de");
            final List<LanLocale> france = toList("/fr.txt", "fr");
            final List<LanLocale> korean = toList("/ko.txt", "ko");
            final List<LanLocale> all = new ArrayList<>();
            all.addAll(english);
            all.addAll(germany);
            all.addAll(france);
            all.addAll(korean);
            mapper().batchSave(all);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void ut1004_query() throws Exception {

        try {
            DynamoDBQueryExpression<LanLocale> query = new DynamoDBQueryExpression<>();
            LanLocale model = LanLocale.builder().code(id).build();
            query.withHashKeyValues(model);
            PaginatedQueryList<LanLocale> result = mapper().query(LanLocale.class, query);
            printJson(result);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void ut1005_queryWithLimit() throws Exception {
        try {
            LanLocale model = LanLocale.builder()
                    .code("AbstractAccessDecisionManager.accessDenied").build();
            DynamoDBQueryExpression<LanLocale> query = new DynamoDBQueryExpression<>();
            query.withHashKeyValues(model).withLimit(5);
            QueryResultPage<LanLocale> queryResult = mapper().queryPage(LanLocale.class,
                    query);
            log.info("result.getCount(): {}", queryResult.getCount());
            List<LanLocale> result = queryResult.getResults();
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
            LanLocale model = LanLocale.builder()
                    .code("AbstractAccessDecisionManager.accessDenied").build();
            DynamoDBQueryExpression<LanLocale> query = new DynamoDBQueryExpression<>();
            query.withHashKeyValues(model);

            Condition condition = new Condition();
            condition.setComparisonOperator(ComparisonOperator.CONTAINS);
            condition.setAttributeValueList(Arrays.asList(new AttributeValue("denied")));
            query.withQueryFilterEntry("value", condition);
            QueryResultPage<LanLocale> queryResult = mapper().queryPage(LanLocale.class,
                    query);
            log.info("result.getCount(): {}", queryResult.getCount());
            List<LanLocale> result = queryResult.getResults();
            printJson(result);
            result.forEach(v -> System.out.println(v));
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void ut1007_scan() throws Exception {
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            Condition condition = new Condition();
            condition.setComparisonOperator(ComparisonOperator.CONTAINS);
            condition.setAttributeValueList(Arrays.asList(new AttributeValue("as")));
            scanExpression.addFilterCondition("value", condition);
            ScanResultPage<LanLocale> queryResult = mapper().scanPage(LanLocale.class,
                    scanExpression);
            log.info("result.getCount(): {}", queryResult.getCount());
            List<LanLocale> result = queryResult.getResults();
            log.info("json: {}", toJson(result));
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void ut1008_withFilterCondition() throws Exception {
        try {
            DynamoDBScanExpression expression = new DynamoDBScanExpression();
            expression.withFilterConditionEntry("value", ConditionBuilder.contains("as"));
            ScanResultPage<LanLocale> queryResult = mapper().scanPage(LanLocale.class,
                    expression);
            List<LanLocale> result = queryResult.getResults();
            log.info("json: {}", toJson(result));
            log.info("result.getCount(): {}", queryResult.getCount());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Key Condition Expression To specify the search criteria, you use a key condition expression—a string that
     * determines the items to be read from the table or index.
     * 
     * You must specify the partition key name and value as an equality condition. You can optionally provide a second
     * condition for the sort key (if present).
     * 
     * The sort key condition must use one of the following comparison operators: <code>
    a = b               true if the attribute a is equal to the value b
    a < b               true if a is less than b
    a <= b              true if a is less than or equal to b
    a > b               true if a is greater than b
    a >= b              true if a is greater than or equal to b
    a BETWEEN b AND c   true if a is greater than or equal to b, and less than or equal to c.
    
    The following function is also supported:
    begins_with (a, 'value')     true if the value of attribute a begins with a particular substring.
    contains (a, 'value')     true if the value of attribute a begins with a particular substring.
    </code> http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Query.html
     * 
     * @throws Exception
     */
    @Test
    public void ut1009_withFilterExpression() throws Exception {
        try {
            DynamoDBScanExpression expression = new DynamoDBScanExpression();
            expression.withFilterExpression("contains(#value, :value)");
            expression.withExpressionAttributeNames(
                    ExpressionBuilder.attrName().name("value").build());
            expression.withExpressionAttributeValues(
                    ExpressionBuilder.attrValue().withS("value", "as").build());
            ScanResultPage<LanLocale> queryResult = mapper().scanPage(LanLocale.class,
                    expression);
            List<LanLocale> result = queryResult.getResults();
            log.info("json: {}", toJson(result));
            log.info("result.getCount(): {}", queryResult.getCount());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void ut1010_withProjectionExpression() throws Exception {
        try {
            DynamoDBScanExpression expression = new DynamoDBScanExpression();
            expression.withFilterExpression("contains(#value, :value) and #code=:code");
            expression.withExpressionAttributeNames(ExpressionBuilder.attrName()
                    .name("code").name("lang").name("value").build());
            expression.withExpressionAttributeValues(
                    ExpressionBuilder.attrValue().withS("value", "as")
                            .withS("code", "AnonymousAuthenticationProvider.incorrectKey")
                            .build());
            expression.withProjectionExpression("#code, #lang, #value");
            ScanResultPage<LanLocale> queryResult = mapper().scanPage(LanLocale.class,
                    expression);
            List<LanLocale> result = queryResult.getResults();
            log.info("json: {}", toJson(result));
            log.info("result.getCount(): {}", queryResult.getCount());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void ut1011_PageableExpression() throws Exception {
        try {
            DynamoDBScanExpression expression = new DynamoDBScanExpression();
            expression.withFilterConditionEntry("value", ConditionBuilder.contains("as"));
            PageRequest pageable = new PageRequest(0, 5);
            Page<LanLocale> page = new ScanPageTemplate<>(LanLocale.class)
                    .scanPage(mapper(), expression, pageable);
            log.info("page number: {}", page.getNumber());
            log.info("page totalElements: {}", page.getTotalElements());
            log.info("page totalPages: {}", page.getTotalPages());
            log.info("page: {}", toJson(page.getContent()));
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
