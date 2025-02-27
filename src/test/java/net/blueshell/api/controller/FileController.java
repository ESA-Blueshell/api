//package net.blueshell.api.controller;
//
//import net.blueshell.api.auth.JwtRequestFilter;
//import net.blueshell.api.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(FileController.class)
//class FileControllerTest {
//
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private StorageService storageService;
//
//    @MockitoBean
//    private UserService userService;
//
//    @MockitoBean
//    private JwtRequestFilter jwtRequestFilter;
//
//    @BeforeEach
//    void setup(WebApplicationContext wac) {
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//    }
//
//    @Test
//    void downloadFile_forbiddenOnSignatures() throws Exception {
//        // filename starting with "signatures" should be forbidden
//        mockMvc.perform(get("/download/signatures-file.jpg"))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void downloadFile_returnsPdfInline() throws Exception {
//        String filename = "document.pdf";
//        byte[] content = "PDF content".getBytes();
//        Resource resource = new ByteArrayResource(content) {
//            @Override
//            public String getFilename() {
//                return filename;
//            }
//        };
//
//        Mockito.when(storageService.loadAsResource(eq(filename))).thenReturn(resource);
//
//        mockMvc.perform(get("/download/" + filename))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
//                .andExpect(header().string("Content-Disposition", "inline;filename=\"" + filename + "\""))
//                .andExpect(header().exists("Cache-Control"))
//                .andExpect(content().bytes(content));
//    }
//
//    @Test
//    void uploadFile_returnsUploadResponse() throws Exception {
//        // Prepare a mock MultipartFile
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "test.txt",
//                MediaType.TEXT_PLAIN_VALUE,
//                "Sample content".getBytes()
//        );
//
//        String storedFilename = "stored-test.txt";
//        String downloadUri = "http://localhost/download/stored-test.txt";
//
//        Mockito.when(storageService.store(any())).thenReturn(storedFilename);
//        Mockito.when(storageService.getDownloadURI(eq(storedFilename))).thenReturn(downloadUri);
//
//        mockMvc.perform(multipart("/upload").file(file))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.fileName").value(storedFilename))
//                .andExpect(jsonPath("$.fileDownloadUri").value(downloadUri))
//                .andExpect(jsonPath("$.fileType").value(MediaType.TEXT_PLAIN_VALUE))
//                .andExpect(jsonPath("$.size").value(file.getSize()));
//    }
//
//    @Test
//    void getHtml_returnsExpectedHtml() throws Exception {
//        mockMvc.perform(get("/bazinga"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
//                .andExpect(content().string(org.hamcrest.Matchers.containsString("<!DOCTYPE html>")))
//                .andExpect(content().string(org.hamcrest.Matchers.containsString("AWOOOOGA")));
//    }
//}
