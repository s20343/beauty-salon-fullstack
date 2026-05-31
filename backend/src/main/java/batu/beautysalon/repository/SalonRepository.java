package batu.beautysalon.repository;
import batu.beautysalon.model.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SalonRepository extends JpaRepository<Salon, Long> {

    List<Salon> findByDistrictIgnoreCase(String district);
    List<Salon> findByServicesOfferedContainingIgnoreCase(String service);
    List<Salon> findByDistrictIgnoreCaseAndServicesOfferedContainingIgnoreCase(String district, String service);
}