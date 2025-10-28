package io.github.fruit22.arch.checker;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchRules {

    public static List<ArchRule> RULES =
            List.of(
                    layeredArchitecture()
                            .consideringOnlyDependenciesInLayers()
                            .layer("Domain").definedBy("..domain..")
                            .layer("Application").definedBy("..application..")
                            .layer("Infrastructure").definedBy("..infrastructure..")
                            .whereLayer("Domain").mayNotAccessAnyLayer()
                            .whereLayer("Application").mayOnlyAccessLayers("Domain")
                            .whereLayer("Infrastructure").mayOnlyAccessLayers("Application", "Domain")
                            .because("Hexagonal architecture should be respected"),

                    classes()
                            .that().resideInAPackage("..port..")
                            .should().beInterfaces()
                            .because("Only interfaces should reside in the port package"),

                    classes()
                            .that().resideInAPackage("..usecase..")
                            .should().beAnnotatedWith("org.springframework.stereotype.Service")
                            .because("All use-case classes should be Spring @Service components"),

                    noClasses()
                            .that().resideInAnyPackage("..domain..", "..application..")
                            .should().beAnnotatedWith("org.springframework.stereotype.Repository")
                            .because("@Repository classes should reside only in the infrastructure layer"),

                    noClasses()
                            .that().resideInAnyPackage("..domain..", "..application..")
                            .should().dependOnClassesThat()
                            .resideInAnyPackage(
                                    "org.springframework.kafka..",
                                    "org.springframework.web..",
                                    "org.springframework.scheduling..",
                                    "jakarta.persistence..",
                                    "org.springframework.data..",
                                    "org.springframework.jms..",
                                    "org.springframework.mail.."
                            )
                            .because("Infrastructure libraries (Kafka, Web, Scheduling, JPA, Spring Data, JMS, Mail) should be used only in the infrastructure layer"),

                    classes()
                            .that().areAnnotatedWith("jakarta.persistence.Entity")
                            .should().haveSimpleNameEndingWith("Entity")
                            .because("All @Entity classes should have names ending with 'Entity'"),

                    classes()
                            .that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                            .should().haveSimpleNameEndingWith("Controller")
                            .because("All @RestController classes should have names ending with 'Controller'"),

                    classes()
                            .that().areAnnotatedWith("org.springframework.stereotype.Repository")
                            .should().haveSimpleNameEndingWith("Repository")
                            .allowEmptyShould(true)
                            .because("All @Repository classes should have names ending with 'Repository'"),

                    classes()
                            .that().areAnnotatedWith("org.springframework.context.annotation.Configuration")
                            .should().haveSimpleNameEndingWith(".*(Config|Configuration)$")
                            .allowEmptyShould(true)
                            .because("All @Configuration classes should have names ending with 'Config' or 'Configuration'"),

                    GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION,
                    GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS,
                    GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS,
                    GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING,
                    GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME
            );
}
