package tacos;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;

@Data
@Entity
@Table(name = "taco")
@RestResource(rel = "tacos" , path = "tacos")
public class Taco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;
    private Date createdAt = new Date();
    @Size(min=1, message="You must choose at least 1 ingredient")
    @ManyToMany
    @JoinTable(
            name = "taco_ingredients",
            joinColumns = @JoinColumn(name = "taco_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> ingredients = new LinkedHashSet<>();
    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

    @Transient
    public BigDecimal getPrice() {
        return ingredients.stream()
                .map(Ingredient::getPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
