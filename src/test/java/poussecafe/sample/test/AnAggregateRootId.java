package poussecafe.sample.test;

import poussecafe.annotations.Trivial;
import poussecafe.domain.ValueObject;
import poussecafe.util.StringId;

/**
 * @trivial
 */
@Trivial
public class AnAggregateRootId extends StringId implements ValueObject {

    public AnAggregateRootId(String value) {
        super(value);
    }
}
