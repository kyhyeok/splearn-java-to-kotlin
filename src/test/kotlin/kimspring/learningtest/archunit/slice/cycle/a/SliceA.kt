package kimspring.learningtest.archunit.slice.cycle.a

import kimspring.learningtest.archunit.slice.cycle.b.SliceB

class SliceA(
    val b: SliceB?,
)
