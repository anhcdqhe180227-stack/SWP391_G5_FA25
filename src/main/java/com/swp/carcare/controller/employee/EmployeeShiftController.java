package com.swp.carcare.controller.employee;

import com.swp.carcare.entity.EmployeeEntity;
import com.swp.carcare.entity.ShiftEntity;
import com.swp.carcare.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee/shift")
public class EmployeeShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping
    public String listShifts(Model model,
                             @RequestParam(required = false) String date,
                             @RequestParam(defaultValue = "0") int page) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        EmployeeEntity employee = shiftService.getEmployeeFromAuthenticatedUser(username);
        if (employee == null) {
            return "redirect:/employee/dashboard";
        }

        Pageable pageable = PageRequest.of(page, 5);
        Page<ShiftEntity> shifts = shiftService.getShiftsForEmployee(employee, pageable, date);

        List<Map<String, Object>> shiftSummaryByDay = shiftService.getShiftSummaryForMonth(employee);

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
        EmployeeEntity employee = shiftService.getEmployeeFromAuthenticatedUser(username);
        if (employee == null) {
            return List.of();
        }
        return shiftService.getShiftsForCalendar(employee);
    }
}
