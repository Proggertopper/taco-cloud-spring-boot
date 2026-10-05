package tacos.web.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tacos.Taco;
import tacos.data.TacoRepository;

@RestController
@RequestMapping(path = "/api", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class TacoController {

    private final TacoRepository tacoRepository;

    public TacoController(TacoRepository tacoRepository) {
        this.tacoRepository = tacoRepository;
    }

    @GetMapping
    public Page<Taco> recentTacos() {
        return tacoRepository.findAll(PageRequest.of(0, 12, Sort.by("createdAt").descending()));
    }
}
