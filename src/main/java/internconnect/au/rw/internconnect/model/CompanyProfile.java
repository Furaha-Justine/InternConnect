package internconnect.au.rw.internconnect.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import jakarta.persistence.OneToOne;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private User user;

    private String companyName;
    private String industry;
    private String website;
}
