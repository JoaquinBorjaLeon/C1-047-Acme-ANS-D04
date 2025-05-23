
package acme.features.flightcrewmember.activitylog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.LegStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		int memberId;
		boolean isLegLanded;

		masterId = super.getRequest().getData("masterId", int.class);
		memberId = this.repository.findFlightAssignmentById(masterId).getAllocatedFlightCrewMember().getId();
		isLegLanded = this.repository.findFlightAssignmentById(masterId).getLeg().equals(LegStatus.LANDED);

		status = memberId == super.getRequest().getPrincipal().getActiveRealm().getId() && !isLegLanded;
		super.getResponse().setAuthorised(status);

	}
	@Override
	public void load() {

		ActivityLog activityLog;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("masterId", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);

		activityLog = new ActivityLog();
		activityLog.setFlightAssignment(flightAssignment);
		activityLog.setDraftMode(true);
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(activityLog);

	}
	@Override
	public void bind(final ActivityLog activityLog) {

		super.bindObject(activityLog, "incidentType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		if (this.getBuffer().getErrors().hasErrors("incidentType"))
			super.state(activityLog.getIncidentType() != null, "incidentType", "acme.validation.member.assignment.incidentType.message");

		if (this.getBuffer().getErrors().hasErrors("description"))
			super.state(activityLog.getDescription() != null, "description", "acme.validation.member.assignment.description.message");

		if (this.getBuffer().getErrors().hasErrors("severityLevel"))
			super.state(activityLog.getSeverityLevel() != null, "severityLevel", "acme.validation.member.assignment.severityLevel.message");

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		assert activityLog != null;
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset = null;

		List<FlightAssignment> assignments;
		assignments = this.repository.findAllFlightAssignments();

		SelectChoices assignmentChoices;
		assignmentChoices = SelectChoices.from(assignments, "description", activityLog.getFlightAssignment());

		dataset = super.unbindObject(activityLog, "registrationMoment", "incidentType", "description", "severityLevel", "draftMode", "flightAssignment");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("assignmentChoices", assignmentChoices);

		super.getResponse().addData(dataset);
	}
}
