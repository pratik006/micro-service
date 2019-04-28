package com.prapps.tutorial.spring.netflix.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prapps.tutorial.spring.netflix.student.Student;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DBServiceStarter.class})
public class DbServiceRestTest {
    private static final Logger LOG = LoggerFactory.getLogger(DbServiceRestTest.class);

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("spring.profiles.active", "local");
    }

    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    protected <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    @Test
    public void getAddStudent() throws Exception {
        String uri = "/rest/db/student";
        Student student = new Student();
        student.setEmail("abc@gmail.com");
        student.setFirstName("Test1");
        student.setLastName("Test2");
        String inputJson = mapToJson(student);
        LOG.debug("inputJson: " + inputJson);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        LOG.debug("studentResp: " + content);
        Student studentResp = mapFromJson(content, Student.class);
        assertEquals(student.getFirstName(), studentResp.getFirstName());
        assertEquals(student.getLastName(), studentResp.getLastName());
        assertEquals(student.getEmail(), studentResp.getEmail());
    }
}
