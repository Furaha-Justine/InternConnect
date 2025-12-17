package internconnect.au.rw.internconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import internconnect.au.rw.internconnect.repository.VillageRepository;
import internconnect.au.rw.internconnect.model.Village;
import org.springframework.stereotype.Service;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final VillageRepository villageRepo;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder, VillageRepository villageRepo) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.villageRepo = villageRepo;
    }

    public Page<User> listAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public User create(User u) {
        return repo.save(u);
    }

    public User get(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public java.util.Map<String, Object> loadLocationFor(User user) {
        if (user == null || user.getVillage() == null || user.getVillage().getId() == null) {
            return java.util.Map.of();
        }
        var opt = villageRepo.fetchWithHierarchy(user.getVillage().getId());
        var v = opt.orElse(null);
        if (v == null) {
            return java.util.Map.of();
        }
        var c = v.getCell();
        var s = c != null ? c.getSector() : null;
        var d = s != null ? s.getDistrict() : null;
        var p = d != null ? d.getProvince() : null;
        
       
        java.util.Map<String, Object> villageMap = java.util.Map.of(
            "id", v.getId(),
            "name", v.getName() != null ? v.getName() : ""
        );
        
        java.util.Map<String, Object> cellMap = c != null ? java.util.Map.of(
            "id", c.getId(),
            "name", c.getName() != null ? c.getName() : ""
        ) : java.util.Map.of();
        
        java.util.Map<String, Object> sectorMap = s != null ? java.util.Map.of(
            "id", s.getId(),
            "name", s.getName() != null ? s.getName() : ""
        ) : java.util.Map.of();
        
        java.util.Map<String, Object> districtMap = d != null ? java.util.Map.of(
            "id", d.getId(),
            "name", d.getName() != null ? d.getName() : ""
        ) : java.util.Map.of();
        
        java.util.Map<String, Object> provinceMap = p != null ? java.util.Map.of(
            "id", p.getId(),
            "code", p.getCode() != null ? p.getCode() : "",
            "name", p.getName() != null ? p.getName() : ""
        ) : java.util.Map.of();
        
        return java.util.Map.of(
            "village", villageMap,
            "cell", cellMap,
            "sector", sectorMap,
            "district", districtMap,
            "province", provinceMap
        );
    }

    public User update(UUID id, User u) {
        User existing = repo.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setFirstName(u.getFirstName());
        existing.setLastName(u.getLastName());
        existing.setEmail(u.getEmail());
        existing.setPhone(u.getPhone());
        existing.setRole(u.getRole());
        if (u.getVillage() != null) {
            Village v = null;
            try {
               
                if (u.getVillage().getId() != null) {
                    v = villageRepo.findById(u.getVillage().getId()).orElse(null);
                }
            } catch (Exception ignored) {}
            existing.setVillage(v);
        }
        if (u.getPassword() != null && !u.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(u.getPassword()));
        }
        return repo.save(existing);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public Page<User> byProvinceName(String name, Pageable pageable) {
        return repo.findByProvinceName(name, pageable);
    }

    public Page<User> byProvinceCode(String code, Pageable pageable) {
        return repo.findByProvinceCode(code, pageable);
    }

    public Page<User> byRole(Role role, Pageable pageable) {
        return repo.findByRole(role, pageable);
    }
}
