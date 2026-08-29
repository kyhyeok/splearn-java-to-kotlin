package kimspring.splearn.domain.course

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class CourseNotFoundException(
    id: Long,
) : SplearnException(
        errorCode = ErrorCode.COURSE_NOT_FOUND,
        message = "강의를 찾을 수 없습니다. id: $id",
    )
