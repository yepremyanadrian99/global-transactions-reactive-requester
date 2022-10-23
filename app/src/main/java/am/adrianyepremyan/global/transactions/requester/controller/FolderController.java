package am.adrianyepremyan.global.transactions.requester.controller;

import am.adrianyepremyan.global.transactions.requester.dto.request.FolderCreateRequest;
import am.adrianyepremyan.global.transactions.requester.service.FolderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/folder")
public record FolderController(FolderService service) {

    @PostMapping
    public Mono<?> create(@RequestBody FolderCreateRequest request) {
        return service.create(request);
    }
}
