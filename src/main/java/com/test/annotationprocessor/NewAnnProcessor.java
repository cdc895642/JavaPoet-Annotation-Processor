package com.test.annotationprocessor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes(
    "com.test.annotationprocessor.NewAnn")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class NewAnnProcessor extends AbstractProcessor {

  private static final String METHOD_PREFIX = "start";
  private static final ClassName classIntent = ClassName.get("java.util", "List");
  private static final ClassName classArrayList = ClassName.get("java.util", "ArrayList");
  private static final ClassName classContext = ClassName.get("java.util", "Set");
  private Filer filer;
  private Messager messager;
  private Elements elements;
  private Map<String, String> activitiesWithPackage;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    filer = processingEnvironment.getFiler();
    messager = processingEnvironment.getMessager();
    elements = processingEnvironment.getElementUtils();
    activitiesWithPackage = new HashMap<>();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    try {
      /**
       * 1- Find all annotated element
       */
      for (Element element : roundEnvironment.getElementsAnnotatedWith(NewAnn.class)) {

        if (element.getKind() != ElementKind.CLASS) {
          messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
          return true;
        }

        TypeElement typeElement = (TypeElement) element;
        activitiesWithPackage.put(
            typeElement.getSimpleName().toString(),
            elements.getPackageOf(typeElement).getQualifiedName().toString());
      }

      /**
       * 2- Generate a class
       */
      TypeSpec.Builder navigatorClass = TypeSpec
          .classBuilder("Navigator")
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

      for (Map.Entry<String, String> element : activitiesWithPackage.entrySet()) {
        String activityName = element.getKey();
        String packageName = element.getValue();
        ClassName activityClass = ClassName.get(packageName, activityName);
        MethodSpec intentMethod = MethodSpec
            .methodBuilder(METHOD_PREFIX + activityName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(classIntent)
            .addParameter(classContext, "context")
            .addStatement("return new $T($L)", classArrayList, "context")
            .build();
        navigatorClass.addMethod(intentMethod);
      }

      /**
       * 3- Write generated class to a file
       */
      JavaFile classF = JavaFile.builder("com.annotationsample", navigatorClass.build()).build();
      classF.writeTo(filer);


    } catch (IOException e) {
      e.printStackTrace();
    }

    return true;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> a = new HashSet<>();
    a.add(NewAnn.class.getCanonicalName());
    return a;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
