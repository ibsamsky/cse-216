class ArithmeticExpression {
    public static <T extends Number> double evaluate(BinaryTree<T> tree) {
        Leaf<T> boxedResult = evaluateBoxed(tree);
        return evaluateLeaf(boxedResult).doubleValue();
    }

    private static <T extends Number> Leaf<T> evaluateBoxed(BinaryTree<T> tree) {
        if (tree instanceof Leaf) return ((Leaf<T>) tree);
        // sealed classes are not available in 1.8
        if (!(tree instanceof Node)) throw new IllegalArgumentException("invalid tree type");
        Node<T> node = ((Node<T>) tree);
        if (node.getLeft() instanceof Node)
            return evaluateBoxed(new Node<>(node.getOperator(), evaluateBoxed(node.getLeft()), node.getRight()));
        if (node.getRight() instanceof Node)
            return evaluateBoxed(new Node<>(node.getOperator(), node.getLeft(), evaluateBoxed(node.getRight())));

        // left and right are both Leaf
        assert (node.getLeft() instanceof Leaf && node.getRight() instanceof Leaf);

        Double resultVal = null;
        Double leftVal = evaluateLeaf((Leaf<T>) node.getLeft()).doubleValue();
        Double rightVal = evaluateLeaf((Leaf<T>) node.getRight()).doubleValue();

        boolean intLeaf = ((Leaf<T>) node.getLeft()).getValue() instanceof Integer;
        switch (node.getOperator()) {
            case ADD:
                resultVal = leftVal + rightVal;
                break;
            case SUBTRACT:
                resultVal = leftVal - rightVal;
                break;
            case MULTIPLY:
                resultVal = leftVal * rightVal;
                break;
            case DIVIDE:
                if (intLeaf) {
                    // perform integer division
                    resultVal = (double) (int) (leftVal / rightVal);

                } else resultVal = leftVal / rightVal;
                System.out.printf("%s / %s = %s%n", leftVal, rightVal, resultVal);
                break;
        }
        if (intLeaf) {
            return new Leaf<>(((T) new Integer(resultVal.intValue()))); // T is Integer
        } else return new Leaf<>((T) resultVal); // T is Double
    }

    private static <T extends Number> T evaluateLeaf(Leaf<T> leaf) {
        return leaf.getValue();
    }

    public static void main(String[] args) {
        BinaryTree<Integer> expressionTree = new Node<>(
                Operator.ADD,
                new Leaf<>(1),
                new Node<>(Operator.MULTIPLY, new Leaf<>(2), new Leaf<>(3))
        );

        double result = evaluate(expressionTree);
        System.out.println("Result of expression: " + result);

        // Constructing another expression: (1.5 + 0.75) - 0.25
        BinaryTree<Double> secondTree = new Node<>(
                Operator.SUBTRACT,
                new Node<>(Operator.ADD, new Leaf<>(1.5), new Leaf<>(0.75)),
                new Leaf<>(0.25)
        );

        double secondResult = evaluate(secondTree);
        System.out.println("Result of another expression: " + secondResult);

        // Constructing for expression: (5 / 2)
        BinaryTree<Integer> thirdTree = new Node<>(
                Operator.DIVIDE,
                new Leaf<>(5),
                new Leaf<>(2)
        );

        double thirdResult = evaluate(thirdTree);
        System.out.println("Result of third expression: " + thirdResult);
        // it is ok if the output is 2.5 in this third test
    }
}
