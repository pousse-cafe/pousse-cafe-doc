package poussecafe.doc.doclet;

import java.util.function.Consumer;
import javax.lang.model.element.TypeElement;
import poussecafe.doc.model.moduledoc.ModuleDocFactory;
import poussecafe.doc.process.ModuleDocCreation;

public class ClassModuleDocCreator implements Consumer<TypeElement> {

    @Override
    public void accept(TypeElement classDoc) {
        if (moduleDocFactory.isModuleDoc(classDoc)) {
            Logger.info("Adding module from class " + classDoc.getQualifiedName().toString());
            moduleDocCreation.addModuleDoc(classDoc);
        }
    }

    private ModuleDocFactory moduleDocFactory;

    private ModuleDocCreation moduleDocCreation;
}
