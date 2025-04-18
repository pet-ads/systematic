package br.all.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes


@AnalyzeClasses(packages = ["br.all"], importOptions = [ImportOption.DoNotIncludeTests::class])
class DomainModuleTest {

    @ArchTest
    fun `should domain have no dependencies`(importedClasses: JavaClasses) =
        noClasses().that()
            .resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideOutsideOfPackages(
                "..domain..",
                "java..",
                "kotlin..",
                "org.jetbrains..",
                "..kfaker..")
            .check(importedClasses)

    @ArchTest
    fun `should domain only be accessed by application or itself`(importedClasses: JavaClasses) =
        classes().that().resideInAPackage("..domain..")
            .should().onlyBeAccessed().byAnyPackage("..application..", "..domain..")
            .check(importedClasses)//

    @ArchTest
    fun `should shared classes in domain have not dependencies outside shared package`(importedClasses: JavaClasses) =
        noClasses().that().resideInAPackage("br.all.domain.shared..")
            .should().dependOnClassesThat().resideOutsideOfPackages(
                "br.all.domain.shared..",
                "java..", "kotlin..",
                "org.jetbrains..",
                "..kfaker..")
            .check(importedClasses)

    @ArchTest
    fun `should application not depend on infrastructure`(importedClasses: JavaClasses) =
        noClasses().that().resideInAPackage("..application..")
            .should().accessClassesThat().resideInAPackage("..infrastructure..")
            .check(importedClasses)

    @ArchTest
    fun `should application only be accessed by infrastructure, service or itself`(importedClasses: JavaClasses) =
        classes().that().resideInAPackage("..application..")
            .should().onlyBeAccessed().byAnyPackage("..application..", "..infrastructure..", "..controller..")
            .check(importedClasses)

    @ArchTest
    fun `should application repositories be abstractions`(importedClasses: JavaClasses) =
        classes().that()
            .resideInAPackage("..application..")
            .and()
            .haveSimpleNameEndingWith("Repository")
            .should().beInterfaces()
            .check(importedClasses)

    @ArchTest
    fun `should concrete repositories be located inside infrastructure`(importedClasses: JavaClasses) =
        classes().that()
            .haveSimpleNameEndingWith("Repository")
            .and()
            .resideOutsideOfPackages("..application..")
            .should().resideInAPackage("..infrastructure..")
            .check(importedClasses)

}