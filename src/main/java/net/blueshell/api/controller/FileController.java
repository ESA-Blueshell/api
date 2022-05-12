package net.blueshell.api.controller;

import net.blueshell.api.storage.StorageService;
import net.blueshell.api.storage.UploadFileResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        } else if (filename.endsWith(".jpg")) {
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.add("content-disposition", "inline;filename=\"" + resource.getFilename() + "\"");
        } else {
            headers.add("content-disposition", "attachment;filename=\"" + resource.getFilename() + "\"");
        }
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('COMMITTEE')")
    @ResponseBody
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = storageService.store(file);

        assert filename != null;
        String uri = StorageService.getDownloadURI(filename);

        return new UploadFileResponse(filename, uri, file.getContentType(), file.getSize());
    }


    @GetMapping("/bazinga")
    @ResponseBody
    public String getHtml() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">\n" +
                "  <meta name=\"robots\" content=\"noindex\">\n" +
                "  <title>AWOOOOGA</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<noscript>\n" +
                "  <strong>No javascript?? CRIIIIIIIIINGE</strong>\n" +
                "</noscript>\n" +
                "\n" +
                "\n" +
                "<div id=\"container\">\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n" +
                "<script>\n" +
                "    let image = document.createElement('img');\n" +
                "    image.src = 'https://esa-blueshell.nl/api/download/' + Math.ceil(Math.random() * 6) + '.jpg'\n" +
                "    image.style.cursor = 'pointer'\n" +
                "    image.addEventListener('click', () => {\n" +
                "        window.open(\"https://images-ext-2.discordapp.net/external/E179MQwXxZUFkccEPIOG-xS8VPBuQdLi4ZnrNXglcpM/%3F1296494117/https/i.kym-cdn.com/photos/images/newsfeed/000/096/044/trollface.jpg\")\n" +
                "    })\n" +
                "\n" +
                "    document.getElementById('container').insertAdjacentElement('afterbegin', image)\n" +
                "    let done = false\n" +
                "    function changeSize() {\n" +
                "        if (image.clientWidth) {\n" +
                "            done = true\n" +
                "            window.resizeTo(image.clientWidth + 100, image.clientHeight + 100)\n" +
                "        }\n" +
                "        if (!done) {\n" +
                "            setTimeout(changeSize, 50)\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    changeSize()\n" +
                "\n" +
                "</script>\n";
    }
}
