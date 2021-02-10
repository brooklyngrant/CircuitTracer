import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail
 */
public class CircuitTracer {

	/** launch the program
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			System.exit(1);
		}
		try {
			new CircuitTracer(args); //create this with args
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private static void printUsage() {
		//TODO: print out clear usage instructions when there are problems with
		// any command line args
		System.out.println("Please input arguments correctly");
		System.out.println(" - First arg: -s for stack or -q for queue");
		System.out.println(" - Second arg: -c for console output or -g for GUI output");
		System.out.println(" - Third arg: input file name");
	}
	
	private CircuitBoard board = null;
	
	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 * @throws FileNotFoundException 
	 */
	private CircuitTracer(String[] args) throws FileNotFoundException {		
		
		String args0 = args[0].toString();
		String args1 = args[1].toString();
		String fileName = args[2].toString();
		if (!(args0.equals("-s") || args0.equals("-q")) && !(args1.equals("-c") || args1.equals("-g"))) {
			printUsage();
			System.exit(1);
		}

		//read in the CircuitBoard from the given file
		try {
			board = new CircuitBoard(fileName);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		}
		
		// initialize an empty Storage object called stateStore that stores objects of type TraceState and to use either a stack or queue
		Storage<TraceState> stateStore = new Storage<TraceState>(args0.equals("-s") ? Storage.DataStructure.stack : Storage.DataStructure.queue);
		// initialize an empty List called bestPaths that stores objects of type TraceState
		List<TraceState> bestPaths = new LinkedList<TraceState>();		
		// add a new initial TraceState object (a path with one trace) to stateStore for each open position adjacent to the starting component
		Point sp = board.getStartingPoint();
		
		if (board.isOpen(sp.x-1, sp.y) && sp.x > 0) { // up
			stateStore.store(new TraceState(board, sp.x-1, sp.y));
		} 
		if (board.isOpen(sp.x+1, sp.y) && sp.x < board.numRows()) { // down
			stateStore.store(new TraceState(board, sp.x+1, sp.y));
		}
		if (board.isOpen(sp.x, sp.y-1) && sp.y > 0) { // left
			stateStore.store(new TraceState(board, sp.x, sp.y-1));
		}					
		if (board.isOpen(sp.x, sp.y+1) && sp.y < board.numCols()) { // right
			stateStore.store(new TraceState(board, sp.x, sp.y+1));
		}
		
		
		while (!stateStore.isEmpty()) {			
			
			TraceState ts = stateStore.retrieve(); // retrieve the next TraceState object from stateStore		
			
			if (ts.isComplete()) { // if that TraceState object is a solution (ends with a position adjacent to the ending component)				
				if (bestPaths.isEmpty() || ts.pathLength() == bestPaths.get(0).pathLength()) { // if bestPaths is empty or the TraceState object's path is equal in length to one of the TraceStates in bestPaths
					bestPaths.add(ts); // add it to bestPaths
					
				} else if (ts.pathLength() <= bestPaths.get(0).pathLength()) { // else if that TraceState object's path is shorter than the paths in bestPaths
					bestPaths.clear(); // clear bestPaths 
					bestPaths.add(ts); // add the current TraceState as the new shortest path
					
				}
				
			} else { // else generate all valid next TraceState objects from the current TraceState and add them to stateStore
				int tsx = ts.getRow();
				int tsy = ts.getCol();
				
				if (ts.isOpen(tsx-1, tsy) && tsx > 0) { // up
					stateStore.store(new TraceState(ts, tsx-1, tsy));
				} 
				if (ts.isOpen(tsx+1, tsy) && tsx < board.numRows()) { // down
					stateStore.store(new TraceState(ts, tsx+1, tsy));
				}
				if (ts.isOpen(tsx, tsy-1) && tsy > 0) { // left
					stateStore.store(new TraceState(ts, tsx, tsy-1));
				}					
				if (ts.isOpen(tsx, tsy+1) && tsy < board.numCols()) { // right
					stateStore.store(new TraceState(ts, tsx, tsy+1));
				}
				
			}
		}	
		
		//TODO: output results to console or GUI, according to specified choice
		if (args1.equals("-c")) {
			for (TraceState ts : bestPaths) {
				System.out.println(ts.toString());
			}
		} else if (args1.equals("-g")) { 
			JFrame frame = new JFrame("Circuit Tracer");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(new CircuitTracerPanel(board, bestPaths)); //Changes size
			frame.pack();
			frame.setVisible(true);
		}
	
	}

	CircuitTracerPanelGrid panelGrid = null;
	
	class CircuitTracerPanel extends JPanel {
		
		public CircuitTracerPanel(CircuitBoard board, List<TraceState> bestPaths) {
			panelGrid = new CircuitTracerPanelGrid(board, bestPaths);
			this.add(panelGrid, BorderLayout.WEST);
			this.add(new ListedPaths(bestPaths), BorderLayout.EAST);
			
		}
		
	} // class CircuitTracerPanel
	
	class CircuitTracerPanelGrid extends JPanel {
		
		CircuitBoard board = null;
		List<TraceState> bestPaths = null;
		
		public CircuitTracerPanelGrid(CircuitBoard board, List<TraceState> bestPaths) {
			this.board = board;
			this.bestPaths = bestPaths;
			
			this.setLayout(new GridLayout(board.numRows(), board.numCols(), 0, 0));//layout for grid
			
			CircuitTracerButton[][] squares = new CircuitTracerButton[board.numRows()][board.numCols()];//makes 2d array
			for (int i = 0; i < squares.length; i++) {//making buttons and adding them to the board
				for(int j = 0; j < squares[0].length; j++) {
					squares[i][j] = new CircuitTracerButton(i, j);
					squares[i][j].setText(board.getText(i, j));
					squares[i][j].setColorForText(board.getText(i, j));
					this.add(squares[i][j]);
				}
			}
			
			
		}
		
		public void changeBoard(int pathNum) {
			TraceState ts = bestPaths.get(pathNum);
			CircuitBoard tsboard = ts.getBoard();
			panelGrid.removeAll();
			
			
			CircuitTracerButton[][] squares = new CircuitTracerButton[tsboard.numRows()][tsboard.numCols()];//makes 2d array
			for (int i = 0; i < squares.length; i++) {//making buttons and adding them to the board
				for(int j = 0; j < squares[0].length; j++) {
					squares[i][j] = new CircuitTracerButton(i, j);
					squares[i][j].setText(tsboard.getText(i, j));
					squares[i][j].setColorForText(tsboard.getText(i, j));
					panelGrid.add(squares[i][j]);
				}
			}
			panelGrid.validate();
			panelGrid.repaint();
		}
		
		
		
		
	} // class CircuitTracerPanel
	
	class ListedPaths extends JPanel {
		
		public ListedPaths(List<TraceState> bestPaths) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(bestPaths.size()+1, 1, 0, 0));
			for (TraceState path : bestPaths) {
				JButton b = new JButton();
				b.setText("Path #" + (bestPaths.indexOf(path)+1));
				b.addActionListener(new ListListener(bestPaths.indexOf(path)));
				panel.add(b);
			}
			add(panel);
		}
		
		private class ListListener implements ActionListener {
			int pathNum;
			public ListListener(int i) {
				this.pathNum = i;
			}

			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				panelGrid.changeBoard(pathNum);
				
			}
			
		}
		
	} // class ListedPaths
	
	class CircuitTracerButton extends JButton {
			
		/**
		 * Constructor method
		 */
		
		public CircuitTracerButton(int row, int column) { 
		    JButton button = new JButton();  
		    this.setEnabled(false);
		   
		}	
		
		public void setColorForText(String text) {
			if (text.equals("X")) {
				this.setBackground(Color.RED);
			} else if (text.equals("O")) {
				this.setBackground(Color.GREEN);
			} else if (text.equals("T")) {
				this.setBackground(Color.BLUE);
			} else if (text.equals("1")) {
				this.setBackground(Color.YELLOW);
			} else if (text.equals("2")) {
				this.setBackground(Color.MAGENTA);
			} else {
				this.setBackground(Color.GRAY);
			}
			
		}
		
	} // class CircuitTracerButton
	
} // class CircuitTracer
	


