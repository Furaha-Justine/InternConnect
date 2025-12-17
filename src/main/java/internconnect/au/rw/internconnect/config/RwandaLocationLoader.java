package internconnect.au.rw.internconnect.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import internconnect.au.rw.internconnect.model.Cell;
import internconnect.au.rw.internconnect.model.District;
import internconnect.au.rw.internconnect.model.Province;
import internconnect.au.rw.internconnect.model.Sector;
import internconnect.au.rw.internconnect.model.Village;
import internconnect.au.rw.internconnect.repository.DistrictRepository;
import internconnect.au.rw.internconnect.repository.ProvinceRepository;
import internconnect.au.rw.internconnect.service.LocationService;

@Configuration
public class RwandaLocationLoader {

	@Bean
	CommandLineRunner loadLocations(ProvinceRepository provinceRepo, LocationService locationService) {
		return args -> {
			if (provinceRepo.count() > 0) {
				
				return;
			}
			String msg = locationService.reload(false);
			System.out.println(msg);
		};
	}
}
