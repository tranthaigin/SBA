package vn.edu.sba301.slot24.orchid;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrchidService {

    private final ConcurrentHashMap<Long, Orchid> orchids = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(2);

    public OrchidService() {
        orchids.put(1L, new Orchid(1L, "Dendrobium", "Demo orchid"));
    }

    public List<Orchid> findAll() {
        return orchids.values().stream()
                .sorted(Comparator.comparing(Orchid::id))
                .toList();
    }

    public Orchid findById(long id) {
        Orchid orchid = orchids.get(id);
        if (orchid == null) {
            throw new ResponseStatusException(NOT_FOUND, "Orchid not found");
        }
        return orchid;
    }

    public Orchid create(OrchidRequest request) {
        long id = nextId.getAndIncrement();
        Orchid orchid = new Orchid(id, request.name(), request.description());
        orchids.put(id, orchid);
        return orchid;
    }

    public Orchid update(long id, OrchidRequest request) {
        if (!orchids.containsKey(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Orchid not found");
        }
        Orchid orchid = new Orchid(id, request.name(), request.description());
        orchids.put(id, orchid);
        return orchid;
    }

    public void delete(long id) {
        if (orchids.remove(id) == null) {
            throw new ResponseStatusException(NOT_FOUND, "Orchid not found");
        }
    }
}
