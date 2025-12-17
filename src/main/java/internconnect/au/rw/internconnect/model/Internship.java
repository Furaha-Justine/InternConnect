package internconnect.au.rw.internconnect.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Data
@NoArgsConstructor
@JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;

    @Column(length = 2000)
    private String description;

    private String locationType; // ONSITE / REMOTE / HYBRID
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private CompanyProfile companyProfile;

    @OneToMany(mappedBy = "internship")
    @JsonIgnore
    private List<Application> applications;
}
