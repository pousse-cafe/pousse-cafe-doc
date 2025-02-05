package poussecafe.doc.model;

import java.util.Optional;
import javax.lang.model.element.Element;

import poussecafe.annotations.ShortDescription;
import poussecafe.annotations.Trivial;
import poussecafe.doc.annotations.AnnotationUtils;
import poussecafe.domain.Service;

public class ComponentDocFactory implements Service {

    public ComponentDoc buildDoc(String name, Element doc) {
        return new ComponentDoc.Builder()
                .name(name)
                .description(annotationsResolver.renderCommentBody(doc))
                .shortDescription(shortDescription(doc))
                .trivial(annotationsResolver.isTrivial(doc)
                        || AnnotationUtils.annotation(doc, Trivial.class).isPresent())
                .build();
    }

    private Optional<String> shortDescription(Element doc) {
        var shortAnnotation = AnnotationUtils.annotation(doc, ShortDescription.class);
        if(shortAnnotation.isPresent()) {
            return Optional.of((String) AnnotationUtils.value(shortAnnotation.orElseThrow(), "value").orElseThrow().getValue());
        } else {
            return annotationsResolver.shortDescription(doc);
        }
    }

    private AnnotationsResolver annotationsResolver;
}
