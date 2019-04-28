package com.prapps.tutorial.spring.netflix.db;

import com.prapps.tutorial.spring.netflix.db.entity.StudentEntity;
import com.prapps.tutorial.spring.netflix.student.Student;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DBServiceStarter.class})
public class DbServiceApplicationTests {

	@BeforeClass
	public static void setUp() {
		System.setProperty("spring.profiles.active", "local");
	}

	@Autowired
	StudentResource studentResource;

	@Test
	public void contextLoads() {

	}

	@Test
	public void addStudent() {
		Student student = new Student();
		student.setEmail("abc@gmail.com");
		student.setFirstName("Test1");
		student.setLastName("Test2");
		Student savedStudent = studentResource.addStudent(student);

		List<Student> students = studentResource.findStudent(student.getFirstName());
		students.stream().filter(s -> s.getFirstName().equals(student.getFirstName())
				&& s.getLastName().equals(student.getLastName())
				&& s.getEmail().equals(student.getEmail()));
	}

}
