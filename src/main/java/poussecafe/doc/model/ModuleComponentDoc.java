package poussecafe.doc.model;

import poussecafe.doc.model.moduledoc.ModuleDocId;
import poussecafe.domain.ValueObject;

import static java.util.Objects.requireNonNull;

public class ModuleComponentDoc implements ValueObject {

    public static class Builder {

        private ModuleComponentDoc doc = new ModuleComponentDoc();

        public Builder componentDoc(ComponentDoc componentDoc) {
            doc.componentDoc = componentDoc;
            return this;
        }

        public Builder moduleDocId(ModuleDocId moduleDocId) {
            doc.moduleDocId = moduleDocId;
            return this;
        }

        public Builder moduleName(String moduleName) {
            doc.moduleName = moduleName;
            return this;
        }

        public ModuleComponentDoc build() {
            requireNonNull(doc.componentDoc);
            requireNonNull(doc.moduleDocId);
            requireNonNull(doc.moduleName);
            return doc;
        }
    }

    private ModuleComponentDoc() {

    }

    private ComponentDoc componentDoc;

    public ComponentDoc componentDoc() {
        return componentDoc;
    }

    private ModuleDocId moduleDocId;

    public ModuleDocId moduleDocId() {
        return moduleDocId;
    }

    private String moduleName;

    public String moduleName() {
        return moduleName;
    }

    public DocumentationItem.Builder toDocumentationItem() {
        return componentDoc.documentationItemBuilder()
                .moduleName(moduleName);
    }
}
