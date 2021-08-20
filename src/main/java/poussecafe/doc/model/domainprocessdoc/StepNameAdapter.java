package poussecafe.doc.model.domainprocessdoc;

import poussecafe.attribute.adapters.DataAdapter;

public class StepNameAdapter implements DataAdapter<String, DomainProcessGraphNodeName> {

    @Override
    public DomainProcessGraphNodeName adaptGet(String storedValue) {
        return new DomainProcessGraphNodeName(storedValue);
    }

    @Override
    public String adaptSet(DomainProcessGraphNodeName valueToStore) {
        return valueToStore.stringValue();
    }

    public static StepNameAdapter instance() {
        return INSTANCE;
    }

    private static final StepNameAdapter INSTANCE = new StepNameAdapter();
}
