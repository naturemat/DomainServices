package infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MapperConfigurer;

@Configuration
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory, MappingMongoConverter converter) {
        MongoTemplate template = new MongoTemplate(mongoDbFactory, converter);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return template;
    }
}