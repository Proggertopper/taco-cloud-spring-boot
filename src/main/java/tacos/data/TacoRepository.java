package tacos.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import tacos.Taco;
import tacos.TacoOrder;

public interface TacoRepository extends JpaRepository<Taco,Long> {
    @Override
    Page<Taco> findAll(Pageable pageable);

}
