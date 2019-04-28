package com.prapps.tutorial.spring.netflix.db;


import com.prapps.tutorial.spring.netflix.db.entity.StudentEntity;
import com.prapps.tutorial.spring.netflix.db.repo.CourseRepo;
import com.prapps.tutorial.spring.netflix.db.repo.StudentRepo;
import com.prapps.tutorial.spring.netflix.student.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/db/student")
public class StudentResource {
    private static final Logger LOG = LoggerFactory.getLogger(StudentResource.class);

    private StudentRepo studentRepo;
    private CourseRepo courseRepo;

    @Autowired
    public StudentResource(StudentRepo studentRepo, CourseRepo courseRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }

    @GetMapping("/{name}")
    public List<Student> findStudent(@PathVariable String name) {
        LOG.debug("hashCode: "+this.hashCode());
        List<Student> students = new ArrayList<>();
        return studentRepo.findByFirstNameIgnoreCaseOrLastNameIgnoreCase(name, name).stream().map(entity -> {
            Student student = new Student();
            BeanUtils.copyProperties(entity, student);
            return student;
        }).collect(Collectors.toList());
    }

    @PostMapping("/register")
    public StudentEntity register(@RequestParam("studentId") Long studentId, @RequestParam("courseId") Long courseId) {
        StudentEntity studentEntity = studentRepo.findById(studentId).get();
        studentEntity.getRegisteredCourses().add(courseRepo.findById(courseId).get());
        return studentRepo.save(studentEntity);
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        StudentEntity entity = new StudentEntity();
        BeanUtils.copyProperties(student, entity);
        Student savedStudent = new Student();
        BeanUtils.copyProperties(studentRepo.save(entity), savedStudent);
        return savedStudent;
    }
}
