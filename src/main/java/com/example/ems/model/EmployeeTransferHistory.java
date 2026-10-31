
package com.example.ems.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_transfer_history")
public class EmployeeTransferHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    private String oldCode;
    private String newCode;
    private String oldDepartment;
    private String newDepartment;
    private LocalDate transferDate;

    @Column(length = 1000)
    private String reason;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Default constructor required by JPA
    public EmployeeTransferHistory() {
    }

    // Constructor for creating a transfer-history record
    public EmployeeTransferHistory(
            Long employeeId,
            String oldCode,
            String newCode,
            String oldDepartment,
            String newDepartment,
            LocalDate transferDate,
            String reason) {

        this.employeeId = employeeId;
        this.oldCode = oldCode;
        this.newCode = newCode;
        this.oldDepartment = oldDepartment;
        this.newDepartment = newDepartment;
        this.transferDate = transferDate;
        this.reason = reason;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getOldCode() {
        return oldCode;
    }

    public String getNewCode() {
        return newCode;
    }

    public String getOldDepartment() {
        return oldDepartment;
    }

    public String getNewDepartment() {
        return newDepartment;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
