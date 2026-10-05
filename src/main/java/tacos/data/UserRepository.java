package tacos.data;
import org.springframework.data.repository.CrudRepository;

import tacos.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
