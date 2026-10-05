package tacos;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.CreditCardNumber;



@Data
@Entity
@Table(name = "taco_order")
public class TacoOrder implements Serializable {
    @NotBlank(message = "Delivery name is required")
    @Column(name = "delivery_name")
    private String deliveryName;

    @NotBlank(message = "Street is required")
    @Column(name = "delivery_street")
    private String deliveryStreet;

    @NotBlank(message = "City is required")
    @Column(name = "delivery_city")
    private String deliveryCity;

    @NotBlank(message = "State is required")
    @Column(name = "delivery_state")
    private String deliveryState;

    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "^[0-9A-Za-z -]{3,12}$", message = "ZIP code must be 3-12 letters or digits")
    @Column(name = "delivery_zip")
    private String deliveryZip;

    @NotBlank(message = "Credit card number is required")
    @CreditCardNumber(message = "Not a valid credit card number")
    @Column(name = "cc_number")
    private String ccNumber;

    @NotBlank(message = "Expiration is required")
    @Pattern(regexp ="^(0[1-9]|1[0-2])/[0-9]{2}$", message="Must be formatted MM/YY")
    @Column(name = "cc_expiration")
    private String ccExpiration;

    @NotBlank(message = "CVV is required")
    @Digits(integer=3, fraction=0, message="Invalid CVV")
    @Column(name = "cc_cvv")
    private String ccCVV;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


    private final static long serialVersionUID = 1L;

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "placed_at")
    private Date placedAt = new Date() ;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    @Size(min = 1, message = "You must add at least one taco to the order")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "taco_order_tacos",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "taco_id"))
    private Set<Taco> tacos = new LinkedHashSet<>();


    public void addTaco(Taco taco) {
        this.tacos.add(taco);
    }

    @Transient
    public BigDecimal getTotal() {
        return tacos.stream()
                .map(Taco::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
