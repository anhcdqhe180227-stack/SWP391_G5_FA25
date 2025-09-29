package com.swp.carcare.controller.recep;

import com.swp.carcare.dto.OwnerDto;
import com.swp.carcare.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/recep/owners")
public class RecepOwnerController {

    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public String getAllOwners(Model model) {
        model.addAttribute("owners", ownerService.getAllOwnerDtos());
        return "recep/owners/view";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Optional<OwnerDto> optionalOwner = ownerService.getOwnerDtoById(id);

        if (optionalOwner.isPresent()) {
            model.addAttribute("owner", optionalOwner.get());
            return "recep/owners/detail";
        }

        return "redirect:/recep/owners";
    }
}
