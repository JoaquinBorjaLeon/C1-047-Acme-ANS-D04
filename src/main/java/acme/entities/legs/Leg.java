
package acme.entities.legs;

import java.time.Duration;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.helpers.MomentHelper;
import acme.constraints.ValidFlightNumber;
import acme.constraints.ValidLeg;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "flightNumber", unique = true), @Index(columnList = "flight_id, scheduledDeparture, scheduledArrival"), // 
	@Index(columnList = "scheduledDeparture"), @Index(columnList = "flight_id"), @Index(columnList = "aircraft_id"),//
	@Index(columnList = "status"), @Index(columnList = "scheduledArrival") //Esta ultima linea de indices ha sido incluida para el S03
})
@ValidLeg
public class Leg extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidFlightNumber
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport				departureAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport				arrivalAirport;

	@Mandatory
	@Valid
	@Automapped
	private LegStatus			status;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft			aircraft;

	@Mandatory
	@Valid
	@ManyToOne
	private Flight				flight;

	@Mandatory
	@Automapped
	private boolean				draftMode;


	@Transient
	public int durationInHours() {
		if (this.scheduledDeparture != null && this.scheduledArrival != null) {
			Duration duration = MomentHelper.computeDuration(this.scheduledDeparture, this.scheduledArrival);
			return (int) duration.toHours();
		}
		return 0;
	}

	@Transient
	public String getDescription() {
		String res = "";
		if (this.scheduledDeparture != null && this.scheduledArrival != null)
			res = String.format("%s %s -> %s %s", this.scheduledDeparture.toLocaleString(), this.departureAirport.getCity(), this.scheduledArrival.toLocaleString(), this.arrivalAirport.getCity());
		return res;
	}
}
