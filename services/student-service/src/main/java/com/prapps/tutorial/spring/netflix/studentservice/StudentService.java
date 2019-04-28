package com.prapps.tutorial.spring.netflix.studentservice;

import com.prapps.tutorial.spring.netflix.student.Student;
import com.prapps.tutorial.spring.netflix.student.StudentSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/student")
public class StudentService {
    private static Logger LOG = LoggerFactory.getLogger(StudentService.class);

    private RestTemplate restTemplate;

    @Value("${services.core-api.student-endpoint}")
    private String studentEndpoint;

    @Value("${services.email-service.endpoint}")
    private String emailEndpoint;

    @Autowired
    public StudentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/search")
    public StudentSearchResponse findStudentsByName(@RequestParam ("name") String name) {
        String targetUrl = studentEndpoint + "/" + name;
        LOG.debug("targetUrl: "+targetUrl);
        List<Student> students = restTemplate.exchange(targetUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Student>>() { }).getBody();
        StudentSearchResponse response = new StudentSearchResponse(students);
        response.getMessages().add("hashCode of StudentService: "+hashCode() );
        return response;
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        if (!StringUtils.isEmpty(student.getEmail())) {
            sendEmail(student.getEmail());
        }
        return restTemplate.postForObject(studentEndpoint, student, Student.class);
    }

    @Async
    void sendEmail(String emailId) {
        restTemplate.postForObject(emailEndpoint, emailId, Student.class);
    }

    @GetMapping("/tryluck")
    public String tryLuck() {
        if (new Random().nextBoolean()) {
            throw new RuntimeException();
        }

        return "success";
    }

    public String tryLuckFallback() {
        return "fallback for tryluck initiated";
    }

    @GetMapping("/tryhash")
    public String tryHash() {
        throw new StudentServiceException();
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "student resource not found")
    public class StudentServiceException extends RuntimeException {
    }
}
