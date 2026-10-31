package com.example.ems.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_code_sequences")
public class EmployeeCodeSequence {
    @Id
    private String department;
    private long lastNumber;

    public EmployeeCodeSequence() {
    }

    public EmployeeCodeSequence(String department, long lastNumber) {
        this.department = department;
        this.lastNumber = lastNumber;
    }

    public String getDepartment() {
        return department;
    }

    public long getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(long n) {
        lastNumber = n;
    }
}
