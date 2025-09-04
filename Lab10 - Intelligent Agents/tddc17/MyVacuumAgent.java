package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;
import java.util.LinkedList;
import java.util.Queue;

import java.util.Random;
import javax.swing.text.TabableView;

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}
class Node {
	private int x;
	private int y;
	Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 10000;
	public MyAgentState state = new MyAgentState();
	LinkedList<Node> path = new LinkedList<>();
	Node homeNode = null;

	private void bfs_search(int target) {
		System.out.println("Starting BFS search...");
		
		// Initialize BFS queue, visited array, and parent tracker
		Queue<Node> bfsQueue = new LinkedList<>();
		boolean[][] visited = new boolean[30][30];
		Node[][] parent = new Node[30][30];  // To keep track of the path
		Node targetNode = null;
		Node startNode = new Node(state.agent_x_position, state.agent_y_position);
		
		// Add the starting node to the queue and mark it as visited
		bfsQueue.add(startNode);
		visited[startNode.getX()][startNode.getY()] = true;
		
		// Directions for moving NORTH, EAST, SOUTH, WEST
		int[] dx = {0, 1, 0, -1};
		int[] dy = {-1, 0, 1, 0};
		
		// Perform BFS
		while (!bfsQueue.isEmpty()) {
			Node currentNode = bfsQueue.poll();
			int currentX = currentNode.getX();
			int currentY = currentNode.getY();
			
			// Check if we've reached the target
			if (state.world[currentX][currentY] == target) {
				targetNode = currentNode;  // Target found
				break;
			}
			
			// Explore neighbors (NORTH, EAST, SOUTH, WEST)
			for (int i = 0; i < 4; i++) {
				int newX = currentX + dx[i];
				int newY = currentY + dy[i];
				
				// Check boundaries, if the node is not visited, and it's not a wall
				if (newX >= 0 && newY >= 0 && newX < 30 && newY < 30 &&
					!visited[newX][newY] && state.world[newX][newY] != state.WALL) {
					
					// Add new node to the queue
					bfsQueue.add(new Node(newX, newY));
					visited[newX][newY] = true;
					parent[newX][newY] = currentNode;  // Set the parent node for backtracking
				}
			}
		}
		
		// If target found, reconstruct the path
		if (targetNode != null) {
			LinkedList<Node> path = new LinkedList<>();
			Node currentNode = targetNode;
			
			// Backtrack from target to start
			while (currentNode != null && 
				   (currentNode.getX() != startNode.getX() || currentNode.getY() != startNode.getY())) {
				path.push(currentNode);
				currentNode = parent[currentNode.getX()][currentNode.getY()];
			}
			
			this.path = path;
			System.out.println("Path found.");
			System.out.println("Path length: " + path.size());
			for (Node node : path) {
				System.out.println("Node: (" + node.getX() + ", " + node.getY() + ")");
			}
		} else {
			// No path found
			this.path.clear();
			System.out.println("No path found.");
		}
	}
	
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
		
	    System.out.println("percept: " + p);
	    
		



	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else if (home)
		{
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.HOME);
			homeNode = new Node(state.agent_x_position, state.agent_y_position);
		}
		else
			state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);

	    state.printWorldDebug();
		
		
	    
	    // Next action selection based on the percept value
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 

	    else
	    {
			

			if(path.isEmpty()) { //Reached target, Look for new.
				bfs_search(state.UNKNOWN);
				if(path.isEmpty()) { //Everything discovered, Look for home.
					if(homeNode.getX() == state.agent_x_position && homeNode.getY() == state.agent_y_position) { //At home exit.
						System.out.println("Agent is home");

						System.out.println("Agent is done in " + (10000 -iterationCounter));
						
						state.agent_last_action=state.ACTION_NONE;
						return NoOpAction.NO_OP;
					}
					else 
					{
						bfs_search(state.HOME);
					}					
				}
			}
		
			Node nextNode = path.getFirst();
			int nextX = nextNode.getX();
			int nextY = nextNode.getY();
			Node currentNode = new Node(state.agent_x_position, state.agent_y_position);

			if(nextX > currentNode.getX()) //Move or turn Agent to the East
			{
				switch (state.agent_direction) {
					case MyAgentState.EAST:
						state.agent_last_action = state.ACTION_MOVE_FORWARD;
						path.poll();
						return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
				
					case MyAgentState.NORTH:
						state.agent_last_action = state.ACTION_TURN_RIGHT;
						state.agent_direction = MyAgentState.EAST;
						return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				
					case MyAgentState.SOUTH:
						state.agent_last_action = state.ACTION_TURN_LEFT;
						state.agent_direction = MyAgentState.EAST;
						return LIUVacuumEnvironment.ACTION_TURN_LEFT;
				
					case MyAgentState.WEST:
						state.agent_last_action = state.ACTION_TURN_RIGHT;
						state.agent_direction = MyAgentState.NORTH;
						return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			
			}
			if(nextX < currentNode.getX()) //Move or turn Agent to the West
			{
				switch (state.agent_direction) {
					case MyAgentState.WEST:
						state.agent_last_action = state.ACTION_MOVE_FORWARD;
						path.poll();
						return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
				
					case MyAgentState.NORTH:
						state.agent_last_action = state.ACTION_TURN_LEFT;
						state.agent_direction = MyAgentState.WEST;
						return LIUVacuumEnvironment.ACTION_TURN_LEFT;
				
					case MyAgentState.SOUTH:
						state.agent_last_action = state.ACTION_TURN_RIGHT;
						state.agent_direction = MyAgentState.WEST;
						return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				
					case MyAgentState.EAST:
						state.agent_last_action = state.ACTION_TURN_RIGHT;
						state.agent_direction = MyAgentState.SOUTH;
						return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			if(nextY > currentNode.getY()) //Move or turn Agent to the North
			{
				switch (state.agent_direction) {
					case MyAgentState.SOUTH:
						state.agent_last_action = state.ACTION_MOVE_FORWARD;
						path.poll();
						return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
				
					case MyAgentState.EAST:
						state.agent_last_action = state.ACTION_TURN_RIGHT;
						state.agent_direction = MyAgentState.SOUTH;
						return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				
					case MyAgentState.WEST:
						state.agent_last_action = state.ACTION_TURN_LEFT;
						state.agent_direction = MyAgentState.SOUTH;
						return LIUVacuumEnvironment.ACTION_TURN_LEFT;
				
					case MyAgentState.NORTH:
						state.agent_last_action = state.ACTION_TURN_RIGHT;
						state.agent_direction = MyAgentState.EAST;
						return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			if(nextY < currentNode.getY()) //Move or turn Agent to the South
			{
				switch (state.agent_direction) {
					case MyAgentState.NORTH:
						state.agent_last_action = state.ACTION_MOVE_FORWARD;
						path.poll();
						return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
				
					case MyAgentState.EAST:
						state.agent_last_action = state.ACTION_TURN_LEFT;
						state.agent_direction = MyAgentState.NORTH;
						return LIUVacuumEnvironment.ACTION_TURN_LEFT;
				
					case MyAgentState.WEST:
						state.agent_last_action = state.ACTION_TURN_RIGHT;
						state.agent_direction = MyAgentState.NORTH;
						return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				
					case MyAgentState.SOUTH:
						state.agent_last_action = state.ACTION_TURN_RIGHT;
						state.agent_direction = MyAgentState.WEST;
						return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
				}
			}
			state.agent_last_action=state.ACTION_NONE;
			return NoOpAction.NO_OP;
	    }


	}

}


public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
