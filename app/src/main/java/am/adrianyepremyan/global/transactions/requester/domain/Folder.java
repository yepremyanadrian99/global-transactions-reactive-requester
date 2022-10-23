package am.adrianyepremyan.global.transactions.requester.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Data
public class Folder {

    @Id
    private String id;

    @Field
    private Long userId;

    @Field
    private String name;

    @Field
    private String description;
}
