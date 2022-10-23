package am.adrianyepremyan.global.transactions.requester.mapper;

import am.adrianyepremyan.global.transactions.requester.domain.Folder;
import am.adrianyepremyan.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrianyepremyan.global.transactions.requester.dto.response.FolderCreateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FolderMapper {

    FolderMapper INSTANCE = Mappers.getMapper(FolderMapper.class);

    FolderCreateResponse toCreateResponse(Folder folder);

    Folder toFolder(FolderCreateRequest request);
}
