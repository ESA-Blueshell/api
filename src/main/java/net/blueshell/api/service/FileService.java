package net.blueshell.api.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.BadRequestException;
import net.blueshell.api.base.BaseModelService;
import net.blueshell.api.config.StorageConfig;
import net.blueshell.api.controller.response.UploadFileResponse;
import net.blueshell.api.common.enums.FileType;
import net.blueshell.api.exception.FileNotFoundException;
import net.blueshell.api.exception.StorageException;
import net.blueshell.api.model.File;
import net.blueshell.api.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;

@Service
public class FileService extends BaseModelService<File, Long, FileRepository> {

    private final Path rootLocation;

    @Value("${app.url}")
    private String appUrl;

    @Autowired
    public FileService(FileRepository fileRepository, StorageConfig properties) {
        super(fileRepository);
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Transactional(readOnly = true)
    public File findByName(String name) {
        return repository.findByName(name).orElseThrow(() ->
                new UsernameNotFoundException("File not found with name: " + name));
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    public Path storeFile(String fileName, byte[] data) {
        Path filePath = this.rootLocation.resolve(fileName);
        if (Files.exists(filePath)) {
            throw new BadRequestException();
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath.toString())) {
            outputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filePath;
    }


    public Resource loadAsResource(File file) {
        try {
            Path filePath = rootLocation.resolve(file.getName());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException(
                        "Could not read file: " + file.getName());
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + file.getName(), e);
        }
    }


    public UploadFileResponse uploadFile(FileType fileType, String entityName, MultipartFile file, String mimeExtension) throws IOException {
        // Validate content type
        String contentType = file.getContentType();
        if (!fileType.isValidContentType(contentType)) {
            throw new BadRequestException("Invalid file type for " + fileType.name());
        }

        // Determine file extension
        String extension = fileType.getFileExtension();
        if (extension == null) {
            extension = mimeExtension;
        }

        String fileName = fileType.generateFileName(entityName, extension);

        byte[] data = file.getBytes();
        Path filePath = storeFile(fileName, data);

        // Build and return response
        return new UploadFileResponse(fileName, filePath.toString(), contentType, file.getSize());
    }

    @Override
    protected Long extractId(File entity) {
        return entity.getId();
    }
}
