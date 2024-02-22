package com.Dash.Dashboard.Config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import com.mongodb.client.MongoClients;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MultipleMongoProperties.class)
public class MultipleMongoConfig {

    private final MultipleMongoProperties mongoProperties;

    @Primary
    @Bean(name = "userMongoTemplate")
    public MongoTemplate primaryMongoTemplate() {
        return new MongoTemplate(primaryFactory(this.mongoProperties.getPrimary()));
    }

    @Bean(name = "verificationMongoTemplate")
    public MongoTemplate secondaryMongoTemplate() {
        return new MongoTemplate(secondaryFactory(this.mongoProperties.getSecondary()));
    }

    @Bean(name = "passwordResetMongoTemplate")
    public MongoTemplate tertiaryMongoTemplate() {
        return new MongoTemplate(tertiaryFactory(this.mongoProperties.getTertiary()));
    }

    @Bean
    @Primary
    public SimpleMongoClientDatabaseFactory primaryFactory(final MongoProperties mongo) {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }

    @Bean
    public SimpleMongoClientDatabaseFactory secondaryFactory(final MongoProperties mongo) {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }

    @Bean
    public SimpleMongoClientDatabaseFactory tertiaryFactory(final MongoProperties mongo) {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }

}