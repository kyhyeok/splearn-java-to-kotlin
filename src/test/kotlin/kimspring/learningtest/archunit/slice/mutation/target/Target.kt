package kimspring.learningtest.archunit.slice.mutation.target

data class Target(
    val active: Boolean,
) {
    fun activate(): Target = copy(active = true)

    fun isActive(): Boolean = active
}
