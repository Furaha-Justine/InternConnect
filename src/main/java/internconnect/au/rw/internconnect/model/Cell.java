package internconnect.au.rw.internconnect.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    private Sector sector;

    @OneToMany(mappedBy = "cell", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Village> villages;
}
