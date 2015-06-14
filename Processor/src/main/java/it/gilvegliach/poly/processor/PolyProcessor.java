package it.gilvegliach.poly.processor;

import com.google.auto.service.AutoService;
import dagger.Component;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gil
 */
@AutoService(Processor.class)
public class PolyProcessor extends AbstractProcessor {
    private Elements mElementUtils;
    private Messager mMessenger;
    private Filer mFiler;

    @Override
    public void init(ProcessingEnvironment env) {
        mElementUtils = env.getElementUtils();
        mMessenger = env.getMessager();
        mFiler = env.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<String>();
        annotations.add(Component.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Component.class)) {
                TypeElement annotatedTypeElement = (TypeElement) annotatedElement;
                ComponentModel componentModel = buildComponentModel(annotatedTypeElement);
                generateCode(annotatedTypeElement, componentModel);
            }
        } catch (Exception e) {
            logError(e.toString());
        }

        return false;
    }

    private void generateCode(TypeElement originElement, ComponentModel componentModel) throws IOException {
        JavaFileObject fileObject = mFiler.createSourceFile(componentModel.generatingFile, originElement);
        Writer writer = fileObject.openWriter();

        try {
            writer.write(componentModel.generateString());
        } finally {
            if (writer != null) writer.close();
        }
    }

    private ComponentModel buildComponentModel(TypeElement annotatedElement) {
        TypeElement componentType = annotatedElement;
        PackageElement componentPackage = mElementUtils.getPackageOf(componentType);

        String componentPackageString = componentPackage.getQualifiedName().toString();
        String componentTypeFullString = componentType.getQualifiedName().toString();
        String componentTypeSimpleString = componentType.getSimpleName().toString();
        ComponentModel componentModel = new ComponentModel(componentPackageString,
                componentTypeFullString, componentTypeSimpleString);

        List<? extends Element> members = componentType.getEnclosedElements();
        for (Element member : members) {
            if (member.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) member;

                // TODO: add error handling
                String methodNameString = method.getSimpleName().toString();
                if (!methodNameString.startsWith("inject")) continue;

                List<? extends VariableElement> parameters = method.getParameters();
                if (parameters.size() != 1) continue;

                VariableElement parameter = parameters.get(0);
                String parameterTypeString = parameter.asType().toString();
                componentModel.addMethod(methodNameString, parameterTypeString);
            }
        }
        return componentModel;
    }

    private void logError(String msg) {
        mMessenger.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    private void logOther(String msg, Element element) {
        mMessenger.printMessage(Diagnostic.Kind.OTHER, msg, element);
    }
}

class ComponentModel {
    String componentPackage;
    String componentTypeFull;
    String componentTypeSimple;
    String generatingFile;

    private final List<InjectMethodModel> mInjectMethods = new ArrayList<InjectMethodModel>();

    public ComponentModel(String componentPackage, String componentTypeFull, String componentTypeSimple) {
        this.componentPackage = componentPackage;
        this.componentTypeFull = componentTypeFull;
        this.componentTypeSimple = componentTypeSimple;
        this.generatingFile =  String.format("%s.Poly%sWrapper", componentPackage, componentTypeSimple);
    }

    void addMethod(String methodName, String parameterType) {
        mInjectMethods.add(new InjectMethodModel(methodName, parameterType));
    }

    String generateString() {
        StringBuilder code = new StringBuilder();
        String typeFull = componentTypeFull;
        String typeSimple = componentTypeSimple;
        String pkg = componentPackage;
        String header = String.format("" +
                "package %s;\n" +
                "\n" +
                "public class Poly%sWrapper implements %s {\n" +
                "    private final %s mComponent;\n" +
                "\n" +
                "    public Poly%sWrapper(%s component) {\n" +
                "        mComponent = component;\n" +
                "    }\n" +
                "\n", pkg, typeSimple, typeFull, typeFull, typeSimple, typeFull);
        code.append(header);

        for (InjectMethodModel injectMethod : mInjectMethods) {
            String methodCode = String.format("" +
                    "    @Override\n" +
                    "    public void %s(%s o) {\n" +
                    "        mComponent.inject(o);\n" +
                    "    }\n" +
                    "\n", injectMethod.methodName, injectMethod.parameterTypeFull);
            code.append(methodCode);
        }

        code.append("    public void inject(Object o) {\n");

        String elseStr = "";
        for (InjectMethodModel injectMethod : mInjectMethods) {
            String ifClauseCode = String.format("" +
                            "        %sif (o instanceof %s) {\n" +
                            "            mComponent.inject((%s) o);\n",
                    elseStr, injectMethod.parameterTypeFull, injectMethod.parameterTypeFull);
            elseStr = "} else ";
            code.append(ifClauseCode);
        }

        code.append("        } else {\n" +
                "            throw new AssertionError(\"Object not recognized\");\n" +
                "        }\n" +
                "    }\n" +
                "}\n");

        return code.toString();
    }
}

class InjectMethodModel {
    final String methodName;
    final String parameterTypeFull; // full name

    public InjectMethodModel(String methodName, String parameterTypeFull) {
        this.methodName = methodName;
        this.parameterTypeFull = parameterTypeFull;
    }
}