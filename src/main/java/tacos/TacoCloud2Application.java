package tacos;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;
import tacos.data.UserRepository;

@SpringBootApplication
public class TacoCloud2Application {

    public static void main(String[] args) {
        SpringApplication.run(TacoCloud2Application.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(
            IngredientRepository ingredientRepo,
            UserRepository userRepo,
            PasswordEncoder encoder,
            TacoRepository tacoRepo) {
        return args -> {
            Ingredient flourTortilla = new Ingredient("FLTO", "Flour Tortilla", Type.WRAP, new BigDecimal("1.50"));
            Ingredient cornTortilla = new Ingredient("COTO", "Corn Tortilla", Type.WRAP, new BigDecimal("1.25"));
            Ingredient groundBeef = new Ingredient("GRBF", "Ground Beef", Type.PROTEIN, new BigDecimal("3.25"));
            Ingredient carnitas = new Ingredient("CARN", "Carnitas", Type.PROTEIN, new BigDecimal("3.50"));
            Ingredient tomatoes = new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES, new BigDecimal("0.80"));
            Ingredient lettuce = new Ingredient("LETC", "Lettuce", Type.VEGGIES, new BigDecimal("0.70"));
            Ingredient cheddar = new Ingredient("CHED", "Cheddar", Type.CHEESE, new BigDecimal("1.10"));
            Ingredient jack = new Ingredient("JACK", "Monterrey Jack", Type.CHEESE, new BigDecimal("1.20"));
            Ingredient salsa = new Ingredient("SLSA", "Salsa", Type.SAUCE, new BigDecimal("0.65"));
            Ingredient sourCream = new Ingredient("SRCR", "Sour Cream", Type.SAUCE, new BigDecimal("0.75"));

            ingredientRepo.save(flourTortilla);
            ingredientRepo.save(cornTortilla);
            ingredientRepo.save(groundBeef);
            ingredientRepo.save(carnitas);
            ingredientRepo.save(tomatoes);
            ingredientRepo.save(lettuce);
            ingredientRepo.save(cheddar);
            ingredientRepo.save(jack);
            ingredientRepo.save(salsa);
            ingredientRepo.save(sourCream);

            if (tacoRepo.count() == 0) {
                Taco taco1 = new Taco();
                taco1.setName("Carnivore");
                taco1.setIngredients(new LinkedHashSet<>(Arrays.asList(
                        flourTortilla, groundBeef, carnitas,
                        sourCream, salsa, cheddar)));
                tacoRepo.save(taco1);

                Taco taco2 = new Taco();
                taco2.setName("Bovine Bounty");
                taco2.setIngredients(new LinkedHashSet<>(Arrays.asList(
                        cornTortilla, groundBeef, cheddar,
                        jack, sourCream)));
                tacoRepo.save(taco2);

                Taco taco3 = new Taco();
                taco3.setName("Veg-Out");
                taco3.setIngredients(new LinkedHashSet<>(Arrays.asList(
                        flourTortilla, cornTortilla, tomatoes,
                        lettuce, salsa)));
                tacoRepo.save(taco3);
            }

            if (!userRepo.existsByUsername("admin")) {
                User admin = new User(
                        "admin",
                        encoder.encode("admin123"),
                        "AdminUser",
                        "Street 1",
                        "Kyiv",
                        "UA",
                        "01001",
                        "1234567890",
                        "ROLE_ADMIN"
                );
                userRepo.save(admin);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
