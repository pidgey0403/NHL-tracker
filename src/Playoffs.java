import java.util.Iterator;

/**
 * The Playoffs class represents NHL team playoffs and uses the TreeBuilder
 * class to create the standings table which records winners for each round,
 * until the final champions are found. It has a constructor which creates the
 * main tree, and methods including: getTree(), getStandings(),
 * updateStandings(), updateRound(), and findParent().
 * 
 */
public class Playoffs {
    private LinkedBinaryTree<String> tree; // create the String tree variable.
    private HockeySeries[] standings; // create the HockeySeries standings array variable.

    /**
     * Constructor method that initializes every node in the tree with either the
     * pairs of teams facing off (series), or with a "TDB" in higher nodes where the
     * winning team is not yet known.
     */
    public Playoffs() {
        String[] storeData = new String[31]; // initialized storeData array with 31 spots.

        for (int i = 0; i < 15; i++) { // for currently unknown winner indexes in the storeData array, initialize them
                                       // with "TBD"+a number
            storeData[i] = "TBD " + i;
        }

        standings = new HockeySeries[15]; // initialize the standings table with 15 slots
        MyFileReader text = new MyFileReader("teams.txt"); // read the team data using MyFileReader and store it into
                                                           // variable text.

        /*
         * for indexes 15-31, set storeData to equal the team names data taken from
         * "teams.txt" while reading the next line from the text file.
         */
        for (int i = 15; i < storeData.length; i++) {
            storeData[i] = text.readString();
        }

        // iterate through the storeData array (which holds team names) and update the
        // standings table with the teams in pairs (pair of teams facing off). i.e.
        // standings[0] = teamA vs teamB
        for (int i = 0, j = 15; j < storeData.length; i++, j += 2) {
            standings[i] = new HockeySeries(storeData[j], storeData[j + 1], 0, 0);
        }

        TreeBuilder<String> binaryTree = new TreeBuilder<>(storeData); // call the TreeBuilder class to create a tree
                                                                       // using storeData (names of teams)
        tree = binaryTree.getTree(); // set tree variable to hold the tree using getTree()
    }

    /**
     * Accessor method that gets the tree object.
     * 
     * @return tree object.
     */
    public LinkedBinaryTree<String> getTree() {
        return tree;
    }

    /**
     * Accessor method that gets the standings table.
     * 
     * @return standings table.
     */
    public HockeySeries[] getStandings() {
        return standings;
    }

    /**
     * Mutatator method that updates teams standings, increment a teams wins
     * depending on their scores, and returns the winning team, if one reaches 4
     * wins.
     * 
     * @param teamA  first team's name.
     * @param teamB  second team's name.
     * @param scoreA first team's int score.
     * @param scoreB second team's int score.
     * @return null if neither team reaches a winning score, or if update was
     *         invalid.
     */
    public String updateStandings(String teamA, String teamB, int scoreA, int scoreB) {
        int correctIndex = 0; // initialize correctIndex variable to 0

        for (int i = 0; i <= standings.length; i++) { // iterate through standings array
            // search for the series that corresponds to the paramter teamA and teamB names
            if ((standings[i].getTeamA().equals(teamA)) && (standings[i].getTeamB().equals(teamB))) {
                standings[i].incrementWins(scoreA, scoreB); // if they match, increment the number of wins for each team
                                                            // using the input parameters (stored in the standings
                                                            // array)
                correctIndex = i; // update the correctIndex variable to the current loop iteration
                break; // if a series matches, and update happens, exit the loop.
            }
        }
        if (standings[correctIndex].getTeamAWins() >= 4) { // if standings at loop iteration where increment occurs, has
                                                           // teamA with 4 or more wins
            return standings[correctIndex].getTeamA(); // return the winning team (team A)
        }
        if (standings[correctIndex].getTeamBWins() >= 4) { // same logic, but if team B has 4 or more wins, then return
                                                           // teamB
            return standings[correctIndex].getTeamB();
        }
        return null; // if neither team is at 4 or more points, return null.
    }

    /**
     * Mutator method that updates the current round if a winner is determined from
     * a series, as well as sets the winning team to the parent node in the tree (to
     * face off another winning team in the next round).
     * 
     * @param round the int which determines which scores text file to open.
     */
    public void updateRound(int round) {
        MyFileReader scores = new MyFileReader("scores" + round + ".txt"); // open the scores file determined by input
                                                                           // parameter round, and store it in scores
                                                                           // variable.

        while (!scores.endOfFile()) { // iterate through the text file as long as it isn't empty
            String[] holdScores = scores.readString().split(","); // split a line in the text file into an array with 4
                                                                  // slots, and store it into holdScores array.
            String name = null; // initialize name variable to null

            /*
             * if teamA's score is greater than teamB's score (stored in holdScores array)
             * call UpdateStandings method and add 1 point to teamA's score; updateStandings
             * returns the team name IF the team has more than 4 points, so store this in
             * name variable.
             */
            if (Integer.parseInt(holdScores[2]) > Integer.parseInt(holdScores[3])) {
                name = updateStandings(holdScores[0], holdScores[1], 1, 0);
            }
            // do the same except if teamB's score is greater, add 1 point to teamB
            else if (Integer.parseInt(holdScores[2]) < Integer.parseInt(holdScores[3])) {
                name = updateStandings(holdScores[0], holdScores[1], 0, 1);
            }

            if (name != null) { // if a winning team was returned
                // create parent node and set it to the parent of teamA and teamB (if it exists)
                BinaryTreeNode<String> parent = findParent(holdScores[0], holdScores[1]);
                parent.setData(name); // set the parent node's data to the winning team's name
            }
        }
    }

    /**
     * Acccessor method that gets the parent node of two given children nodes.
     * 
     * @param teamAName left child node's name in the tree.
     * @param teamBName right child node's name in the tree.
     * @return null if children nodes don't match up to a parent node, else return
     *         the parent node.
     */
    public BinaryTreeNode<String> findParent(String teamAName, String teamBName) {
        // create parentQueue queue object; used to store nodes to be traversed
        // according to level-order traversal approach
        LinkedQueue<BinaryTreeNode<String>> parentQueue = new LinkedQueue<>();
        BinaryTreeNode<String> node = tree.getRoot(); // create binaryTreeNode node variable and set it to be the root
                                                      // node of the tree.

        parentQueue.enqueue(node); // enqueue the tree's root to the parentQueue (this will be visited first)

        while (!parentQueue.isEmpty()) { // iterate through parentQueue if not empty
            node = parentQueue.dequeue(); // current node is the node dequeued from parentQueue
            if ((node.getLeft() != null) && (node.getRight() != null)) { // if node has left and right children
                if ((node.getLeft().getData().equals(teamAName)) && node.getRight().getData().equals(teamBName)) {
                    return node; // if left child and right child's data equals input parameter data, return node
                }
                parentQueue.enqueue(node.getLeft()); // enqueue left node onto parentQueue (becomes new parent node)
                parentQueue.enqueue(node.getRight()); // enqueue right node onto parentQueue (becomes new parent node)
            }
        }
        return null; // if parent node cannot be found return null
    }

    /**
     * This method adds the new series to the standings array before a new round
     * begins. It does this using an iterator from the tree and skipping over the
     * nodes that are still unknown until it gets to the nodes from the new round.
     * It then takes two teams at a time from the iterator and creates a new series
     * in the standings array for those two teams. The series standings (number of
     * wins for each team) are set to 0 by default.
     */
    public void addNewStandings(int numSkips, int sIndex, int eIndex) {
        Iterator<String> iter = tree.iteratorLevelOrder();
        int i;
        String team1, team2;
        for (i = 0; i < numSkips; i++) {
            iter.next();
        }
        for (i = sIndex; i <= eIndex; i++) {
            team1 = iter.next();
            team2 = iter.next();
            standings[i] = new HockeySeries(team1, team2, 0, 0);
        }
    }

    /**
     * This method simply prints out the standings table in a cleanly formatted
     * table structure.
     */
    public void printStandings() {
        String str;
        for (int k = 0; k < standings.length; k++) {
            if (standings[k] != null) {
                str = String.format("%-15s\t%-15s\t%3d-%d", standings[k].getTeamA(), standings[k].getTeamB(),
                        standings[k].getTeamAWins(), standings[k].getTeamBWins());
                System.out.println(str);
            }
        }
    }

    public static void main(String[] args) {
        Playoffs pl = new Playoffs();
        pl.updateRound(1);

        // Uncomment each pair of lines when you are ready to run the subsequent rounds.

        // pl.addNewStandings(7, 8, 11); // Ensure you execute this line before calling
        // updateRound(2).
        // pl.updateRound(2);

        // pl.addNewStandings(3, 12, 13); // Ensure you execute this line before calling
        // updateRound(3).
        // pl.updateRound(3);

        // pl.addNewStandings(1, 14, 14); // Ensure you execute this line before calling
        // updateRound(4).
        // pl.updateRound(4);
    }

}
