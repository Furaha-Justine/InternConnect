package internconnect.au.rw.internconnect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    java.util.Optional<User> findByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("""
      select u from User u
      where u.village.cell.sector.district.province.name = :provinceName
    """)
    Page<User> findByProvinceName(String provinceName, Pageable pageable);

    @Query("""
      select u from User u
      where u.village.cell.sector.district.province.code = :provinceCode
    """)
    Page<User> findByProvinceCode(String provinceCode, Pageable pageable);
}
