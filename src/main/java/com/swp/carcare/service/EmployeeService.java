package com.swp.carcare.service;

import com.swp.carcare.dto.EmployeeDto;
import com.swp.carcare.entity.EmployeeEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.EmployeeRepository;
import com.swp.carcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<EmployeeDto> getAllEmployees() {
        List<EmployeeEntity> employeeEntities = employeeRepository.findAll();
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        for (EmployeeEntity employee : employeeEntities) {
            EmployeeDto dto = new EmployeeDto(
                    employee.getId(),
                    employee.getFullName(),
                    employee.getGender(),
                    employee.getPhoneNumber(),
                    employee.getUser().getEmail(),
                    employee.getStatus()
            );
            employeeDtos.add(dto);
        }
        return employeeDtos;
    }

    public boolean saveEmployee(EmployeeDto employeeDto) {
        if (userRepository.findByEmail(employeeDto.getEmail()).isPresent()) {
            return false;
        }

        UserEntity user = new UserEntity();
        user.setEmail(employeeDto.getEmail());
        user.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        user.setStatus(employeeDto.getStatus());
        user.setRole(2);

        UserEntity savedUser = userRepository.save(user);

        EmployeeEntity employee = new EmployeeEntity();
        employee.setFullName(employeeDto.getFullName());
        employee.setGender(employeeDto.getGender());
        employee.setDescription(employeeDto.getDescription());
        employee.setPhoneNumber(employeeDto.getPhoneNumber());
        employee.setUser(savedUser);

        employeeRepository.save(employee);

        return true;
    }

    public Optional<EmployeeEntity> getEmployeeById(Integer id) {
        return employeeRepository.findById(id);
    }

    public boolean updateEmployee(Integer id, EmployeeDto employeeDto) {
        Optional<EmployeeEntity> optionalEmp = employeeRepository.findById(id);
        if (optionalEmp.isPresent()) {
            EmployeeEntity employee = optionalEmp.get();
            employee.setFullName(employeeDto.getFullName());
            employee.setGender(employeeDto.getGender());
            employee.setDescription(employeeDto.getDescription());
            employee.setPhoneNumber(employeeDto.getPhoneNumber());
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }

    public boolean toggleEmployeeStatus(Integer id, Integer newStatus) {
        Optional<EmployeeEntity> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            EmployeeEntity employee = employeeOpt.get();
            employee.setStatus(newStatus);
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }
}
