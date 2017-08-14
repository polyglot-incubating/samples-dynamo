package org.chiwooplatform.samples.support;

import java.util.Arrays;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

public class ConditionBuilder {

    public static Condition contains(final String value) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.CONTAINS)
                .withAttributeValueList(Arrays.asList(new AttributeValue(value)));
        return condition;
    }

}
