package com.swp.carcare.service;

import com.swp.carcare.entity.EmployeeEntity;
import com.swp.carcare.entity.ShiftEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.EmployeeRepository;
import com.swp.carcare.repository.ShiftRepository;
import com.swp.carcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    public EmployeeEntity getEmployeeFromAuthenticatedUser(String username) {
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return null;
        }
        return employeeRepository.findByUser(userEntity);
    }

    public Page<ShiftEntity> getShiftsForEmployee(EmployeeEntity employee, Pageable pageable, String date) {
        if (date != null && !date.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            return shiftRepository.findByEmployeeIdAndStartTimeBetween(employee.getId(), startOfDay, endOfDay, pageable);
        } else {
            return shiftRepository.findByEmployeeId(employee.getId(), pageable);
        }
    }

    public List<Map<String, Object>> getShiftSummaryForMonth(EmployeeEntity employee) {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

        List<ShiftEntity> allShiftsInMonth = shiftRepository.findAll().stream()
                .filter(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employee.getId()))
                .filter(s -> !s.getStartTime().isBefore(firstDay.atStartOfDay()) && !s.getStartTime().isAfter(lastDay.atTime(23, 59, 59)))
                .collect(Collectors.toList());

        Map<Integer, Long> shiftCountByDay = allShiftsInMonth.stream()
                .collect(Collectors.groupingBy(shift -> shift.getStartTime().getDayOfMonth(), Collectors.counting()));

        return shiftCountByDay.entrySet().stream().map(entry -> {
            Map<String, Object> row = Map.of(
                    "day", String.format("%02d/%02d/%d", entry.getKey(), now.getMonthValue(), now.getYear()),
                    "count", entry.getValue()
            );
            return row;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getShiftsForCalendar(EmployeeEntity employee) {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

        List<ShiftEntity> allShiftsInMonth = shiftRepository.findAll().stream()
                .filter(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employee.getId()))
                .filter(s -> !s.getStartTime().isBefore(firstDay.atStartOfDay()) && !s.getStartTime().isAfter(lastDay.atTime(23, 59, 59)))
                .collect(Collectors.toList());

        return allShiftsInMonth.stream().map(shift -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", "Ca làm");
            map.put("start", shift.getStartTime().toString());
            map.put("end", shift.getEndTime().toString());
            map.put("color", "#b39ddb");
            return map;
        }).collect(Collectors.toList());
    }

    public Page<ShiftEntity> getShifts(Integer empId, String date, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        if (empId != null) {
            return shiftRepository.findByEmployeeId(empId, pageable);
        } else if (date != null && !date.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            return shiftRepository.findByStartTimeBetween(startOfDay, endOfDay, pageable);
        } else {
            return shiftRepository.findAll(pageable);
        }
    }

    public String saveShift(Integer id, String rawDate, String fixedTime, Integer empId) {
        String onlyDate = rawDate.split("T")[0];
        LocalDate date = LocalDate.parse(onlyDate);

        LocalTime startTime = fixedTime.equals("MORNING") ? LocalTime.of(7, 0) : LocalTime.of(13, 0);
        LocalTime endTime = fixedTime.equals("MORNING") ? LocalTime.of(11, 0) : LocalTime.of(17, 0);

        LocalDateTime shiftStart = LocalDateTime.of(date, startTime);
        LocalDateTime shiftEnd = LocalDateTime.of(date, endTime);

        List<ShiftEntity> conflicts = shiftRepository.findConflictingShifts(empId, shiftStart, shiftEnd, id);
        if (!conflicts.isEmpty()) {
            return "Đã tồn tại ca làm trùng thời gian cho nhân viên";
        }

        ShiftEntity shift = id != null ? shiftRepository.findById(id).orElse(new ShiftEntity()) : new ShiftEntity();
        shift.setStartTime(shiftStart);
        shift.setEndTime(shiftEnd);

        if (empId != null) {
            EmployeeEntity employee = employeeRepository.findById(empId).orElse(null);
            shift.setEmployee(employee);
        } else {
            shift.setEmployee(null);
        }

        shiftRepository.save(shift);
        return "Lưu thành công";
    }

    public void deleteShift(Integer id) {
        shiftRepository.deleteById(id);
    }

}
