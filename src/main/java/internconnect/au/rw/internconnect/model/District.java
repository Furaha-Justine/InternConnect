package internconnect.au.rw.internconnect.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    private Province province;

    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Sector> sectors;
}
