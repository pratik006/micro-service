package com.prapps.tutorial.spring.netflix.db.repo;

import com.prapps.tutorial.spring.netflix.db.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepo extends JpaRepository<StudentEntity, Long> {
    List<StudentEntity> findByFirstNameIgnoreCaseOrLastNameIgnoreCase(String fName, String lName);
}
