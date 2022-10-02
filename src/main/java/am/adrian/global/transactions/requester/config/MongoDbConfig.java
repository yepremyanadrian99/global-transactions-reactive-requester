package am.adrian.global.transactions.requester.config;

import am.adrian.global.transactions.requester.repository.FolderRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = FolderRepository.class)
public class MongoDbConfig {
}
