package internconnect.au.rw.internconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import internconnect.au.rw.internconnect.model.Village;

public interface VillageRepository extends JpaRepository<Village, Long> {

    @Query("select v from Village v join fetch v.cell c join fetch c.sector s join fetch s.district d join fetch d.province where v.id = :id")
    Optional<Village> fetchWithHierarchy(Long id);
}
