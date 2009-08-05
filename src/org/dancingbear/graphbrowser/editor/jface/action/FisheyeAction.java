package org.dancingbear.graphbrowser.editor.jface.action;

import java.util.List;

import org.dancingbear.graphbrowser.editor.gef.editparts.NodeEditPart;
import org.dancingbear.graphbrowser.editor.gef.ui.parts.GraphEditor;
import org.dancingbear.graphbrowser.layout.DirectedGraphToModelConverter;
import org.dancingbear.graphbrowser.layout.Layout;
import org.dancingbear.graphbrowser.layout.ModelToDirectedGraphConverter;
import org.dancingbear.graphbrowser.layout.dot.DirectedGraphLayout;
import org.dancingbear.graphbrowser.layout.fisheye.FisheyeLayout;
import org.dancingbear.graphbrowser.layout.model.DirectedGraph;
import org.dancingbear.graphbrowser.layout.model.Node;
import org.dancingbear.graphbrowser.model.IModelGraph;
import org.dancingbear.graphbrowser.model.IModelNode;
import org.eclipse.draw2d.Animation;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;


public class FisheyeAction extends Action {

	private static final int ANIMATION_TIME = 500;
	private IWorkbenchPage page;

	public FisheyeAction(IWorkbenchPage page) {
		this.page = page;
	}


	@Override
	public void run() {
		if (page.getActiveEditor() instanceof GraphEditor) {
			Animation.markBegin();
			GraphEditor editor = (GraphEditor) page.getActiveEditor();

			// get the selected node
			List<?> selectedItems = editor.getViewer().getSelectedEditParts();
			IModelNode node = null;

			for (int i = 0; i < selectedItems.size(); i++) {
				if (selectedItems.get(i) instanceof NodeEditPart) { 
					NodeEditPart nodePart = (NodeEditPart) selectedItems.get(i);
					node = nodePart.getCastedModel();            }
			}

			if (node ==null) {
				return; // no nodes, so don't do anything
			}
			
			IModelGraph graph = editor.getGraph();

			final ModelToDirectedGraphConverter modelToGraphConv = new ModelToDirectedGraphConverter();
			final DirectedGraphToModelConverter graphToModelConvert = new DirectedGraphToModelConverter();

			// Convert graph to directed graph
			// For this moment, the obtained graph has no layout. All the nodes have (0,0) as coordinates 
			final DirectedGraph directedGraph = modelToGraphConv.convertToGraph(graph.getName());

			//find the selected origin in the directed graph (by name)
			Node origin = null;
			for (Node n : directedGraph.getNodes()) {
				if (n.getData().equals(node.getName())) {
					origin = n;
					break;
				}
			}

			// We need to apply a first layout to distribute the nodes on the canvas
			DirectedGraphLayout layout = new DirectedGraphLayout();
			layout.visit(directedGraph);

			// Apply Fisheye layout
			if (origin != null) {
				Layout l = new FisheyeLayout(origin);
				l.visit(directedGraph);
			}

			// Store graph
			graphToModelConvert.convertToModel(directedGraph, graph.getName());

			Animation.run(ANIMATION_TIME);
			editor.getViewer().flush();
		}
	}

}