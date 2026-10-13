package fu.se.chapter15api.services;

import fu.se.chapter15api.pojos.Orchid;
import java.util.List;
import java.util.Optional;

public interface IOrchidService {
    List<Orchid> getAllOrchids();

    Optional<Orchid> getOrchidByID(int orchidID);

    Orchid insertOrchid(Orchid orchid);

    Orchid updateOrchid(int orchidID, Orchid orchid);

    void deleteOrchid(int orchidID);
}
