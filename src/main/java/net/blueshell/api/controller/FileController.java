package net.blueshell.api.controller;

import net.blueshell.api.base.BaseController;
import net.blueshell.api.model.File;
import net.blueshell.api.repository.FileRepository;
import net.blueshell.api.service.EventService;
import net.blueshell.api.service.FileService;
import net.blueshell.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * yoinked from <a href="https://attacomsian.com/blog/uploading-files-spring-boot">https://attacomsian.com/blog/uploading-files-spring-boot</a>
 */

@Controller
public class FileController extends BaseController<FileService, FileRepository> {

    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public FileController(FileService service, FileRepository repository, UserService userService, EventService eventService) {
        super(service, repository);
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    @PreAuthorize("hasPermission(#filename, 'File', 'See')")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        File file = service.findByName(filename);
        Resource resource = service.loadAsResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(file.getMediaType());
        headers.setContentDispositionFormData("attachment", file.getName());

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.DAYS).cachePublic())
                .headers(headers)
                .body(resource);
    }

//    @PostMapping("users/{userId}/debitMandate")
//    @PreAuthorize("hasPermission(#userId, 'User', 'Edit')")
//    public UploadFileResponse uploadUserDebitMandate(@PathVariable Long userId, @RequestBody File file) throws IOException {
//        User user = userService.findById(userId);
//        file.setName(user.getUsername());
//        return service.uploadFile(FileType.DEBIT_MANDATE, user.getFullName(), file, "pdf");
//    }
//
//
//    @PostMapping("users/{userId}/membershipForm")
//    @PreAuthorize("hasPermission(#userId, 'User', 'Edit')")
//    public UploadFileResponse uploadMembershipForm(@PathVariable Long userId, @RequestBody FileDTO dto) throws IOException {
//        User user = userService.findById(userId);
//        dto.setType(FileType.MEMBERSHIP_FORM);
//        dto.setName(user.getUsername());
//        return service.uploadFile(FileType.DEBIT_MANDATE, user.getFullName(), file, "pdf");
//    }
//
//    private String extractExtension(String contentType) {
//        try {
//            return MimeTypes.getDefaultMimeTypes().forName(contentType).getExtension();
//        } catch (MimeTypeException e) {
//            throw new BadRequestException("Unsupported file type");
//        }
//    }
}