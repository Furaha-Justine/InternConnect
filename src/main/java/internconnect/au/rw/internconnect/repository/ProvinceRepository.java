package internconnect.au.rw.internconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import internconnect.au.rw.internconnect.model.Province;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    Optional<Province> findByCode(String code);

    Optional<Province> findByName(String name);
}
