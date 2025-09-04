package searchCustom;

import java.util.ArrayList;
import java.util.HashSet;

import searchShared.NodeQueue;
import searchShared.Problem;
import searchShared.SearchObject;
import searchShared.SearchNode;

import world.GridPos;

public class CustomGraphSearch implements SearchObject {

	private HashSet<SearchNode> explored;
	private NodeQueue frontier;
	protected ArrayList<SearchNode> path;
	private boolean insertFront;

	/**
	 * The constructor tells graph search whether it should insert nodes to front or back of the frontier 
	 */
    public CustomGraphSearch(boolean bInsertFront) {
		insertFront = bInsertFront;
    }

	/**
	 * Implements "graph search", which is the foundation of many search algorithms
	 */
	public ArrayList<SearchNode> search(Problem p) {
		// The frontier is a queue of expanded SearchNodes not processed yet
		frontier = new NodeQueue();
		/// The explored set is a set of nodes that have been processed 
		explored = new HashSet<SearchNode>();
		// The start state is given
		GridPos startState = (GridPos) p.getInitialState();
		// Initialize the frontier with the start state  
		frontier.addNodeToFront(new SearchNode(startState));
		// Path will be empty until we find the goal.
		path = new ArrayList<SearchNode>();
		
		// While there are nodes in the frontier
		while (!frontier.isEmpty()) 
		{
			// Get the first node in the frontier
			SearchNode currentNode = frontier.removeFirst();

			// If the current node is the goal state
			if (p.isGoalState(currentNode.getState())) 
			{
				// Set the path to the path from the root to the current node
				path = currentNode.getPathFromRoot();
				break;
			}

			// Add the node to the explored set
			explored.add(currentNode);
			
			// Get the child states of the current node
			ArrayList<GridPos> childStates = p.getReachableStatesFrom(currentNode.getState());
			// For each child state
			for (GridPos childState : childStates) 
			{
				// Create a new search node from the child state and the current node
				SearchNode childNode = new SearchNode(childState, currentNode);
				// If the child node is not in the frontier and not in the explored set
				if (!frontier.contains(childNode) && !explored.contains(childNode)) 
				{
					// Add the child node to the frontier
					if (insertFront) 
						frontier.addNodeToFront(childNode);
					else 
						frontier.addNodeToBack(childNode);
				}
			}
		}
		return path;
	}

	/*
	 * Functions below are just getters used externally by the program 
	 */
	public ArrayList<SearchNode> getPath() {
		return path;
	}

	public ArrayList<SearchNode> getFrontierNodes() {
		return new ArrayList<SearchNode>(frontier.toList());
	}
	public ArrayList<SearchNode> getExploredNodes() {
		return new ArrayList<SearchNode>(explored);
	}
	public ArrayList<SearchNode> getAllExpandedNodes() {
		ArrayList<SearchNode> allNodes = new ArrayList<SearchNode>();
		allNodes.addAll(getFrontierNodes());
		allNodes.addAll(getExploredNodes());
		return allNodes;
	}

}
