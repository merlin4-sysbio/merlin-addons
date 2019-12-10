package pt.uminho.ceb.biosystems.merlin.merlin_addons.compartments;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.sing_group.gc4s.dialog.AbstractInputJDialog;
import org.sing_group.gc4s.input.InputParameter;
import org.sing_group.gc4s.input.InputParametersPanel;
import org.sing_group.gc4s.input.combobox.ExtendedJComboBox;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.clipboard.ClipboardItem;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.aibench.workbench.Workbench;
import pt.uminho.ceb.biosystems.merlin.gui.datatypes.WorkspaceAIB;
import pt.uminho.ceb.biosystems.merlin.gui.jpanels.CustomGUI;
import pt.uminho.ceb.biosystems.merlin.gui.utilities.CreateImageIcon;
import pt.uminho.ceb.biosystems.merlin.services.model.ModelCompartmentServices;

public class MergeCompartmentsGUI extends AbstractInputJDialog implements InputGUI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1360571549990266018L;
	private ParamsReceiver rec;
	private ExtendedJComboBox<String> compartment1;
	private ExtendedJComboBox<String> compartment2;
	private String[] workspaces;
	private ExtendedJComboBox<String> workspace;

	public MergeCompartmentsGUI() {
		super(new JFrame());
	}
	
	

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		this.rec = arg0;
		this.setTitle(arg1.getName());
		this.setVisible(true);		
	}

	@Override
	public void onValidationError(Throwable t) {
		Workbench.getInstance().error(t);		
	}

	@Override
	public void finish() {
		
	}

	@Override
	public String getDescription() {
		return "Merge Compartments";
	}

	@Override
	public String getDialogTitle() {
		return "Merge Compartments";
	}
	
	@Override
	public Component getButtonsPane() {
		final JPanel buttonsPanel = new JPanel(new FlowLayout());

		okButton = new JButton("proceed");
		okButton.setEnabled(true);
		okButton.setToolTipText("proceed");
		okButton.setIcon(new CreateImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/Ok.png")),0.1).resizeImageIcon());
		ActionListener listener= new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				
				rec.paramsIntroduced(
						new ParamSpec[]{
								new ParamSpec("Workspace", WorkspaceAIB.class,workspace.getSelectedItem().toString(),null),
								new ParamSpec("Compartments 1", String.class,compartment1.getSelectedItem().toString(),null),
								new ParamSpec("Compartments 2", String.class,compartment2.getSelectedItem().toString(),null)
						}
						);
				
			}
		};
		okButton.addActionListener(listener);

		cancelButton = new JButton("cancel");
		cancelButton.setToolTipText("cancel");
		cancelButton.setIcon(new CreateImageIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/Cancel.png")),0.1).resizeImageIcon());
		cancelButton.addActionListener(event -> {

			String[] options = new String[2];
			options[0] = "yes";
			options[1] = "no";

			int result = CustomGUI.stopQuestion("cancel confirmation", "are you sure you want to cancel the operation?", options);

			if(result == 0) {
				canceled = true;
				dispose();
			}

		});


		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		getRootPane().setDefaultButton(okButton);
		InputMap im = okButton.getInputMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
		im.put(KeyStroke.getKeyStroke("released ENTER"), "released");

		return buttonsPanel;


	}

	@Override
	public Component getInputComponentsPane() {
		this.compartment1 = new ExtendedJComboBox<String>(new String[0]);
		this.compartment2 = new ExtendedJComboBox<String>(new String[0]);
		
		List<ClipboardItem> cl = Core.getInstance().getClipboard().getItemsByClass(WorkspaceAIB.class);                    
		
		workspaces = new String[cl.size()];
		for (int i = 0; i < cl.size(); i++) {

			workspaces[i] = (cl.get(i).getName());
		}
		this.workspace = new ExtendedJComboBox<String>(workspaces);
		
		if(this.workspace.getModel().getSize()>0)
			try {
				this.setCompartments();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		this.workspace.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				try {
					setCompartments();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			
		});
		
		
		InputParameter[] inPar = getInputParameters();
		return new InputParametersPanel(inPar);
	}
	
	private void setCompartments() throws Exception {
		
//		WorkspaceAIB actualWorkspace = AIBenchUtils.getProject(this.workspace.getSelectedItem().toString());
		
		Map<Integer, String> compartments = ModelCompartmentServices.getModelCompartmentIdAndName(this.workspace.getSelectedItem().toString());
		
		String[] compartmentsNames = compartments.values().toArray(new String[compartments.size()]);
		
		
		compartment1.setModel(new DefaultComboBoxModel<>(compartmentsNames));
		
		compartment2.setModel(new DefaultComboBoxModel<>(compartmentsNames));
		
	}



	public InputParameter[] getInputParameters() {
		InputParameter[] parameters = new InputParameter[3];
		
		parameters[0] = 

				new InputParameter(
						"Workspace", 
						workspace, 
						"Select the workspace"
						);
		parameters[1] = 

				new InputParameter(
						"Compartment to replace", 
						compartment1, 
						"compartment to replace"
						);

		parameters[2] = 
				new InputParameter(
						"Compartment to keep", 
						compartment2, 
						"compartment to keep"
						);
		
		return parameters;
	}

	
	@Override
	public void setVisible(boolean b) {
		this.pack();
		super.setVisible(b);
	}
}
