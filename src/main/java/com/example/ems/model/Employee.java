
package com.example.ems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees", uniqueConstraints = {
        @UniqueConstraint(columnNames = "employee_code"),
        @UniqueConstraint(columnNames = "email")
})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, unique = true, length = 12)
    private String employeeCode;

    @NotBlank
    @Pattern(regexp = "[A-Za-z ]{2,100}")
    @Column(nullable = false)
    private String fullName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "[0-9]{10}")
    @Column(nullable = false, length = 10)
    private String mobile;

    @NotBlank
    @Pattern(regexp = "[0-9]{12}")
    @Column(nullable = false, length = 12)
    private String aadhaar;

    @NotBlank
    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]")
    @Column(nullable = false, length = 10)
    private String pan;

    @NotNull
    @PastOrPresent
    private LocalDate dateOfBirth;

    @NotBlank
    private String gender;

    @NotBlank
    @Column(length = 500)
    private String address;

    @NotBlank
    @Column(nullable = false)
    private String department;

    @NotBlank
    @Column(nullable = false)
    private String roleName;

    @NotNull
    private LocalDate joiningDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 14, scale = 2)
    private BigDecimal annualSalary;

    @NotBlank
    private String status = "Active";

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
