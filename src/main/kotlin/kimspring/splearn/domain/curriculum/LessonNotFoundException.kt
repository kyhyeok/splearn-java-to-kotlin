package kimspring.splearn.domain.curriculum

import kimspring.splearn.domain.shared.ErrorCode
import kimspring.splearn.domain.shared.SplearnException

class LessonNotFoundException(
    id: Long,
) : SplearnException(
        errorCode = ErrorCode.LESSON_NOT_FOUND,
        message = "수업을 찾을 수 없습니다. id: $id",
    )
