/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/

package io.ktor.server.config

import io.ktor.server.application.Application
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

/**
 * Represents an application config node
 *
 * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfig)
 */
public interface ApplicationConfig {
    /**
     * Get config property with [path] or fail
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfig.property)
     *
     * @throws ApplicationConfigurationException
     */
    public fun property(path: String): ApplicationConfigValue

    /**
     * Get config property value for [path] or return `null`
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfig.propertyOrNull)
     */
    public fun propertyOrNull(path: String): ApplicationConfigValue?

    /**
     * Get config child node or fail
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfig.config)
     *
     * @throws ApplicationConfigurationException
     */
    public fun config(path: String): ApplicationConfig

    /**
     * Get a list of child nodes for [path] or fail
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfig.configList)
     *
     * @throws ApplicationConfigurationException
     */
    public fun configList(path: String): List<ApplicationConfig>

    /**
     * Returns the set of keys, found by recursing the root object.
     * All entries represent leaf nodes' keys, meaning that there would be no nested
     * objects directly included as values for returned keys.
     * It's still possible that entries may be a list and the lists may contain objects.
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfig.keys)
     *
     * @return set of paths with non-null values, built up by recursing the entire tree of
     * config and creating an entry for each leaf value.
     */
    public fun keys(): Set<String>

    /**
     * Returns map representation of this config.
     * Values can be `String`, `Map<String, Any>`, `List<String>` and `List<Map<String, Any>>`
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfig.toMap)
     */
    public fun toMap(): Map<String, Any?>
}

/**
 * Represents an application config value
 *
 * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfigValue)
 */
public interface ApplicationConfigValue {
    /**
     * Get property string value
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfigValue.getString)
     */
    public fun getString(): String

    /**
     * Get property list value
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfigValue.getList)
     */
    public fun getList(): List<String>
}

/**
 * Represents an application config value that can be converted to an arbitrary type through
 * a serialization or parsing mechanism.
 */
public interface SerializableConfigValue : ApplicationConfigValue {
    public fun getAs(type: TypeInfo): Any?
}

/**
 * Convenience function for accessing properties using serialization.
 */
public inline fun <reified E> Application.property(key: String): E =
    environment.config.property(key).getAs()

/**
 * Convenience function for accessing properties using serialization.
 */
public inline fun <reified E> Application.propertyOrNull(key: String): E? =
    environment.config.propertyOrNull(key)?.getAs()

/**
 * Converts the application config value to the given type parameter.
 */
public inline fun <reified E> ApplicationConfigValue.getAs(): E =
    if (E::class == String::class) {
        getString() as E
    } else if (this !is SerializableConfigValue) {
        throw UnsupportedOperationException("Configuration implementation does not support deserialization")
    } else {
        getAs(typeInfo<E>()) as E
    }

/**
 * Thrown when an application is misconfigured
 *
 * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.ApplicationConfigurationException)
 */
public class ApplicationConfigurationException(message: String, cause: Throwable?) : Exception(message, cause) {
    public constructor(message: String) : this(message, null)
}

/**
 * Try read String value from [ApplicationConfig].
 *
 *
 * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.tryGetString)
 *
 * @return null if key is missing
 */
public fun ApplicationConfig.tryGetString(key: String): String? =
    propertyOrNull(key)?.getString()

/**
 * Try read String value from [ApplicationConfig].
 *
 *
 * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.config.tryGetStringList)
 *
 * @return null if key is missing
 */
public fun ApplicationConfig.tryGetStringList(key: String): List<String>? =
    propertyOrNull(key)?.getList()
