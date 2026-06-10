package orm;

//import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Set;

//@AutoService(Processor.class)
@SupportedAnnotationTypes("orm.GenerateOrmEntity")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class OrmAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateOrmEntity.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                generateEntityClass(classElement);
            }
        }
        return true;
    }

    private void generateEntityClass(TypeElement classElement) {
        String blueprintClassName = classElement.getSimpleName().toString();
        String targetClassName = blueprintClassName + "Entity";
        String packageName = processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();

        GenerateOrmEntity annotation = classElement.getAnnotation(GenerateOrmEntity.class);
        String tableName = annotation.tableName();

        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + targetClassName);

            try (Writer writer = sourceFile.openWriter()) {
                writer.write("package " + packageName + ";\n\n");

                writer.write("@orm.DbTable(name = \"" + tableName + "\")\n");
                writer.write("public class " + targetClassName + " {\n\n");

                for (Element enclosedElement : classElement.getEnclosedElements()) {
                    if (enclosedElement.getKind() == ElementKind.FIELD) {
                        String fieldName = enclosedElement.getSimpleName().toString();
                        String fieldType = enclosedElement.asType().toString();

                        writer.write("    @orm.DbColumn(name = \"" + fieldName.toLowerCase() + "\")\n");
                        writer.write("    private " + fieldType + " " + fieldName + ";\n\n");
                    }
                }

                writer.write("    public " + targetClassName + "() {}\n\n");


                writer.write("}\n");
            }
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "Помилка генерації коду: " + e.getMessage());
        }
    }
}