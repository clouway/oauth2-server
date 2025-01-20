package com.clouway.oauth2.token

/**
 * ServiceAccount is a representation of a service account that is used to access a service.
 *
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
data class ServiceAccount(
    val clientId: String,
    val clientEmail: String,
    val name: String,
    val customerId: Long?,
    val claims: Map<String, Any>,
    val permissions: Permissions = Permissions(allowAll = true, scopes = emptyList()), // Default to allow all
)

data class Permissions(
    val allowAll: Boolean = false, // If true, overrides other constraints and allows all operations
    val scopes: List<Scope> = emptyList(), // Specific scopes allowed for this service account
    val resourceConstraints: List<ResourceConstraint> = emptyList(), // Constraints on specific resources
)

data class Scope(
    val name: String, // Name of the scope (e.g., "read", "write", "admin")
    val description: String? = null, // Optional description of the scope
)

data class ResourceConstraint(
    val resourceType: String, // The type of resource being constrained (e.g., "bucket", "database")
    val resourceId: String, // The specific resource ID (e.g., "bucket123", "db456")
    val actions: List<String> = emptyList(), // Actions allowed on this resource (e.g., "read", "write")
)
