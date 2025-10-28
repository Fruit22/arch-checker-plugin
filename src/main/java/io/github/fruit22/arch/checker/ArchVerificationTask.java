package io.github.fruit22.arch.checker;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.EvaluationResult;
import com.tngtech.archunit.lang.FailureReport;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.JavaCompile;

import java.io.File;
import java.util.stream.Collectors;


public abstract class ArchVerificationTask extends DefaultTask {

    @Input
    public abstract ListProperty<String> getExcludedRules();

    @TaskAction
    public void verifyArchitecture() {
        var classes = importCompiledClasses();
        String failures = ArchRules.RULES.stream()
                .map(rule -> rule.evaluate(classes))
                .filter(EvaluationResult::hasViolation)
                .map(EvaluationResult::getFailureReport)
                .map(FailureReport::toString)
                .collect(Collectors.joining(System.lineSeparator()));

        if (!failures.isEmpty()) {
            throw new GradleException("ArchUnit rule violations detected:"
                    + System.lineSeparator() + System.lineSeparator() + failures);
        }
    }

    private JavaClasses importCompiledClasses() {
        var outputDirs = getProject().getTasks().withType(JavaCompile.class)
                .stream()
                .map(JavaCompile::getDestinationDirectory)
                .map(DirectoryProperty::get)
                .map(Directory::getAsFile)
                .filter(File::exists)
                .filter(File::isDirectory)
                .map(File::toPath)
                .toList();
        return new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPaths(outputDirs);
    }

}
