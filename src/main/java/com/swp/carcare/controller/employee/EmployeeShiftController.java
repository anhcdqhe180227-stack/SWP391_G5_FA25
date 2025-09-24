package com.swp.carcare.controller.employee;


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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee/shift")
public class EmployeeShiftController {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listShifts(Model model,
                             @RequestParam(required = false) String date,
                             @RequestParam(defaultValue = "0") int page) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        if (userEntity == null) {
            return "redirect:/employee/dashboard";
        }

        EmployeeEntity employee = employeeRepository.findByUser(userEntity);
        Pageable pageable = PageRequest.of(page, 5, Sort.by("startTime").descending());

        Page<ShiftEntity> shifts;

        if (date != null && !date.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            shifts = shiftRepository.findByEmployeeIdAndStartTimeBetween(employee.getId(), startOfDay, endOfDay, pageable);
        } else {
            shifts = shiftRepository.findByEmployeeId(employee.getId(), pageable);
        }

        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
        List<ShiftEntity> allShiftsInMonth = shiftRepository.findAll().stream()
                .filter(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employee.getId()))
                .filter(s -> !s.getStartTime().isBefore(firstDay.atStartOfDay()) && !s.getStartTime().isAfter(lastDay.atTime(23, 59, 59)))
                .toList();
        Map<Integer, Long> shiftCountByDay = new java.util.TreeMap<>();
        for (ShiftEntity shift : allShiftsInMonth) {
            int day = shift.getStartTime().getDayOfMonth();
            shiftCountByDay.put(day, shiftCountByDay.getOrDefault(day, 0L) + 1);
        }
        List<Map<String, Object>> shiftSummaryByDay = new java.util.ArrayList<>();
        for (int d = 1; d <= now.lengthOfMonth(); d++) {
            Map<String, Object> row = new java.util.HashMap<>();
            row.put("day", String.format("%02d/%02d/%d", d, now.getMonthValue(), now.getYear()));
            row.put("count", shiftCountByDay.getOrDefault(d, 0L));
            shiftSummaryByDay.add(row);
        }
        model.addAttribute("shiftSummaryByDay", shiftSummaryByDay);

        model.addAttribute("shifts", shifts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shifts.getTotalPages());
        model.addAttribute("selectedDate", date);

        return "employee/shift";
    }


    @GetMapping("/calendar")
    @ResponseBody
    public List<Map<String, Object>> getShiftsForCalendar() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        EmployeeEntity employee = employeeRepository.findByUser(userEntity);
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
        List<ShiftEntity> allShiftsInMonth = shiftRepository.findAll().stream()
            .filter(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employee.getId()))
            .filter(s -> !s.getStartTime().isBefore(firstDay.atStartOfDay()) && !s.getStartTime().isAfter(lastDay.atTime(23, 59, 59)))
            .toList();
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (ShiftEntity shift : allShiftsInMonth) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("title", "Ca làm");
            map.put("start", shift.getStartTime().toString());
            map.put("end", shift.getEndTime().toString());
            map.put("color", "#b39ddb");
            result.add(map);
        }
        return result;
    }
}
