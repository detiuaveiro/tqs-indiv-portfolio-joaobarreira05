package pt.zeromonos.garbagecollection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.zeromonos.garbagecollection.domain.BookingStatusHistory;

@Repository
public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, Long> {
}
