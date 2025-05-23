package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.tasks.Task;
import acme.entities.tasks.TaskType;
import acme.features.technician.maintenanceRecordTask.TechnicianMaintenanceRecordTaskRepository;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskDeleteService extends AbstractGuiService<Technician, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;
	
	@Autowired
	private TechnicianMaintenanceRecordTaskRepository mrtRepository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean exist, published;
		Task task;
		Technician technician;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findById(id);

		exist = task != null;
		if (exist) {
			published = !task.getDraftMode();
			technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
			if (!published && technician.equals(task.getTechnician()))
				super.getResponse().setAuthorised(true);
		}
	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findById(id);

		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {
		super.bindObject(task, "type", "description", "priority", "duration");
	}

	@Override
	public void validate(final Task task) {
	}

	@Override
	public void perform(final Task task) {
		this.mrtRepository.deleteAll(this.repository.findMaintenanceRecordTasksByTaskId(task.getId()));
		this.repository.delete(task);
	}

	@Override
	public void unbind(final Task task) {
		SelectChoices choices;

		Dataset dataset;
		choices = SelectChoices.from(TaskType.class, task.getType());

		dataset = super.unbindObject(task, "type", "description", "priority", "duration", "draftMode");
		dataset.put("type", choices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}
}