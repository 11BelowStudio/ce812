package crappy.internals.validators;

import crappy.internals.CrappyWarning;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;


public class CrappyWarningValidator extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes(){
        return Collections.singleton(CrappyWarning.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(CrappyWarning.class).forEach(
                e -> {
                    this.processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.WARNING,
                            "@CrappyWarning: " + e.getAnnotation(CrappyWarning.class).value(),
                            e
                    );
                }
        );
        return true;
    }
}
