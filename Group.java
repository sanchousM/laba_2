package task;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class Group {
    public static Group grouping(Deque<Token> tokens, boolean isRoot) {
        var elements = new ArrayList<Token>();
        while (!tokens.isEmpty()) {
            var token = tokens.pop();
            if (token instanceof Token.BracketsOpenToken) {
                var group = grouping(tokens, false);
                elements.add(group.evaluate());
                continue;
            }
            if (token instanceof Token.BracketsCloseToken) {
                if (isRoot) {
                    throw new IllegalStateException("unexpected close bracket");
                }
                return new Group(elements);
            }
            elements.add(token);
        }
        if (!isRoot) {
            throw new IllegalStateException("unexpected open bracket");
        }
        return new Group(elements);
    }

    private final List<Token> tokens;

    public Group(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token.NumericToken evaluate() {
        try {
            walkUnaryRight(this.tokens, Token.SinToken.class);
            walkUnaryRight(this.tokens, Token.CosToken.class);
            walkBinary(this.tokens, Token.PowToken.class);
            walkBinary(this.tokens, Token.MulToken.class);
            walkBinary(this.tokens, Token.DivToken.class);
            walkBinary(this.tokens, Token.AddToken.class);
            walkBinary(this.tokens, Token.SubToken.class);
        } catch (Exception ex) {
            throw new IllegalStateException("invalid expression");
        }
        if (this.tokens.size() == 1) {
            return (Token.NumericToken) this.tokens.get(0);
        } else {
            throw new IllegalStateException("invalid expression");
        }
    }

    private static void walkBinary(final List<Token> tokens, Class<? extends Token> clazz) {
        for (int i = 0; i < tokens.size(); i++) {
           var curr = tokens.get(i);
           if (curr.getClass() == clazz) {
               tokens.set(i + 1, curr.consume(tokens.get(i - 1), tokens.get(i + 1)));
               tokens.set(i, null);
               tokens.set(i - 1, null);
           }
        }
        tokens.removeIf(Objects::isNull);
    }

    private static void walkUnaryRight(final List<Token> tokens, Class<? extends Token> clazz) {
        for (int i = 0; i < tokens.size(); i++) {
            var curr = tokens.get(i);
            if (curr.getClass() == clazz) {
                tokens.set(i + 1, curr.consume(tokens.get(i + 1)));
                tokens.set(i, null);
            }
        }
        tokens.removeIf(Objects::isNull);
    }
}
