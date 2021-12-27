/**
 * The TreeBuilder class creates a binary tree of generic type. It uses a
 * queue-based approach, has a constructor that takes in a generic array and
 * builds the tree using a level-order esque approach, and has a getTree()
 * method which returns the tree.
 * 
 */
public class TreeBuilder<T> {
    private LinkedBinaryTree<T> tree; // create the generic tree variable.

    /**
     * Constructor method that takes in a generic array which values will comprise
     * the tree. Uses two queues to build the tree.
     * 
     * @param array generic array that contains all values used to initialize the
     *              tree.
     */
    public TreeBuilder(T[] array) {
        LinkedQueue<BinaryTreeNode<T>> dataQueue = new LinkedQueue<>(); // create dataQueue object of type
                                                                        // LinkedQueue<BinaryTreeNode<T>>.
        LinkedQueue<BinaryTreeNode<T>> parentQueue = new LinkedQueue<>(); // create parentQueue object and initialize it
                                                                          // as an empty queue.

        // initialize the dataQueue queue by looping through all elements of the array,
        // assigning each to a BinaryTreeNode<T>, and then enqueuing each node onto the
        // dataQueue.
        for (int i = 0; i < array.length; i++) {
            BinaryTreeNode<T> node = new BinaryTreeNode<T>(array[i]);
            dataQueue.enqueue(node);
        }

        BinaryTreeNode<T> root = dataQueue.dequeue(); // create root node and have it store first element of dataQueue
        parentQueue.enqueue(root); // enqueue root node onto parentQueue
        tree = new LinkedBinaryTree<T>(root); // set the tree's root node

        while (!dataQueue.isEmpty()) {
            BinaryTreeNode<T> a = dataQueue.dequeue(); // node a holds the left child node from dataQueue
            BinaryTreeNode<T> b = dataQueue.dequeue(); // node b holds the right child node from dataQueue
            BinaryTreeNode<T> parent = parentQueue.dequeue(); // temp node parent holds the dequeued parent node from
                                                              // parentQueue

            if (a.getData() != null) { // if a node (left child) isn't null
                parent.setLeft(a); // set current parent node's left child to be a (in the tree)
                parentQueue.enqueue(a); // enqueue node a as a new parent node to have it's children checked and set in
                                        // subsequent loops
            }
            if (b.getData() != null) { // same steps for node a, applies to node b
                parent.setRight(b);
                parentQueue.enqueue(b);
            }
        }
    }

    /**
     * Accessor method that gets the created tree.
     * 
     * @return tree object.
     */
    public LinkedBinaryTree<T> getTree() {
        return tree;
    }
}
