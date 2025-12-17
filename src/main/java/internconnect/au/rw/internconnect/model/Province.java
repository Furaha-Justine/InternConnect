package internconnect.au.rw.internconnect.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;

    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<District> districts;
}
