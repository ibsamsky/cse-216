class Leaf<T extends Number> extends BinaryTree<T> {
    private final T value;

    public Leaf(T value) {
        this.value = value;
    }

    protected T getValue() {
        return value;
    }
}
