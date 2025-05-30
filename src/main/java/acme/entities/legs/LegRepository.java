
package acme.entities.legs;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l")
	List<Leg> findAllLegs();

	@Query("select l from Leg l where l.flightNumber = :flightNumber")
	public Leg getLegFromFlightNumber(String flightNumber);

	@Query("select case when count(l) > 0 then true else false end from Leg l where l.id != :legId and l.flight.id = :flightId and (l.scheduledDeparture <= :scheduledArrival and l.scheduledArrival >= :scheduledDeparture)")
	public boolean isLegOverlapping(Integer legId, Integer flightId, Date scheduledDeparture, Date scheduledArrival);

}
