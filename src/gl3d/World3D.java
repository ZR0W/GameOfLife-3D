package gl3d;

import java.util.Random;

public class World3D {

	private Cell[][][] world;
	private int worldWidth;
	private int worldLength;
	private int worldHeight;
	
	final private int UNDERPOPULATION_CAP = 1;
	final private int SURVIVAL_MIN = 1;
	final private int SURVIVAL_MAX = 3;
	final private int OVERPOPULATION_MIN =3;
	final private int REPRODUCTION_MIN = 3;
	final private int REPRODUCTION_MAX = 3;
	
	int genCount;
	
	public World3D(int width, int length, int height) {
		worldWidth = width;
		worldLength = length;
		worldHeight = height;
		world = new Cell[width][length][height];
		genCount = 0;
		
		for(int x = 0; x < worldWidth; x++) {
			for(int y = 0; y < worldLength; y++) {
				for(int z = 0; z < worldHeight; z++) {
					world[x][y][z] = new Cell();
				}
			}
		}
	}
	
	public World3D() {
		this(10, 10, 10);
	}
	
	public void update() {
		System.out.println("---Generation " + genCount + "---");
		updateAllNeighbours();
		int nextGenSurvivalCount = 0;
		for(int i = 0; i < worldWidth; i++) {
			for(int j = 0; j < worldLength; j++) {
				for(int k = 0; k < worldHeight; k++) {
					Cell cell = world[i][j][k];
					int num = cell.getNumNeighbours();
					Boolean isLiveCell = cell.isAlive();
					if(isLiveCell) {
						//live cell conditions:
						
						//underpopulation check
						if(num < UNDERPOPULATION_CAP) {
							cell.unPopulate();
//						System.out.print("x");
						}
						//survival check
						if(SURVIVAL_MIN < num && num < SURVIVAL_MAX) {
							cell.populate();
							nextGenSurvivalCount++;
//						System.out.print("o");
						}
						//overpopulation check
						if(OVERPOPULATION_MIN < num) {
							cell.unPopulate();
//						System.out.print("x");
						}	
					}else {
						//dead cell conditions: 
						
						//reproduction check
						if(REPRODUCTION_MIN < num) {
							cell.populate();
							nextGenSurvivalCount++;
//						System.out.print("o");
						}
					}
//					System.out.print("current neighbour Count = " + num + "/");
					System.out.print(num + ".");
				}

			}
		}
		System.out.print("\n" + "num living in nextGen: " + nextGenSurvivalCount);
		System.out.print("\n");
		genCount++;
	}
	
	public void randomInitialize(int numCells) {
		System.out.println("randomInitialize()");
		if(numCells > worldWidth*worldHeight*worldLength) {
			System.err.println("Error: num to generate larger than world total");
			return;
		}
		int count = 0;
		System.out.print("\t");
		while(count < numCells) {
			
			int x = getRandomNumber(0, worldWidth);
			int y = getRandomNumber(0, worldLength);
			int z = getRandomNumber(0, worldHeight);
			if(exists(x, y, z) && !isAlive(x, y, z)) {
				populate(x, y, z);
				count++;
				System.out.print(".");
			}
			
		}
		for(int i = 0; i < worldWidth; i++) {
			for(int j = 0; j < worldLength; j++) {
				for(int k = 0; k < worldHeight; k++) {
					updateNumNeighbours(i, j, k);
				}

			}
		}
		System.out.println();
	}
	
	public int getWorldWidth() {
		return worldWidth;
	}

	public int getWorldLength() {
		return worldLength;
	}

	public int getWorldHeight() {
		return worldHeight;
	}
	
	public Boolean isAlive(int x, int y, int z) {
		if(exists(x, y, z)) {
			return world[x][y][z].isAlive();
		}else {
			return false;
		}
	}

	@Override
	public String toString() {
		String output = "";
		for(int i = 0; i < worldWidth; i++) {
			output += ("i: " + i + "\n");
			for(int j = 0; j < worldLength; j++) {
				output += ("\t" + "j: " + j + "\n");
				for(int k = 0; k < worldHeight; k++) {
					output += ("\t\t" + "k: " + k);
					output += world[i][j][k].toString();
				}

			}
		}
//		return "World3D.toString";
		return output;
	}

	private boolean exists(int x, int y, int z) {
		Boolean inX = (x >= 0) && (x < worldWidth);
		Boolean inY = (y >= 0) && (y < worldLength);
		Boolean inZ = (z >= 0) && (z < worldHeight);
		return inX && inY && inZ;
	}

	private void populate(int x, int y, int z) {
		if(exists(x, y, z)) {
			world[x][y][z].populate();			
		}else {
			System.err.println("target does not exist");
		}
	}
	
	private void unpopulate(int x, int y, int z) {
		if(exists(x, y, z)) {
			world[x][y][z].unPopulate();
		}else {
			System.err.println("target does not exist");
		}
	}
	
	/**
	 * generates a random number in the range provided, <Strong>inclusive</Strong>
	 * @param min
	 * @param max
	 * @return
	 */
	private int getRandomNumber(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	private Cell getCell(int x, int y, int z) {
		return world[x][y][z];
	}
	
	private void setCell(int x, int y, int z, Cell c) {
		world[x][y][z] = c;
	}
	
	private void updateNumNeighbours(int x, int y, int z) {
		//we imagine home node in 0, 0, 0
		//there are a total of 3*3*3 - 1 neighbors to check for
		
		//-1, 1, 1
		//0, 1, 1
		//1, 1, 1
		
		//-1, 0, 1
		//0, 0, 1
		//1, 0, 1
		
		//-1, -1, 1
		//0, -1, 1
		//1, -1, 1
		
		//etc.
		
		int num = 0;
		
		for(int i = x-1; i <= x+1; i++) {
			for(int j = y-1; j <= y+1; j++) {
				for(int k = z-1; k <= z+1; k++) {
					//check for existence
					if(exists(i, j, k)) {
						//check for not center cell
						if(!(x==i && y==j && z==k)) {
							if(world[i][j][k].isAlive()) {
								num++;
							}
						}						
					}
					
				}
			}
		}
		world[x][y][z].updateNumNeighbour(num);
		
	}
	
	private void updateAllNeighbours() {
		for(int i = 0; i < worldWidth; i++) {
			for(int j = 0; j < worldLength; j++) {
				for(int k = 0; k < worldHeight; k++) {
					updateNumNeighbours(i, j, k);
				}

			}
		}
	}
	
	public static void main(String[] args) {
		World3D w = new World3D();
		w.populate(0, 0, 0);
		w.populate(1, 0, 0);
		w.populate(0, 1, 0);
		w.populate(1, 1, 0);
		
		w.populate(0, 0, 1);
		w.populate(1, 0, 1);
		w.populate(0, 1, 1);
		w.populate(1, 1, 1);
		
		w.updateAllNeighbours();
	}

}
