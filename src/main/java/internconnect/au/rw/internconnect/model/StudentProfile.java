package internconnect.au.rw.internconnect.model;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Data
@NoArgsConstructor
@JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private User user;

    private String university;
    private String major;
    private Integer graduationYear;
    private String cvUrl;

    @ManyToMany
    @JoinTable(
        name = "student_profile_skill",
        joinColumns = @JoinColumn(name = "student_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;
}
