package tacos;


import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;



@Data
@Entity
@Table(name = "ingredient")
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PROTECTED, force=true)

public class Ingredient {

    @Id
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private BigDecimal price;

    public enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
