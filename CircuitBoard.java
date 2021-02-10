import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Represents a 2D circuit board as read from an input file.
 *  
 * @author mvail
 */
public class CircuitBoard {
	/** current contents of the board */
	private char[][] board;
	/** location of row,col for '1' */
	private Point startingPoint;
	/** location of row,col for '2' */
	private Point endingPoint;

	//constants you may find useful
	private final int ROWS; //initialized in constructor
	private final int COLS; //initialized in constructor
	private final char OPEN = 'O'; //capital 'o'
	private final char CLOSED = 'X';
	private final char TRACE = 'T';
	private final char START = '1';
	private final char END = '2';
	private final String ALLOWED_CHARS = "OXT12";

	/** Construct a CircuitBoard from a given board input file, where the first
	 * line contains the number of rows and columns as ints and each subsequent
	 * line is one row of characters representing the contents of that position.
	 * Valid characters are as follows:
	 *  'O' an open position
	 *  'X' an occupied, unavailable position
	 *  '1' first of two components needing to be connected
	 *  '2' second of two components needing to be connected
	 *  'T' is not expected in input files - represents part of the trace
	 *   connecting components 1 and 2 in the solution
	 * 
	 * @param filename
	 * 		file containing a grid of characters
	 * @throws FileNotFoundException if Scanner cannot read the file
	 * @throws InvalidFileFormatException for any other format or content issue that prevents reading a valid input file
	 */
	public CircuitBoard(String filename) throws FileNotFoundException {
		try {
			Scanner fileScan = new Scanner(new File(filename)); // throws FileNotFoundException if Scanner cannot read the file
			Scanner lineScan;	
			String line = "";
			
			//parse the given file to populate the char[][]		
			try {
				line = fileScan.nextLine();
				lineScan = new Scanner(line);
				//Number of columns and rows
				ROWS = lineScan.nextInt();
				COLS = lineScan.nextInt();
				
				//Set grid up		
				board = new char[ROWS][COLS];
				
				boolean isAlreadyAOne = false;
				boolean isAlreadyATwo = false;
				for (int i = 0; i < board.length; i++) { //rows
					line = fileScan.nextLine();
					lineScan = new Scanner(line);
					int lengthLine = line.replaceAll("\\s", "").length();
					if (lengthLine != COLS) {
						lineScan.close();
						throw new Exception("6");
					}
					for (int j = 0; j < board[i].length; j++) { //cols 						
						if (lineScan.hasNext()) {
							char currentChar = lineScan.next().charAt(0);
							board[i][j] = currentChar;
							if (currentChar == '1' && !isAlreadyAOne) { 
								startingPoint = new Point(i,j);
								isAlreadyAOne = true;
							} else if (currentChar == '2' && !isAlreadyATwo) {
								endingPoint = new Point(i,j);
								isAlreadyATwo = true;
							} else if (currentChar != 'O' && currentChar != 'X') {
								lineScan.close();
								throw new Exception("1");
							}
						} else {
							lineScan.close();
							throw new Exception("2");
						}						
					}
					lineScan.close();				
				}
				if (fileScan.hasNext()) {
					throw new Exception("3");
				}
				if (!isAlreadyAOne || !isAlreadyATwo) {
					throw new Exception("4");
				}
				if (startingPoint.x+1 == endingPoint.x || startingPoint.x-1 == endingPoint.x || startingPoint.y+1 == endingPoint.y || startingPoint.y-1 == endingPoint.y) {
					if (startingPoint.x == endingPoint.x || startingPoint.y == endingPoint.y) { // 1 and 2 are next to each other
						//throw new Exception("5");
					}
				}
			
			} catch (Exception e) { // throw InvalidFileFormatException if any formatting or parsing issues are encountered
				throw new InvalidFileFormatException("\nException type:" + e + ". Please fix format of file: " + filename);
			}
			
			fileScan.close();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("File not found: " + filename);
		}
		
		
	}
	
	/** Copy constructor - duplicates original board
	 * 
	 * @param original board to copy
	 */
	public CircuitBoard(CircuitBoard original) {
		board = original.getBoard();
		startingPoint = new Point(original.startingPoint);
		endingPoint = new Point(original.endingPoint);
		ROWS = original.numRows();
		COLS = original.numCols();
	}

	/** utility method for copy constructor
	 * @return copy of board array */
	private char[][] getBoard() {
		char[][] copy = new char[board.length][board[0].length];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				copy[row][col] = board[row][col];
			}
		}
		return copy;
	}
	
	/** Return the char at board position x,y
	 * @param row row coordinate
	 * @param col col coordinate
	 * @return char at row, col
	 */
	public char charAt(int row, int col) {
		return board[row][col];
	}
	
	/** Return whether given board position is open
	 * @param row
	 * @param col
	 * @return true if position at (row, col) is open 
	 */
	public boolean isOpen(int row, int col) {
		if (row < 0 || row >= board.length || col < 0 || col >= board[row].length) {
			return false;
		}
		return board[row][col] == OPEN;
	}
	
	/** Set given position to be a 'T'
	 * @param row
	 * @param col
	 * @throws OccupiedPositionException if given position is not open
	 */
	public void makeTrace(int row, int col) {
		if (isOpen(row, col)) {
			board[row][col] = TRACE;
		} else {
			throw new OccupiedPositionException("row " + row + ", col " + col + "contains '" + board[row][col] + "'");
		}
	}
	
	/** Set given position to be a 'T'
	 * @param row
	 * @param col
	 * @throws OccupiedPositionException if given position is not open
	 */
	public String getText(int row, int col) {
		return Character.toString(board[row][col]);
	}
	
	/** @return starting Point(row,col) */
	public Point getStartingPoint() {
		return new Point(startingPoint);
	}
	
	/** @return ending Point(row,col) */
	public Point getEndingPoint() {
		return new Point(endingPoint);
	}
	
	/** @return number of rows in this CircuitBoard */
	public int numRows() {
		return ROWS;
	}
	
	/** @return number of columns in this CircuitBoard */
	public int numCols() {
		return COLS;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				str.append(board[row][col] + " ");
			}
			str.append("\n");
		}
		return str.toString();
	}
	
}// class CircuitBoard
