package task;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

public abstract class Token {
    private static final Map<Pattern, Class<? extends Token>> TOKENS_MAP = Map.of(
            Pattern.compile("^([0-9]*[.])?[0-9]+"), NumericToken.class,
            Pattern.compile("^\\+"), AddToken.class,
            Pattern.compile("^-"), SubToken.class,
            Pattern.compile("^\\*"), MulToken.class,
            Pattern.compile("^/"), DivToken.class,
            Pattern.compile("^\\^"), PowToken.class,
            Pattern.compile("^\\("), BracketsOpenToken.class,
            Pattern.compile("^\\)"), BracketsCloseToken.class,
            Pattern.compile("^sin"), SinToken.class,
            Pattern.compile("^cos"), CosToken.class
    );

    public static Deque<Token> tokenize(String input) {
        var result = new ArrayDeque<Token>();
        input = input.replaceAll(" ","");
        while (!input.isEmpty()) {
            var finalInput = input;
            var stringClassEntry = TOKENS_MAP.
                    entrySet().
                    stream().
                    map(x -> Map.entry(x.getKey().matcher(finalInput), x.getValue())).
                    filter(x -> x.getKey().find()).
                    map(x -> Map.entry(x.getKey().group(), x.getValue())).
                    findAny().
                    orElseThrow(() -> new IllegalArgumentException("unknown symbol near: " + finalInput));
            try {
                result.add(stringClassEntry.
                        getValue().
                        getDeclaredConstructor(String.class).
                        newInstance(stringClassEntry.getKey())
                );
            } catch (InstantiationException |
                    NoSuchMethodException |
                    InvocationTargetException |
                    IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            input = input.replaceFirst(Pattern.quote(stringClassEntry.getKey()), "");
        }
        return result;
    }

    public static String tokensAsString(Deque<Token> tokens) {
        return tokens.stream().reduce("", (all, x) -> all + x.toString() + "|", (x, y) -> x + y);
    }

    protected final String data;

    public Token(String data) {
        this.data = data;
    }

    public abstract NumericToken consume(Token... tokens);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "data='" + data + '\'' +
                '}';
    }

    public static final class NumericToken
            extends Token {
        protected double value;

        @SuppressWarnings("unused")
        public NumericToken(String data) {
            super(data);
            this.value = Double.parseDouble(data);
        }

        public NumericToken(double data) {
            super(String.valueOf(data));
            this.value = data;
        }

        public double getValue() {
            return value;
        }

        @Override
        public NumericToken consume(Token... tokens) {
            throw new RuntimeException();
        }

        @Override
        public String toString() {
            return "NumericToken{" +
                    "data='" + data + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    public static final class AddToken
            extends Token {
        public AddToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            var value = ((NumericToken)tokens[0]).value + ((NumericToken)tokens[1]).value;
            return new NumericToken(value);
        }
    }

    public static final class SubToken
            extends Token {
        public SubToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            var value = ((NumericToken)tokens[0]).value - ((NumericToken)tokens[1]).value;
            return new NumericToken(value);
        }
    }

    public static final class MulToken
            extends Token {
        public MulToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            var value = ((NumericToken)tokens[0]).value * ((NumericToken)tokens[1]).value;
            return new NumericToken(value);
        }
    }

    public static final class DivToken
            extends Token {
        public DivToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            var value = ((NumericToken)tokens[0]).value / ((NumericToken)tokens[1]).value;
            return new NumericToken(value);
        }
    }

    public static final class PowToken
            extends Token {
        public PowToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            var value = Math.pow(((NumericToken)tokens[0]).value, ((NumericToken)tokens[1]).value);
            return new NumericToken(value);
        }
    }

    public static final class BracketsOpenToken
            extends Token {
        public BracketsOpenToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            throw new RuntimeException();
        }
    }

    public static final class BracketsCloseToken
            extends Token {
        public BracketsCloseToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            throw new RuntimeException();
        }
    }

    public static final class SinToken
            extends Token {
        public SinToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            var value = ((NumericToken)tokens[0]).value;
            return new NumericToken(Math.sin(value));
        }
    }

    public static final class CosToken
            extends Token {
        public CosToken(String data) {
            super(data);
        }

        @Override
        public NumericToken consume(Token... tokens) {
            var value = ((NumericToken)tokens[0]).value;
            return new NumericToken(Math.cos(value));
        }
    }
}
