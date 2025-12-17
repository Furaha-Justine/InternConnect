package internconnect.au.rw.internconnect.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import internconnect.au.rw.internconnect.model.Province;
import internconnect.au.rw.internconnect.repository.ProvinceRepository;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin
public class LocationController {

    private final ProvinceRepository provinceRepo;

    public LocationController(ProvinceRepository provinceRepo) {
        this.provinceRepo = provinceRepo;
    }

    @GetMapping("/provinces")
    public List<Province> provinces() {
        return provinceRepo.findAll();
    }

    @GetMapping("/provinces/{id}")
    public Province province(@PathVariable Long id) {
        return provinceRepo.findById(id).orElse(null);
    }
}
