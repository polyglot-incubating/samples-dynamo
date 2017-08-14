package org.chiwooplatform.samples.entity;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * LanLocale 메시지 객체는 DynamoDB 에 아래와 같이 저장 된다.
 * 
 * <code>
{
  "code" : "hello",
  "lang" : "en",
  "value" : "hello",
  "creator" : "lamp",
  "createTime" : 1502717621878
}</code>
 * 
 * code 속성은 HashKey, lang 속성은 RangeKey 로 구성 되어 있다.
 * 
 */
@Data
@DynamoDBTable(tableName = LanLocale.TABLE_NAME)
public class LanLocale {

    public static final String TABLE_NAME = "LanLocale";

    @Id
    @DynamoDBHashKey(attributeName = "code")
    private String code;
    @DynamoDBRangeKey(attributeName = "lang")
    private String lang;
    @DynamoDBAttribute(attributeName = "value")
    private String value;
    @DynamoDBAttribute(attributeName = "creator")
    private String creator;
    @DynamoDBAttribute(attributeName = "createTime")
    private Long createTime;

    public LanLocale() {
        super();
    }

    @Builder
    private LanLocale(String code, String lang, String value, String creator,
            Long createTime) {
        super();
        this.code = code;
        this.lang = lang;
        this.value = value;
        this.creator = creator;
        this.createTime = createTime;
    }
}
