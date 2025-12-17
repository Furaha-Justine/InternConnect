package internconnect.au.rw.internconnect.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private StudentProfile studentProfile;

    @ManyToOne
    private Internship internship;

    private String status; // APPLIED / ACCEPTED / REJECTED

    private Instant appliedAt;
}
