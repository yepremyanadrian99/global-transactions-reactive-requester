package am.adrian.global.transactions.requester.mapper;

import am.adrian.global.transactions.requester.domain.Folder;
import am.adrian.global.transactions.requester.dto.response.FolderCreateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FolderMapper {

    FolderMapper INSTANCE = Mappers.getMapper(FolderMapper.class);

    FolderCreateResponse toCreateResponse(Folder folder);
}
