package poussecafe.doc.doclet;

import java.util.Optional;
import java.util.function.Consumer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import jdk.javadoc.doclet.DocletEnvironment;
import poussecafe.doc.model.DocletServices;
import poussecafe.doc.model.aggregatedoc.AggregateDoc;
import poussecafe.doc.model.aggregatedoc.AggregateDocFactory;
import poussecafe.doc.model.aggregatedoc.AggregateDocId;
import poussecafe.doc.model.aggregatedoc.AggregateDocRepository;
import poussecafe.doc.model.entitydoc.EntityDoc;
import poussecafe.doc.model.entitydoc.EntityDocFactory;
import poussecafe.doc.model.entitydoc.EntityDocId;
import poussecafe.doc.model.entitydoc.EntityDocRepository;
import poussecafe.doc.model.relationdoc.Component;
import poussecafe.doc.model.relationdoc.ComponentType;
import poussecafe.doc.model.relationdoc.RelationDocFactory.NewRelationParameters;
import poussecafe.doc.model.vodoc.ValueObjectDoc;
import poussecafe.doc.model.vodoc.ValueObjectDocFactory;
import poussecafe.doc.model.vodoc.ValueObjectDocId;
import poussecafe.doc.model.vodoc.ValueObjectDocRepository;
import poussecafe.doc.process.ComponentLinking;

public class RelationCreator implements Consumer<TypeElement> {

    @Override
    public void accept(TypeElement classDoc) {
        if(aggregateDocFactory.isAggregateDoc(classDoc)) {
            tryRelationAggregateId(classDoc);
        }
        if(entityDocFactory.isEntityDoc(classDoc)) {
            tryRelationEntityId(classDoc);
        }
        if(aggregateDocFactory.isAggregateDoc(classDoc) || entityDocFactory.isEntityDoc(classDoc)) {
            tryAttributes(classDoc);
        }
        if(valueObjectDocFactory.isValueObjectDoc(classDoc)) {
            CodeExplorer codeExplorer = new CodeExplorer.Builder()
                    .basePackage(configuration.basePackage())
                    .rootClassDoc(classDoc)
                    .classRelationBuilder(this::classRelationBuilder)
                    .docletServices(docletServices)
                    .build();
            codeExplorer.explore();
        }
    }

    private AggregateDocFactory aggregateDocFactory;

    private EntityDocFactory entityDocFactory;

    private ValueObjectDocFactory valueObjectDocFactory;

    private PousseCafeDocletConfiguration configuration;

    private DocletServices docletServices;

    private void tryRelationAggregateId(TypeElement classDoc) {
        Optional<AggregateDoc> aggregateDoc = aggregateDocRepository.getOptional(AggregateDocId.ofClassName(classDoc.getQualifiedName().toString()));
        if(aggregateDoc.isPresent()) {
            Optional<ValueObjectDoc> idDoc = valueObjectDocRepository.getOptional(ValueObjectDocId.ofClassName(aggregateDoc.get().attributes().idClassName().value()));
            if(idDoc.isPresent()) {
                Logger.debug("Building bi-directional relation between aggregate " + classDoc.getQualifiedName() + " and its id " + aggregateDoc.get().attributes().idClassName().value());
                NewRelationParameters aggregateIdParameters = new NewRelationParameters();
                aggregateIdParameters.fromComponent = component(classDoc);
                aggregateIdParameters.toComponent = new Component(
                        ComponentType.VALUE_OBJECT,
                        aggregateDoc.get().className(),
                        aggregateDoc.get().attributes().moduleComponentDoc().value().componentDoc().name());
                componentLinking.linkComponents(aggregateIdParameters);

                NewRelationParameters idAggregateParameters = new NewRelationParameters();
                idAggregateParameters.fromComponent = aggregateIdParameters.toComponent;
                idAggregateParameters.toComponent = aggregateIdParameters.fromComponent;
                componentLinking.linkComponents(idAggregateParameters);
            }
        }
    }

    private AggregateDocRepository aggregateDocRepository;

    private ValueObjectDocRepository valueObjectDocRepository;

    private void tryRelationEntityId(TypeElement classDoc) {
        Optional<EntityDoc> entityDoc = entityDocRepository.getOptional(EntityDocId.ofClassName(classDoc.getQualifiedName().toString()));
        if(entityDoc.isPresent()) {
            Optional<ValueObjectDoc> idDoc = valueObjectDocRepository.getOptional(ValueObjectDocId.ofClassName(entityDoc.get().attributes().idClassName().value()));
            if(idDoc.isPresent()) {
                Logger.debug("Building relation between entity " + classDoc.getQualifiedName() + " and its id " + entityDoc.get().attributes().idClassName().value());
                NewRelationParameters entityIdParameters = new NewRelationParameters();
                entityIdParameters.fromComponent = component(classDoc);
                entityIdParameters.toComponent = new Component(
                        ComponentType.VALUE_OBJECT,
                        idDoc.get().className(),
                        idDoc.orElseThrow().attributes().moduleComponentDoc().value().componentDoc().name());
                componentLinking.linkComponents(entityIdParameters);
            }
        }
    }

    private EntityDocRepository entityDocRepository;

    private Component component(TypeElement classDoc) {
        String className = classDoc.getQualifiedName().toString();
        if(aggregateDocFactory.isAggregateDoc(classDoc)) {
            var aggregate = aggregateDocRepository.get(AggregateDocId.ofClassName(className));
            return new Component(
                    ComponentType.AGGREGATE,
                    aggregate.className(),
                    aggregate.attributes().moduleComponentDoc().value().componentDoc().name());
        } else if(entityDocFactory.isEntityDoc(classDoc)) {
            var entity = entityDocRepository.get(EntityDocId.ofClassName(className));
            return new Component(
                    ComponentType.ENTITY,
                    entity.className(),
                    entity.attributes().moduleComponentDoc().value().componentDoc().name());
        } else if(valueObjectDocFactory.isValueObjectDoc(classDoc)) {
            var valueObject = valueObjectDocRepository.get(ValueObjectDocId.ofClassName(className));
            return new Component(
                    ComponentType.VALUE_OBJECT,
                    valueObject.className(),
                    valueObject.attributes().moduleComponentDoc().value().componentDoc().name());
        } else {
            throw new IllegalArgumentException("Unsupported component class " + classDoc.getQualifiedName().toString());
        }
    }

    private ComponentLinking componentLinking;

    private void classRelationBuilder(TypeElement from, TypeElement to) {
        if(from != to) {
            linkComponents(from, to);
        }
    }

    private void linkComponents(TypeElement from,
            TypeElement to) {
        Logger.debug("Building relation between " + from.getQualifiedName() + " and " + to.getQualifiedName());
        NewRelationParameters parameters = new NewRelationParameters();
        parameters.fromComponent = component(from);
        parameters.toComponent = component(to);
        componentLinking.linkComponents(parameters);
    }

    private void tryAttributes(TypeElement classDoc) {
        TypeElement rootClassDoc;
        if(aggregateDocFactory.isStandaloneRoot(classDoc)
                || entityDocFactory.isEntityDoc(classDoc)) {
            rootClassDoc = classDoc;
        } else {
            rootClassDoc = aggregateDocFactory.innerRoot(classDoc).orElseThrow();
        }

        DeclaredType superclass = (DeclaredType) rootClassDoc.getSuperclass();
        TypeElement attributesClassDoc = (TypeElement) docletEnvironment.getTypeUtils().asElement(superclass.getTypeArguments().get(1));
        CodeExplorer pathFinder = new CodeExplorer.Builder()
                .rootClassDoc(classDoc)
                .basePackage(configuration.basePackage())
                .classRelationBuilder(this::classRelationBuilder)
                .docletServices(docletServices)
                .build();
        pathFinder.explore(attributesClassDoc);
    }

    private DocletEnvironment docletEnvironment;
}
