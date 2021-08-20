package poussecafe.doc.model.domainprocessdoc;

import poussecafe.doc.model.processstepdoc.StepMethodSignature;
import poussecafe.util.StringId;

public class DomainProcessGraphNodeName extends StringId {

    public DomainProcessGraphNodeName(String value) {
        super(value);
    }

    public DomainProcessGraphNodeName(StepMethodSignature signature) {
        super(signature.toString());
    }
}
