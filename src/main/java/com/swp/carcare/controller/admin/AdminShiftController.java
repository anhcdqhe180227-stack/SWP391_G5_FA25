package com.swp.carcare.controller.admin;

import com.swp.carcare.entity.ShiftEntity;
import com.swp.carcare.service.ShiftService;
import com.swp.carcare.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private ShiftService shiftService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/api")
    @ResponseBody
    public List<Map<String, Object>> getShifts() {
        return shiftService.getShifts(null, null, 0).getContent().stream().map(s -> {
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
        String fixedTime = formData.get("fixedTime");
        Integer empId = formData.get("empId") != null && !formData.get("empId").isEmpty()
                ? Integer.valueOf(formData.get("empId")) : null;

        String response = shiftService.saveShift(id, rawDate, fixedTime, empId);
        if (response.equals("Lưu thành công")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public void deleteShift(@PathVariable Integer id) {
        shiftService.deleteShift(id);
    }

    @GetMapping
    public String listShifts(Model model,
                             @RequestParam(required = false) Integer empId,
                             @RequestParam(required = false) String date,
                             @RequestParam(defaultValue = "0") int page) {

        Page<ShiftEntity> shifts = shiftService.getShifts(empId, date, page);

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
