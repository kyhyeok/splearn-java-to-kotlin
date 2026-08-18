package kimspring.learningtest.archunit.slice.mutation.caller

import kimspring.learningtest.archunit.slice.mutation.target.Target

class Caller {
    fun misuse(target: Target): Target = target.activate()

    fun allowedRead(target: Target): Boolean = target.isActive()
}
