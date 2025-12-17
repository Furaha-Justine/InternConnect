package internconnect.au.rw.internconnect.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import internconnect.au.rw.internconnect.model.Cell;
import internconnect.au.rw.internconnect.model.District;
import internconnect.au.rw.internconnect.model.Province;
import internconnect.au.rw.internconnect.model.Sector;
import internconnect.au.rw.internconnect.model.Village;
import internconnect.au.rw.internconnect.repository.CellRepository;
import internconnect.au.rw.internconnect.repository.DistrictRepository;
import internconnect.au.rw.internconnect.repository.ProvinceRepository;
import internconnect.au.rw.internconnect.repository.SectorRepository;
import internconnect.au.rw.internconnect.repository.VillageRepository;

@Service
public class LocationService {

    private final ProvinceRepository provinceRepo;
    private final DistrictRepository districtRepo;
    private final SectorRepository sectorRepo;
    private final CellRepository cellRepo;
    private final VillageRepository villageRepo;

    public LocationService(ProvinceRepository provinceRepo,
            DistrictRepository districtRepo,
            SectorRepository sectorRepo,
            CellRepository cellRepo,
            VillageRepository villageRepo) {
        this.provinceRepo = provinceRepo;
        this.districtRepo = districtRepo;
        this.sectorRepo = sectorRepo;
        this.cellRepo = cellRepo;
        this.villageRepo = villageRepo;
    }

    @Transactional
    public String reload(boolean force) {
        try {
            if (!force && provinceRepo.count() > 0) {
                return "Locations already present. Use force=true to reload.";
            }

            if (force) {
               
                villageRepo.deleteAll();
                cellRepo.deleteAll();
                sectorRepo.deleteAll();
                districtRepo.deleteAll();
                provinceRepo.deleteAll();
            }

            ClassPathResource resource = new ClassPathResource("locations/rwanda-locations.json");
            if (!resource.exists()) {
                return "rwanda-locations.json not found at classpath: locations/rwanda-locations.json";
            }

            ObjectMapper mapper = new ObjectMapper();
            try (InputStream is = resource.getInputStream()) {
                JsonNode root = mapper.readTree(is);
                if (!root.isArray()) {
                    return "Invalid JSON format: expected array at root";
                }

                boolean isFlat = root.size() > 0 && root.get(0).has("province_name");

                if (!isFlat) {
                    for (JsonNode provNode : root) {
                        String provinceName = provNode.path("province").asText(null);
                        String provinceCode = provNode.path("code").asText(null);
                        if (provinceName == null) {
                            continue;
                        }

                        Province province = new Province();
                        province.setName(provinceName);
                        province.setCode(provinceCode == null ? provinceName : provinceCode);
                        province = provinceRepo.save(province);

                        JsonNode districtsNode = provNode.path("districts");
                        if (districtsNode.isArray()) {
                            List<District> savedDistricts = new ArrayList<>();
                            for (JsonNode dNode : districtsNode) {
                                String districtName = dNode.path("name").asText(null);
                                if (districtName == null) {
                                    continue;
                                }
                                District district = new District();
                                district.setName(districtName);
                                district.setProvince(province);
                                district = districtRepo.save(district);

                                JsonNode sectorsNode = dNode.path("sectors");
                                if (sectorsNode.isArray()) {
                                    for (JsonNode sNode : sectorsNode) {
                                        String sectorName = sNode.path("name").asText(null);
                                        if (sectorName == null) {
                                            continue;
                                        }
                                        Sector sector = new Sector();
                                        sector.setName(sectorName);
                                        sector.setDistrict(district);
                                        sector = sectorRepo.save(sector);

                                        JsonNode cellsNode = sNode.path("cells");
                                        if (cellsNode.isArray()) {
                                            for (JsonNode cNode : cellsNode) {
                                                String cellName = cNode.path("name").asText(null);
                                                if (cellName == null) {
                                                    continue;
                                                }
                                                Cell cell = new Cell();
                                                cell.setName(cellName);
                                                cell.setSector(sector);
                                                cell = cellRepo.save(cell);

                                                JsonNode villagesNode = cNode.path("villages");
                                                if (villagesNode.isArray()) {
                                                    for (JsonNode vNode : villagesNode) {
                                                        String villageName = vNode.asText(null);
                                                        if (villageName == null) {
                                                            continue;
                                                        }
                                                        Village village = new Village();
                                                        village.setName(villageName);
                                                        village.setCell(cell);
                                                        villageRepo.save(village);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                savedDistricts.add(district);
                            }
                        }
                    }
                } else {
                   
                    Map<String, Province> codeToProvince = new HashMap<>();
                    Map<String, District> codeToDistrict = new HashMap<>();
                    Map<String, Sector> codeToSector = new HashMap<>();
                    Map<String, Cell> codeToCell = new HashMap<>();
                    Map<String, Village> codeToVillage = new HashMap<>();

                    for (JsonNode row : root) {
                        String pCode = row.path("province_code").asText(null);
                        String pName = row.path("province_name").asText(null);
                        String dCode = row.path("district_code").asText(null);
                        String dName = row.path("district_name").asText(null);
                        String sCode = row.path("sector_code").asText(null);
                        String sName = row.path("sector_name").asText(null);
                        String cCode = row.path("cell_code").asText(null);
                        String cName = row.path("cell_name").asText(null);
                        String vCode = row.path("village_code").asText(null);
                        String vName = row.path("village_name").asText(null);

                        if (pName == null) {
                            continue;
                        }

                        Province province = codeToProvince.get(pCode);
                        if (province == null) {
                            province = new Province();
                            province.setCode(pCode == null ? pName : String.valueOf(pCode));
                            province.setName(pName);
                            province = provinceRepo.save(province);
                            codeToProvince.put(province.getCode(), province);
                        }

                        if (dName != null) {
                            String dKey = String.valueOf(dCode);
                            District district = codeToDistrict.get(dKey);
                            if (district == null) {
                                district = new District();
                                district.setName(dName);
                                district.setProvince(province);
                                district = districtRepo.save(district);
                                codeToDistrict.put(dKey, district);
                            }

                            if (sName != null) {
                                String sKey = String.valueOf(sCode);
                                Sector sector = codeToSector.get(sKey);
                                if (sector == null) {
                                    sector = new Sector();
                                    sector.setName(sName);
                                    sector.setDistrict(district);
                                    sector = sectorRepo.save(sector);
                                    codeToSector.put(sKey, sector);
                                }

                                if (cName != null) {
                                    String cKey = String.valueOf(cCode);
                                    Cell cell = codeToCell.get(cKey);
                                    if (cell == null) {
                                        cell = new Cell();
                                        cell.setName(cName);
                                        cell.setSector(sector);
                                        cell = cellRepo.save(cell);
                                        codeToCell.put(cKey, cell);
                                    }

                                    if (vName != null) {
                                        String vKey = String.valueOf(vCode);
                                        if (!codeToVillage.containsKey(vKey)) {
                                            Village village = new Village();
                                            village.setName(vName);
                                            village.setCell(cell);
                                            village = villageRepo.save(village);
                                            codeToVillage.put(vKey, village);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return "Rwanda locations loaded successfully.";
        } catch (Exception ex) {
            return "Error while loading locations: " + ex.getMessage();
        }
    }
}


