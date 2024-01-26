package com.Dash.Dashboard.Config;

import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MultipleMongoProperties.class)
public class MultipleMongoConfig {

    private final MultipleMongoProperties mongoProperties;

    @Primary
    @Bean(name = "userMongoTemplate")
    public MongoTemplate primaryMongoTemplate() throws Exception {
        return new MongoTemplate(primaryFactory(this.mongoProperties.getPrimary()));
    }

    @Bean(name = "verificationMongoTemplate")
    public MongoTemplate secondaryMongoTemplate() throws Exception {
        return new MongoTemplate(secondaryFactory(this.mongoProperties.getSecondary()));
    }

    @Bean
    @Primary
    public SimpleMongoClientDatabaseFactory primaryFactory(final MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }

    @Bean
    public SimpleMongoClientDatabaseFactory secondaryFactory(final MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }

}