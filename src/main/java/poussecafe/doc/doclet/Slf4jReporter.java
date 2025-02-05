package poussecafe.doc.doclet;

import com.sun.source.util.DocTreePath;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import jdk.javadoc.doclet.Reporter;
import org.slf4j.LoggerFactory;


public class Slf4jReporter implements Reporter {

    @Override
    public void print(Kind kind, String msg) {
        if(kind == Kind.ERROR) {
            logger.error(msg);
        } else if(kind == Kind.MANDATORY_WARNING || kind == Kind.WARNING) {
            logger.warn(msg);
        } else if(kind == Kind.NOTE) {
            logger.info(msg);
        } else {
            logger.debug(msg);
        }
    }

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void print(Kind kind, DocTreePath path, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void print(Kind kind, Element e, String msg) {
        throw new UnsupportedOperationException();
    }
}
