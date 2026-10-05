package tacos.data;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import tacos.OrderStatus;
import tacos.TacoOrder;
import tacos.User;

public interface OrderRepository extends CrudRepository<TacoOrder, String> {
    @EntityGraph(attributePaths = {"tacos", "tacos.ingredients", "user"})
    Optional<TacoOrder> findById(String id);

    @EntityGraph(attributePaths = {"tacos", "tacos.ingredients"})
    List<TacoOrder> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"tacos", "tacos.ingredients", "user"})
    List<TacoOrder> findAllByOrderByPlacedAtDesc();

    @EntityGraph(attributePaths = {"tacos", "tacos.ingredients", "user"})
    List<TacoOrder> findByStatusOrderByPlacedAtDesc(OrderStatus status);
}
