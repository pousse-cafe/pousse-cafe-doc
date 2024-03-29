package poussecafe.doc.model;

import java.io.Serializable;
import poussecafe.attribute.AutoAdapter;
import poussecafe.doc.model.moduledoc.ModuleDocId;

@SuppressWarnings("serial")
public class ModuleComponentDocData implements Serializable, AutoAdapter<ModuleComponentDoc> {

    public static ModuleComponentDocData adapt(ModuleComponentDoc moduleComponentDoc) {
        ModuleComponentDocData data = new ModuleComponentDocData();
        data.componentDoc = ComponentDocData.of(moduleComponentDoc.componentDoc());
        data.moduleId = moduleComponentDoc.moduleDocId().stringValue();
        data.moduleName = moduleComponentDoc.moduleName();
        return data;
    }

    public ComponentDocData componentDoc;

    public String moduleId;

    public String moduleName;

    @Override
    public ModuleComponentDoc adapt() {
        return new ModuleComponentDoc.Builder()
                .componentDoc(componentDoc.toModel())
                .moduleDocId(ModuleDocId.ofPackageName(moduleId))
                .moduleName(moduleName)
                .build();
    }
}
