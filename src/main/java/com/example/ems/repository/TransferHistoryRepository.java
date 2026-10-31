
package com.example.ems.repository;

import com.example.ems.model.EmployeeTransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferHistoryRepository
                extends JpaRepository<EmployeeTransferHistory, Long> {

        List<EmployeeTransferHistory> findByEmployeeIdOrderByTransferDateDesc(Long employeeId);
}
