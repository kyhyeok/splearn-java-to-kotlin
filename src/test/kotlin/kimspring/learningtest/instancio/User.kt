package kimspring.learningtest.instancio

data class User(
    var id: Long? = null,
    var name: String? = null,
    var email: String? = null,
    var status: UserStatus? = null,
)
