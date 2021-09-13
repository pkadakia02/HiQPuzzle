

class HiQ_0
{
	public static void main(String[] args) throws java.lang.Exception
	{
		Puzzle.solve(new HiQ(0,0));
	}
}

interface Puzzle 
{
    int action_count();                 // number of possible actions
    boolean action(int move_number);    // try to perform an action
    void undo_last_action();
    boolean goal();                     // Puzzle is in a goal state
    void display_solution();
    
    static void solve(Puzzle P)
    {
        if(P.goal())
            P.display_solution();         
        else  
            for (int a = 0; a < P.action_count(); a++)
                if(P.action(a))
                {
                    solve(P);
                    P.undo_last_action();
                }
    }
}

class HiQ implements Puzzle
{
    //==============================================================================
    // Represents a rectangular board with padding using a 1-d array.
    //
    class Board
    {
        private int NROWS;      // number of rows
        private int NCOLS;      // number of columns
        private int PAD;        // padding around the board
        private int BOARD_SIZE; // size of array to store board with padding

        public int LEFT = -1;   // movement directions
        public int RIGHT = 1;
        public int UP;
        public int DOWN;
        
        //-----------------------------------
        // compute contents index from row, col
        int index(int row, int col)
        {
            return (row + PAD)*DOWN + col + PAD;
        }
        //-----------------------------------
        // compute row, col from contents index
        int row(int index){ return index/DOWN - PAD; }
        int col(int index){ return index%DOWN - PAD; }
        
        //-----------------------------------
        // get or set the board contents
        Object get_contents(int row, int col)
        {
            return contents[(row + PAD)*DOWN + col + PAD];
        }
        void set_contents(int row, int col, Object c)
        {
            contents[(row + PAD)*DOWN + col + PAD] = c;
        }
        
        //-----------------------------------
        // constructor
        Board(int nrows, int ncols, int pad)
        {
            NROWS = nrows;
            NCOLS = ncols;
            PAD = pad;
            DOWN = NCOLS + 2*PAD;               // length of a row + padding
            UP = -DOWN;
            BOARD_SIZE = DOWN*(NROWS + 2*PAD);           
            
            contents = new Object[BOARD_SIZE];  // allocate storage
            
            for(int i = 0; i < BOARD_SIZE; i++)
                contents[i] = null;
        }
        public Object contents[];               // stores the board contents
    }
    
    //==============================================================================
    // Represents a possible action (peg jump)
    //
    class Move
    {
        public int from, over, to;  // index into board contents
        public Move(int f, int o, int t){from = f; over = o; to = t;}
    }
    //------------------------------------------------------------------------
    // Representation of the Hi_Q problem state
    //
    private final int nrows = 5;
    private final int ncols = 5;
    private Board b = new Board(nrows, ncols, 2);               // board contents
    private Move move_list[] = new Move[nrows*ncols*8];         // all possible moves
    private int move_count = 0;                                 // number of possible moves
    private Move made_move_list[] = new Move[nrows*ncols - 1];  // moves actually made
    private int made_move_count = 0;                            // number of made moves
    private int solution_count = 0;
    private final Integer EMPTY = new Integer(0);               // possible board contents
    private final Integer FULL = new Integer(1);
    private int row_empty;                                      // initially empty hole
    private int col_empty;
    
    public HiQ(int row_empty, int col_empty)
    {
        this.row_empty = row_empty;
        this.col_empty = col_empty;
    
        //--------------------------------------------------------------------
        // Setup the list of all possible moves
        //
        for (int row = 0; row < nrows; row++)       // go through all the rows/columns
            for (int col = 0; col < ncols; col++)
                b.set_contents(row, col, FULL);     // fill the board
        b.set_contents(row_empty, col_empty, EMPTY);
        
        int d[] = {b.LEFT, b.RIGHT, b.UP, b.DOWN,   // all 8 possible jump directions 
                    b.UP+b.RIGHT, b.UP+b.LEFT, b.DOWN+b.LEFT, b.DOWN+b.RIGHT};
                    
        for (int row = 0; row < nrows; row++)       // go through all the rows/columns
            for (int col = 0; col < ncols; col++)
                for (int i = 0; i < d.length; ++i)  // look in all directions for jumps 
                {
                    int hole_to = b.index(row, col) + 2*d[i];
                    if (b.contents[hole_to] == null) continue;  // jump off board
                    
                    int hole_over = b.index(row, col) + d[i];
                    int hole_from = b.index(row, col);
                    move_list[move_count++] = new Move(hole_from, hole_over, hole_to);
                }

        //--------------------------------------------------------------------
        //Show the list of all possible moves
        /*
        System.out.println("move_count " + move_count);
        for (int i = 0; i < move_count; ++i)
        {
            Move m = move_list[i];
            System.out.println("jump" + "   (" + b.row(m.from) + " " + b.col(m.from) + ")" + "   (" + b.row(m.over) + " " + b.col(m.over) + ")" + "   (" + b.row(m.to) + " " + b.col(m.to) + ")"); 
        } */ 
    }
    
    //------------------------------------------------------------------------
    // Interface implementation
    //
    
    public boolean goal(){return made_move_count == ((ncols*nrows)-2);}
    
    public int action_count(){return move_count;} //how many possible jumps
    
    public void undo_last_action(){
    	
    	made_move_count--;
    	Move m = made_move_list[made_move_count];
    	
    	b.contents[m.from] = FULL;
    	b.contents[m.over]= FULL;
    	b.contents[m.to] = EMPTY;
    }
    
    public boolean action(int i){
    	Move m = move_list[i];
    	if(b.contents[m.from] == FULL && b.contents[m.over] == FULL && b.contents[m.to] == EMPTY) {
    		b.contents[m.from] = EMPTY;
        	b.contents[m.over]= EMPTY;
        	b.contents[m.to] = FULL;
    		made_move_list[made_move_count++] = m;	
    		return true;}
    	return false;
    		
    }

    public void display_solution(){
    	System.out.println("Solution " + (++solution_count));
    	
        for (int i = 0; i < made_move_count; ++i)
        {
            Move m = made_move_list[i];
            System.out.println(i+1 + ") " + b.row(m.from) + ", " + b.col(m.from) + "        " + b.row(m.to) + ", " + b.col(m.to)); 
        }
        
        System.out.println("\n"); 
        System.out.println("Press Enter To Continue...");     
        new java.util.Scanner(System.in).nextLine();
    	
    }
    
    

}