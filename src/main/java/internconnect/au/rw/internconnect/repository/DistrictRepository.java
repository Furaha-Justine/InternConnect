package internconnect.au.rw.internconnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import internconnect.au.rw.internconnect.model.District;

public interface DistrictRepository extends JpaRepository<District, Long> {

    List<District> findByProvince_Name(String provinceName);
}
