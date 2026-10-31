
package com.example.ems.service;

import com.example.ems.model.*;
import com.example.ems.repository.*;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class EmployeeService {

        private final EmployeeRepository employees;
        private final CodeSequenceRepository sequences;
        private final TransferHistoryRepository history;

        private static final Map<String, String> PREFIX = Map.of(
                        "IT", "I",
                        "HR", "H",
                        "Finance", "F",
                        "Sales", "S",
                        "Admin", "A");

        public EmployeeService(
                        EmployeeRepository employees,
                        CodeSequenceRepository sequences,
                        TransferHistoryRepository history) {

                this.employees = employees;
                this.sequences = sequences;
                this.history = history;
        }

        @Transactional
        public Employee create(Employee employee) {

                if (employees.existsByEmailIgnoreCase(employee.getEmail())) {
                        throw new IllegalArgumentException("Email already exists");
                }

                employee.setDepartment(cleanDepartment(employee.getDepartment()));
                employee.setEmployeeCode(nextCode(employee.getDepartment()));

                return employees.save(employee);
        }

        private String cleanDepartment(String department) {

                String match = PREFIX.keySet().stream()
                                .filter(d -> d.equalsIgnoreCase(department.trim()))
                                .findFirst()
                                .orElse(null);

                if (match == null) {
                        throw new IllegalArgumentException(
                                        "Department must be IT, HR, Finance, Sales, or Admin");
                }

                return match;
        }

        private String nextCode(String department) {

                EmployeeCodeSequence sequence = sequences.findById(department)
                                .orElseGet(() -> sequences.save(new EmployeeCodeSequence(department, 0)));

                sequence.setLastNumber(sequence.getLastNumber() + 1);
                sequences.save(sequence);

                return PREFIX.get(department)
                                + String.format("%04d", sequence.getLastNumber());
        }

        public Page<Employee> search(
                        String search,
                        String department,
                        String role,
                        String status,
                        String gender,
                        LocalDate joiningFrom,
                        LocalDate joiningTo,
                        BigDecimal minSalary,
                        BigDecimal maxSalary,
                        int page,
                        int size,
                        String sortBy,
                        String direction) {

                if (page < 0) {
                        page = 0;
                }

                if (!Set.of(5, 10, 20).contains(size)) {
                        size = 10;
                }

                Set<String> allowedSortFields = Set.of(
                                "employeeCode",
                                "fullName",
                                "department",
                                "roleName",
                                "email",
                                "mobile",
                                "annualSalary",
                                "status",
                                "joiningDate");

                if (!allowedSortFields.contains(sortBy)) {
                        sortBy = "employeeCode";
                }

                Sort sort = Sort.by(
                                "desc".equalsIgnoreCase(direction)
                                                ? Sort.Direction.DESC
                                                : Sort.Direction.ASC,
                                sortBy);

                Specification<Employee> specification = (root, query, cb) -> {
                        List<Predicate> predicates = new ArrayList<>();

                        if (search != null && !search.isBlank()) {
                                String pattern = "%" + search.toLowerCase() + "%";

                                predicates.add(cb.or(
                                                cb.like(cb.lower(root.get("employeeCode")), pattern),
                                                cb.like(cb.lower(root.get("fullName")), pattern),
                                                cb.like(cb.lower(root.get("email")), pattern),
                                                cb.like(cb.lower(root.get("mobile")), pattern),
                                                cb.like(cb.lower(root.get("department")), pattern),
                                                cb.like(cb.lower(root.get("roleName")), pattern),
                                                cb.like(cb.lower(root.get("address")), pattern),
                                                cb.like(cb.lower(root.get("status")), pattern)));
                        }

                        if (department != null && !department.isBlank()) {
                                predicates.add(
                                                cb.equal(root.get("department"), department));
                        }

                        if (role != null && !role.isBlank()) {
                                predicates.add(cb.equal(root.get("roleName"), role));
                        }

                        if (status != null && !status.isBlank()) {
                                predicates.add(cb.equal(root.get("status"), status));
                        }

                        if (gender != null && !gender.isBlank()) {
                                predicates.add(cb.equal(root.get("gender"), gender));
                        }

                        if (joiningFrom != null) {
                                predicates.add(
                                                cb.greaterThanOrEqualTo(
                                                                root.get("joiningDate"), joiningFrom));
                        }

                        if (joiningTo != null) {
                                predicates.add(
                                                cb.lessThanOrEqualTo(
                                                                root.get("joiningDate"), joiningTo));
                        }

                        if (minSalary != null) {
                                predicates.add(
                                                cb.greaterThanOrEqualTo(
                                                                root.get("annualSalary"), minSalary));
                        }

                        if (maxSalary != null) {
                                predicates.add(
                                                cb.lessThan(
                                                                root.get("annualSalary"), maxSalary));
                        }

                        return cb.and(predicates.toArray(new Predicate[0]));
                };

                return employees.findAll(
                                specification,
                                PageRequest.of(page, size, sort));
        }

        @Transactional
        public Employee transfer(
                        Long id,
                        String newDepartment,
                        LocalDate date,
                        String reason) {

                Employee employee = employees.findById(id)
                                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

                String targetDepartment = cleanDepartment(newDepartment);

                if (targetDepartment.equals(employee.getDepartment())) {
                        throw new IllegalArgumentException(
                                        "Employee is already in that department");
                }

                String oldDepartment = employee.getDepartment();
                String oldCode = employee.getEmployeeCode();
                String newCode = nextCode(targetDepartment);

                employee.setDepartment(targetDepartment);
                employee.setEmployeeCode(newCode);

                employees.save(employee);

                history.save(new EmployeeTransferHistory(
                                employee.getId(),
                                oldCode,
                                newCode,
                                oldDepartment,
                                targetDepartment,
                                date,
                                reason));

                return employee;
        }

        public List<EmployeeTransferHistory> history(Long id) {
                return history.findByEmployeeIdOrderByTransferDateDesc(id);
        }

        public Employee get(Long id) {
                return employees.findById(id)
                                .orElseThrow(() -> new NoSuchElementException("Employee not found"));
        }

        @Transactional
        public Employee update(Long id, Employee input) {

                Employee employee = get(id);

                if (employees.findByEmailIgnoreCase(input.getEmail())
                                .filter(existing -> !existing.getId().equals(id))
                                .isPresent()) {

                        throw new IllegalArgumentException("Email already exists");
                }

                String department = cleanDepartment(input.getDepartment());

                employee.setFullName(input.getFullName());
                employee.setEmail(input.getEmail());
                employee.setMobile(input.getMobile());
                employee.setAadhaar(input.getAadhaar());
                employee.setPan(input.getPan());
                employee.setDateOfBirth(input.getDateOfBirth());
                employee.setGender(input.getGender());
                employee.setAddress(input.getAddress());
                employee.setRoleName(input.getRoleName());
                employee.setJoiningDate(input.getJoiningDate());
                employee.setAnnualSalary(input.getAnnualSalary());
                employee.setStatus(input.getStatus());

                if (!department.equals(employee.getDepartment())) {

                        String oldDepartment = employee.getDepartment();
                        String oldCode = employee.getEmployeeCode();

                        employee.setDepartment(department);
                        employee.setEmployeeCode(nextCode(department));

                        history.save(new EmployeeTransferHistory(
                                        employee.getId(),
                                        oldCode,
                                        employee.getEmployeeCode(),
                                        oldDepartment,
                                        department,
                                        LocalDate.now(),
                                        "Department changed during employee update"));
                }

                return employees.save(employee);
        }

        @Transactional
        public void deactivate(Long id) {

                Employee employee = get(id);
                employee.setStatus("Inactive");

                employees.save(employee);
        }


}
