package com.swp.carcare.controller.admin;


import com.swp.carcare.entity.ShiftEntity;
import com.swp.carcare.repository.EmployeeRepository;
import com.swp.carcare.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/shift")
public class AdminShiftController {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/api")
    @ResponseBody
    public List<Map<String, Object>> getShifts() {
        return shiftRepository.findAll().stream().map(s -> {
            Map<String, Object> ev = new HashMap<>();
            ev.put("id", s.getId());
            
            String personInfo = "";
            if (s.getEmployee() != null) {
                personInfo = s.getEmployee().getFullName();
            }
            
            ev.put("title", personInfo);
            
            ev.put("start", s.getStartTime().toString());
            ev.put("end", s.getEndTime().toString());
            ev.put("fixedTime", getFixedTimeFromStartTime(s.getStartTime()));
            ev.put("empId", s.getEmployee() != null ? s.getEmployee().getId() : null);
            return ev;
        }).collect(Collectors.toList());
    }

    @PostMapping("/api/save")
    public ResponseEntity<?> saveShift(@RequestParam Map<String, String> formData,
                            RedirectAttributes redirectAttributes) {

        Integer id = formData.get("id") != null && !formData.get("id").isEmpty()
                ? Integer.valueOf(formData.get("id")) : null;

        String rawDate = formData.get("date");
        String onlyDate = rawDate.split("T")[0];
        LocalDate date = LocalDate.parse(onlyDate);

        String fixedTime = formData.get("fixedTime");
        LocalTime startTime = fixedTime.equals("MORNING") ? LocalTime.of(7, 0) : LocalTime.of(13, 0);
        LocalTime endTime = fixedTime.equals("MORNING") ? LocalTime.of(11, 0) : LocalTime.of(17, 0);

        LocalDateTime shiftStart = LocalDateTime.of(date, startTime);
        LocalDateTime shiftEnd = LocalDateTime.of(date, endTime);

        Integer empId = formData.get("empId") != null && !formData.get("empId").isEmpty()
                ? Integer.valueOf(formData.get("empId")) : null;


        List<ShiftEntity> conflicts = shiftRepository.findConflictingShifts(empId, shiftStart, shiftEnd, id);
        if (!conflicts.isEmpty()) {
            return ResponseEntity.badRequest().body("Đã tồn tại ca làm trùng thời gian cho nhân viên");
        }


        ShiftEntity shift = id != null ? shiftRepository.findById(id).orElse(new ShiftEntity()) : new ShiftEntity();
        shift.setStartTime(shiftStart);
        shift.setEndTime(shiftEnd);

        if (empId != null) {
            shift.setEmployee(employeeRepository.findById(empId).orElse(null));
        } else {
            shift.setEmployee(null);
        }

        shiftRepository.save(shift);
        return ResponseEntity.ok("Lưu thành công");
    }


    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public void deleteShift(@PathVariable Integer id) {
        shiftRepository.deleteById(id);
    }

    @GetMapping
    public String listShifts(Model model,
                             @RequestParam(required = false) Integer empId,
                             @RequestParam(required = false) String date,
                             @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("startTime").descending());
        Page<ShiftEntity> shifts;

        if (empId != null) {
            shifts = shiftRepository.findByEmployeeId(empId, pageable);
        } else if (date != null && !date.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(23, 59, 59);
            shifts = shiftRepository.findByStartTimeBetween(startOfDay, endOfDay, pageable);
        } else {
            shifts = shiftRepository.findAll(pageable);
        }

        model.addAttribute("shifts", shifts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shifts.getTotalPages());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("selectedDoctorId", empId);
        model.addAttribute("selectedDate", date);

        return "admin/shift/view";
    }


    private String getFixedTimeFromStartTime(LocalDateTime startTime) {
        if (startTime.toLocalTime().equals(LocalTime.of(7, 0))) {
            return "MORNING";
        } else if (startTime.toLocalTime().equals(LocalTime.of(13, 0))) {
            return "AFTERNOON";
        }
        return "UNKNOWN";
    }
}