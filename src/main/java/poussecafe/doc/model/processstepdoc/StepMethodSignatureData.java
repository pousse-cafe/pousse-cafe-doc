package poussecafe.doc.model.processstepdoc;

import java.io.Serializable;
import java.util.Optional;
import poussecafe.attribute.AutoAdapter;
import poussecafe.doc.model.domainprocessdoc.ComponentMethodName;

@SuppressWarnings("serial")
public class StepMethodSignatureData implements Serializable, AutoAdapter<StepMethodSignature> {

    public static StepMethodSignatureData adapt(StepMethodSignature signature) {
        StepMethodSignatureData data = new StepMethodSignatureData();
        data.componentName = signature.componentMethodName().componentName();
        data.methodName = signature.componentMethodName().methodName();
        data.eventName = signature.consumedEventName().orElse(null);
        return data;
    }

    private String componentName;

    private String methodName;

    private String eventName;

    public StepMethodSignature adapt() {
        return new StepMethodSignature.Builder()
                .componentMethodName(new ComponentMethodName.Builder()
                        .componentName(componentName)
                        .methodName(methodName)
                        .build())
                .consumedMessageName(Optional.ofNullable(eventName))
                .build();
    }
}
