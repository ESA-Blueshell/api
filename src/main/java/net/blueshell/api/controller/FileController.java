package net.blueshell.api.controller;

import net.blueshell.api.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.stream.Collectors;

/**
 * yoinked from <a href="https://attacomsian.com/blog/uploading-files-spring-boot">https://attacomsian.com/blog/uploading-files-spring-boot</a>
 */

@Controller
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

        Resource resource = storageService.loadAsResource(filename);

        HttpHeaders headers = new HttpHeaders();
        // If the file is a pdf, open it in a new tab
        if (filename.endsWith(".pdf")) {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("content-disposition", "inline;filename=\"" + resource.getFilename() + "\"");
        } else {
            headers.add("content-disposition", "attachment;filename=\"" + resource.getFilename() + "\"");
        }
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }
}