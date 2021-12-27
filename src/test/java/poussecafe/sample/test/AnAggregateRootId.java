package poussecafe.sample.test;

import poussecafe.domain.ValueObject;
import poussecafe.util.StringId;

public class AnAggregateRootId extends StringId implements ValueObject {

    public AnAggregateRootId(String value) {
        super(value);
    }
}
