package ara

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal fun org.gradle.api.Project.getVersionCatalogLibs(): VersionCatalog =
    this.extensions.getByType<VersionCatalogsExtension>().named("libs")
