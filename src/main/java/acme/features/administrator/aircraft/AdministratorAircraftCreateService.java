
package acme.features.administrator.aircraft;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.airline.Airline;

@GuiService
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		boolean status = true;
		String metodo = super.getRequest().getMethod();

		if (super.getRequest().hasData("id")) {
			int id = super.getRequest().getData("id", int.class);
			if (id != 0)
				status = false;
		}

		if ("POST".equalsIgnoreCase(metodo)) {
			int airlineId = super.getRequest().getData("airline", int.class);
			String aStatus = super.getRequest().getData("status", String.class);

			boolean isValidStatus = aStatus != null && !aStatus.trim().isEmpty() && Arrays.stream(AircraftStatus.values()).anyMatch(s -> s.name().equals(aStatus));
			if (isValidStatus = false)
				status = false;

			Airline airline = this.repository.findAirlineById(airlineId);
			boolean airlineExists = airline != null && this.repository.findAllAirlines().contains(airline);
			if (airlineExists = false)
				status = false;
		}

		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {

		Aircraft aircraft;
		Boolean draftMode = true;
		aircraft = new Aircraft();
		aircraft.setDraftMode(draftMode);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		super.bindObject(aircraft, "model", "regNumber", "capacity", "cargoWeight", "status", "notes", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);

		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		SelectChoices choicesStatuses;
		SelectChoices choicesAirlines;
		Collection<Airline> airlines;

		Dataset dataset;

		choicesStatuses = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());
		airlines = this.repository.findAllAirlines();
		choicesAirlines = SelectChoices.from(airlines, "name", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "regNumber", "capacity", "cargoWeight", "status", "notes");
		dataset.put("statuses", choicesStatuses);
		dataset.put("airlines", choicesAirlines);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}
}
