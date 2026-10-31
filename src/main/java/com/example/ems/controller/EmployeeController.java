
package com.example.ems.controller;

import com.example.ems.model.Employee;
import com.example.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Employee> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) LocalDate joiningFrom,
            @RequestParam(required = false) LocalDate joiningTo,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "employeeCode") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.search(
                search, department, role, status, gender,
                joiningFrom, joiningTo, minSalary, maxSalary,
                page, size, sortBy, direction);
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Employee> create(
            @Valid @RequestBody Employee employee) {

        return ResponseEntity.status(201)
                .body(service.create(employee));
    }

    @GetMapping("/{id}")
    public Employee get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/{id}/personal-details")
    public Employee details(@PathVariable Long id) {
        return service.get(id);
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Employee update(
            @PathVariable Long id,
            @Valid @RequestBody Employee employee) {

        return service.update(id, employee);
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }


    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/{id}/transfer")
    public Employee transfer(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String date = body.getOrDefault(
                "transferDate",
                LocalDate.now().toString());

        String reason = body.getOrDefault(
                "reason",
                "Department transfer");

        return service.transfer(
                id,
                body.get("newDepartment"),
                LocalDate.parse(date),
                reason);
    }

    @GetMapping("/{id}/transfer-history")
    public List<?> history(@PathVariable Long id) {
        return service.history(id);
    }
}
