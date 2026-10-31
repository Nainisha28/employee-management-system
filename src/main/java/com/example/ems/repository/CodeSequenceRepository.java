package com.example.ems.repository;

import com.example.ems.model.EmployeeCodeSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeSequenceRepository extends JpaRepository<EmployeeCodeSequence, String> {
}
