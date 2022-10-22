package am.adrian.global.transactions.requester.controller;

import am.adrian.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrian.global.transactions.requester.dto.response.FolderCreateResponse;
import am.adrian.global.transactions.requester.service.TestFolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/folder")
@RequiredArgsConstructor
public record TestFolderController(TestFolderService testService) {

    @PostMapping("/test/{count}")
    public Mono<FolderCreateResponse> testCreate(@PathVariable int count,
                                                 @RequestBody FolderCreateRequest request) {
        return testService.create(request, count);
    }
}
