package io.github.fruit22.arch.checker;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ArchCheckerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("archCheck", ArchVerificationTask.class, task -> {
            task.setGroup("verification");
            task.setDescription("Checks project architecture with ArchUnit");
            task.dependsOn(project.getTasks().getByName("compileJava"));
        });

        project.getTasks().named("check").configure(check -> {
            check.dependsOn("archCheck");
        });
    }


}
