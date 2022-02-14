/***
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package crappy.internals.validators;

import crappy.internals.CrappyWarning;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;

/**
 * An attempt at making a validator to check for CrappyWarning annotations, probably doesn't even work.
 */
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
