package am.adrian.global.transactions.requester.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
public class Item {

    @Id
    private String id;

    @DocumentReference
    private Folder folderId;
}
