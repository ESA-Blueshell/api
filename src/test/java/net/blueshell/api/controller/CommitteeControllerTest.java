package net.blueshell.api.controller;

import net.blueshell.api.BaseDocumentationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class CommitteeControllerTest extends BaseDocumentationTest {

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        super.setup(context); // Initialize with autowired context
    }

    @Test
    public void getCommittees() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/committees")
                        .param("isMember", "true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document.document());
    }
}