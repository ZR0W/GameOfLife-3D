package gl3d;

public class Cell {
	
	
	private Boolean stateChanged; //whether or not current state is different than previous
	private Boolean living; //whether or not the cell is populated
	private int numLiveNeighbours; //number of neighbors around this cell
	
	public Cell() {
		stateChanged = false;
		living = false;
		numLiveNeighbours = 0;
	}
	
	public Boolean stateChanged() {
		return stateChanged;
	}
	
	public Boolean isAlive() {
		return living;
	}
	
	public int getNumNeighbours() {
		return numLiveNeighbours;
	}
	
	public Boolean populate() {
		if(!living) {
			living = true;
			stateChanged = true;
		}else {
			stateChanged = false;
		}
		return stateChanged;
	}
	
	public Boolean unPopulate() {
		if(living) {
			living = false;
			stateChanged = true;
		}else {
			stateChanged = false;
		}
		return stateChanged;
	}
	
	public void updateNumNeighbour(int num) {
		numLiveNeighbours = num;	
	}

	@Override
	public String toString() {
//		return "Cell [stateChanged=" + stateChanged + ", living=" + living + ", numLiveNeighbours=" + numLiveNeighbours
//				+ "]";
		if(living) {
			return "o";
		}else {			
			return "x";
		}
	}

	public static void main(String[] args) {
		Cell c = new Cell();
		c.populate();
		System.out.println(c.toString());
	}

}
