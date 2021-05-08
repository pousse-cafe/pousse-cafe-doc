package poussecafe.doc.model;

import poussecafe.doc.model.relationdoc.Component;

import static java.util.Objects.requireNonNull;

public class Relation {

    public Component from() {
        return from;
    }

    private Component from;

    public Component to() {
        return to;
    }

    private Component to;

    public static class Builder {

        public Relation build() {
            requireNonNull(relation.from);
            requireNonNull(relation.to);
            return relation;
        }

        private Relation relation = new Relation();

        public Builder from(Component from) {
            relation.from = from;
            return this;
        }

        public Builder to(Component to) {
            relation.to = to;
            return this;
        }
    }

    private Relation() {

    }
}
