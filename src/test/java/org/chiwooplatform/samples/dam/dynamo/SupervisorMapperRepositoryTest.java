package org.chiwooplatform.samples.dam.dynamo;

import org.chiwooplatform.samples.AbstractDynamoTests;
import org.chiwooplatform.samples.entity.Supervisor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = {
        // "home",
        "default"
        // xxx
})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractDynamoTests.class,
        SupervisorMapperRepositoryTest.TemplateConfiguration.class })
public class SupervisorMapperRepositoryTest {

    @Configuration
    static class TemplateConfiguration {
        @Bean
        SupervisorMapperRepository supervisorMapperRepository(
                AmazonDynamoDB amazonDynamoDB) {
            return new SupervisorMapperRepository(amazonDynamoDB);
        }
    }

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private SupervisorMapperRepository supervisorMapperRepository;

    // private static final String emailPrefix = "john";

    @Before
    public void setUp() {
        TableCreator tableCreator = new TableCreator(amazonDynamoDB);
        tableCreator.deleteSupervisorsTable();
        tableCreator.createSupervisor();
    }

    @Test
    public void addSupervisor() {
        Supervisor supervisor = new Supervisor();
        supervisor.setName("John Doe");
        supervisor.setCompany("Company Name");
        supervisor.setFactory("London Factory");
        supervisorMapperRepository.insertSupervisor(supervisor);
    }

    @Test
    public void getSupervisor() {

        Supervisor supervisor = new Supervisor();
        supervisor.setCompany("Sun");
        supervisor.setFactory("Athens");
        supervisor.setName("John Doe");
        supervisorMapperRepository.insertSupervisor(supervisor);

        Supervisor result = supervisorMapperRepository.getSupervisor("Sun", "Athens");
        Assert.assertEquals("John Doe", result.getName());
        log.info("result: {}", result);
    }

}