package pt.uminho.ceb.biosystems.merlin.merlin_addons.compartments;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.aibench.workbench.Workbench;
import pt.uminho.ceb.biosystems.merlin.gui.datatypes.WorkspaceAIB;
import pt.uminho.ceb.biosystems.merlin.gui.utilities.AIBenchUtils;
import pt.uminho.ceb.biosystems.merlin.core.containers.model.CompartmentContainer;
import pt.uminho.ceb.biosystems.merlin.services.model.ModelCompartmentServices;

@Operation(name="Merge Compartments", description = "Merge Compartments")
public class MergeCompartments {

	private String compartment1;
	private String compartment2;
	private WorkspaceAIB workspace;

	@Port(direction=Direction.INPUT, name="", order=1)
	public void setWorkspace (String workspace){

		this.workspace = AIBenchUtils.getProject(workspace);
	}

	@Port(direction=Direction.INPUT, name="compartment to replace", order=2)
	public void set1 (String compartment1){

		this.compartment1 = compartment1;
	}

	@Port(direction=Direction.INPUT, name="compartment to keep", order=3)
	public void set2 (String compartment2) throws Exception{

		this.compartment2 = compartment2;

		if (this.compartment1.equals(compartment2))
			Workbench.getInstance().error("Please select different compartments!");
		else {

			String workspaceName = this.workspace.getName();

			CompartmentContainer container1 = ModelCompartmentServices.getCompartmentByName(
					workspaceName, this.compartment1);
			CompartmentContainer container2 = ModelCompartmentServices.getCompartmentByName(
					workspaceName, this.compartment2);

			ModelCompartmentServices.mergeCompartments(workspaceName, 
					container2.getCompartmentID(), container1.getCompartmentID());

			ModelCompartmentServices.deleteCompartmentById(workspaceName, container1.getCompartmentID());
			
			Workbench.getInstance().info("process complete! all entries of the compartment '" + container1.getName() + "' were replaced by '" + container2.getName() + "'");
		}
	}


}
