package internconnect.au.rw.internconnect.service;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.model.Village;
import internconnect.au.rw.internconnect.repository.UserRepository;
import internconnect.au.rw.internconnect.repository.VillageRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for user management operations.
 */
@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  private static final String ID_KEY = "id";
  private static final String NAME_KEY = "name";
  private static final String CODE_KEY = "code";
  private static final String VILLAGE_KEY = "village";
  private static final String CELL_KEY = "cell";
  private static final String SECTOR_KEY = "sector";
  private static final String DISTRICT_KEY = "district";
  private static final String PROVINCE_KEY = "province";
  private static final String EMPTY_STRING = "";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final VillageRepository villageRepository;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      VillageRepository villageRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.villageRepository = villageRepository;
  }

  public Page<User> listAll(Pageable pageable) {
    logger.debug("Listing all users");
    return userRepository.findAll(pageable);
  }

  public User create(User user) {
    logger.debug("Creating user: {}", user.getEmail());
    return userRepository.save(user);
  }

  public User get(UUID id) {
    logger.debug("Fetching user with ID: {}", id);
    return userRepository.findById(id).orElse(null);
  }

  public User findByEmail(String email) {
    logger.debug("Finding user by email: {}", email);
    return userRepository.findByEmail(email).orElse(null);
  }

  /**
   * Loads the location hierarchy for a user (village, cell, sector, district, province).
   *
   * @param user the user whose location to load
   * @return map containing location hierarchy
   */
  public Map<String, Object> loadLocationFor(User user) {
    if (user == null || user.getVillage() == null || user.getVillage().getId() == null) {
      logger.debug("User or village is null, returning empty location map");
      return Map.of();
    }
    var villageOptional = villageRepository.fetchWithHierarchy(user.getVillage().getId());
    var village = villageOptional.orElse(null);
    if (village == null) {
      logger.debug("Village not found for user: {}", user.getId());
      return Map.of();
    }
    var cell = village.getCell();
    var sector = cell != null ? cell.getSector() : null;
    var district = sector != null ? sector.getDistrict() : null;
    var province = district != null ? district.getProvince() : null;
    Map<String, Object> locationMap = new HashMap<>();
    locationMap.put(VILLAGE_KEY, createLocationMap(village.getId(), village.getName()));
    locationMap.put(CELL_KEY, cell != null ? createLocationMap(cell.getId(), cell.getName())
        : Map.of());
    locationMap.put(SECTOR_KEY, sector != null ? createLocationMap(sector.getId(), sector.getName())
        : Map.of());
    locationMap.put(DISTRICT_KEY,
        district != null ? createLocationMap(district.getId(), district.getName()) : Map.of());
    locationMap.put(PROVINCE_KEY, province != null
        ? createProvinceMap(province.getId(), province.getCode(), province.getName())
        : Map.of());
    return locationMap;
  }

  private Map<String, Object> createLocationMap(Long id, String name) {
    Map<String, Object> map = new HashMap<>();
    map.put(ID_KEY, id);
    map.put(NAME_KEY, name != null ? name : EMPTY_STRING);
    return map;
  }

  private Map<String, Object> createProvinceMap(Long id, String code, String name) {
    Map<String, Object> map = new HashMap<>();
    map.put(ID_KEY, id);
    map.put(CODE_KEY, code != null ? code : EMPTY_STRING);
    map.put(NAME_KEY, name != null ? name : EMPTY_STRING);
    return map;
  }

  public User update(UUID id, User user) {
    logger.debug("Updating user with ID: {}", id);
    User existingUser = userRepository.findById(id).orElse(null);
    if (existingUser == null) {
      logger.warn("User not found for update: {}", id);
      return null;
    }
    existingUser.setFirstName(user.getFirstName());
    existingUser.setLastName(user.getLastName());
    existingUser.setEmail(user.getEmail());
    existingUser.setPhone(user.getPhone());
    existingUser.setRole(user.getRole());
    if (user.getVillage() != null) {
      Village village = null;
      try {
        if (user.getVillage().getId() != null) {
          village = villageRepository.findById(user.getVillage().getId()).orElse(null);
        }
      } catch (Exception exception) {
        logger.warn("Error fetching village: {}", exception.getMessage());
      }
      existingUser.setVillage(village);
    }
    if (user.getPassword() != null && !user.getPassword().isBlank()) {
      existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepository.save(existingUser);
  }

  public void delete(UUID id) {
    logger.debug("Deleting user with ID: {}", id);
    userRepository.deleteById(id);
  }

  public Page<User> findByProvinceName(String name, Pageable pageable) {
    logger.debug("Finding users by province name: {}", name);
    return userRepository.findByProvinceName(name, pageable);
  }

  public Page<User> findByProvinceCode(String code, Pageable pageable) {
    logger.debug("Finding users by province code: {}", code);
    return userRepository.findByProvinceCode(code, pageable);
  }

  public Page<User> findByRole(Role role, Pageable pageable) {
    logger.debug("Finding users by role: {}", role);
    return userRepository.findByRole(role, pageable);
  }
}
