class Node<T extends Number> extends BinaryTree<T> {
    private final Operator operator;
    private final BinaryTree<T> left;
    private final BinaryTree<T> right;

    public Node(Operator operator, BinaryTree<T> left, BinaryTree<T> right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    protected Operator getOperator() {
        return operator;
    }

    protected BinaryTree<T> getLeft() {
        return left;
    }

    protected BinaryTree<T> getRight() {
        return right;
    }
}
