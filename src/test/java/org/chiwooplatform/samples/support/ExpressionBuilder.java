package org.chiwooplatform.samples.support;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class ExpressionBuilder<T> {

    private final Map<String, T> m;

    private ExpressionBuilder() {
        super();
        m = new HashMap<>();
    }

    public static ExpressionBuilder<String> attrName() {
        return new ExpressionBuilder<String>();
    }

    public ExpressionBuilder<T> name(T name) {
        this.m.put("#" + name, name);
        return this;
    }

    public static ExpressionBuilder<AttributeValue> attrValue() {
        return new ExpressionBuilder<AttributeValue>();
    }

    @SuppressWarnings("unchecked")
    public ExpressionBuilder<T> withS(String key, String value) {
        this.m.put(":" + key, (T) new AttributeValue().withS(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ExpressionBuilder<T> withSS(String key, String... value) {
        this.m.put(":" + key, (T) new AttributeValue().withSS(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ExpressionBuilder<T> withN(String key, String value) {
        this.m.put(":" + key, (T) new AttributeValue().withN(value));
        return this;
    }

    @SuppressWarnings("unchecked")
    public ExpressionBuilder<T> withB(String key, String... value) {
        this.m.put(":" + key, (T) new AttributeValue().withNS(value));
        return this;
    }

    public Map<String, T> build() {
        return this.m;
    }

    public static void main(String[] aaa) {
        Map<String, String> attrName = ExpressionBuilder.attrName().name("k1").name("k2")
                .build();
        System.out.println(attrName.toString());

        Map<String, AttributeValue> attrValue = ExpressionBuilder.attrValue()
                .withS("k1", "k1").withSS("k2", "k2-1", "k2-2").build();
        System.out.println(attrValue.toString());
    }
}
