package poussecafe.doc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import poussecafe.doc.model.relationdoc.Component;

import static java.util.Objects.requireNonNull;
import static poussecafe.util.Equality.referenceEquals;

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

    @Override
    public boolean equals(Object obj) {
        return referenceEquals(this, obj).orElse(other -> new EqualsBuilder()
                .append(from, other.from)
                .append(to, other.to)
                .build());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(from)
                .append(to)
                .build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(from)
                .append(to)
                .build();
    }
}
