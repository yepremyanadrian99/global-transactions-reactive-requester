package am.adrianyepremyan.global.transactions.requester.repository;

import am.adrianyepremyan.global.transactions.requester.domain.Folder;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FolderRepository extends ReactiveMongoRepository<Folder, String> {
}
