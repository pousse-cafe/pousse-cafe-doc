package poussecafe.doc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.Module;
import poussecafe.doc.model.Relation;
import poussecafe.doc.model.relationdoc.Component;
import poussecafe.doc.model.relationdoc.ComponentType;
import poussecafe.source.model.Aggregate;
import poussecafe.source.model.SourceModel;
import poussecafe.source.model.TypeComponent;
import poussecafe.source.model.TypeReference;

@Builder(builderClassName = "Builder")
public class PousseCafeDocGenerator {

    public void generate() {
        var domain = buildDomain();
        writeGraphs(domain);
        writeHtml(domain);
        writePdf();
    }

    private Domain buildDomain() {
        return new Domain.Builder()
                .name(configuration.domainName())
                .version(configuration.version())
                .modules(modules())
                .relations(relations())
                .build();
    }

    private PousseCafeDocGenerationConfiguration configuration;

    private List<Module> modules() {
        var modules = new ArrayList<Module>();
        for(TypeComponent module : model.modules()) {
            var builder = ModuleBuilder.builder()
                    .model(model)
                    .module(module)
                    .build();
            modules.add(builder.build());
        }
        return modules;
    }

    private SourceModel model;

    private List<Relation> relations() {
        var relations = new HashSet<Relation>();
        model.aggregates().forEach(aggregate -> addRelations(relations, component(aggregate), aggregate.rootReferences()));
        model.entities().forEach(entity -> addRelations(relations, component(ComponentType.ENTITY, entity), entity.references()));
        model.valueObjects().forEach(valueObject -> addRelations(relations, component(ComponentType.VALUE_OBJECT, valueObject), valueObject.references()));
        return new ArrayList<>(relations);
    }

    private Component component(Aggregate aggregate) {
        return new Component(ComponentType.AGGREGATE, aggregate.className().asName(), aggregate.name());
    }

    private void addRelations(Set<Relation> relations, Component from, List<TypeReference> references) {
        for(TypeReference reference : references) {
            var relation = new Relation.Builder()
                    .from(from)
                    .to(new Component(componentType(reference.type()), reference.typeClassName(), reference.typeClassName().simple()))
                    .build();
            relations.add(relation);
        }
    }

    private Component component(ComponentType type, TypeComponent typeComponent) {
        return new Component(type, typeComponent.typeName().asName(), typeComponent.typeName().simpleName());
    }

    private ComponentType componentType(poussecafe.source.model.ComponentType type) {
        return ComponentType.valueOf(type.name());
    }

    private void writeGraphs(Domain domain) {
        GraphImagesWriter graphsWriter = GraphImagesWriter.builder()
                .customDotExecutable(configuration.customDotExecutable())
                .customFdpExecutable(configuration.customFdpExecutable())
                .outputDirectoryPath(configuration.outputDirectory())
                .build();
        graphsWriter.writeImages(domain);
    }

    private void writeHtml(Domain domain) {
        var htmlWriter = new HtmlWriter.Builder()
                .outputDirectoryPath(configuration.outputDirectory())
                .includeGenerationDate(configuration.includeGenerationDate())
                .build();
        htmlWriter.writeHtml(domain);
    }

    private void writePdf() {
        var pdfWriter = new PdfWriter(configuration);
        pdfWriter.writePdf();
    }
}
