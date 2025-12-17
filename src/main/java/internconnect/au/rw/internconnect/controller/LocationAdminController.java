package internconnect.au.rw.internconnect.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import internconnect.au.rw.internconnect.service.LocationService;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin
public class LocationAdminController {

    private final LocationService service;

    public LocationAdminController(LocationService service) {
        this.service = service;
    }

    @PostMapping("/reload")
    public Map<String, String> reload(@RequestParam(defaultValue = "false") boolean force) {
        String message = service.reload(force);
        return Map.of("message", message);
    }
}


