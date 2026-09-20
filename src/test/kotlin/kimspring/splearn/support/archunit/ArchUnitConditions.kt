package kimspring.splearn.support.archunit

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage
import com.tngtech.archunit.core.domain.JavaMethodCall
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.library.dependencies.Slice

internal fun onlyCallAllowedMethodsOfOtherSlices(): ArchCondition<Slice> =
    object : ArchCondition<Slice>("다른 슬라이스의 getter, data class 구조적 메서드, enum 메서드만 호출할 수 있다") {
        private val classesInAnySlice = mutableSetOf<JavaClass>()

        override fun init(allSlices: Collection<Slice>) {
            classesInAnySlice.addAll(allSlices.flatten())
        }

        override fun check(
            slice: Slice,
            events: ConditionEvents,
        ) {
            slice
                .flatMap { it.methodCallsFromSelf }
                .filterNot { it.targetOwner in slice }
                .filter { it.targetOwner in classesInAnySlice }
                .disallowedMutationCalls()
                .forEach { events.add(SimpleConditionEvent.violated(it, it.description)) }
        }
    }

internal fun onlyCallReadOnlyMethodsOfClassesIn(packageIdentifier: String): ArchCondition<JavaClass> =
    object : ArchCondition<JavaClass>("$packageIdentifier 클래스의 조회 메서드만 호출할 수 있다") {
        private val residesInTargetPackage = resideInAPackage(packageIdentifier)

        override fun check(
            javaClass: JavaClass,
            events: ConditionEvents,
        ) {
            javaClass.methodCallsFromSelf
                .filter { residesInTargetPackage.test(it.targetOwner) }
                .disallowedMutationCalls()
                .forEach { events.add(SimpleConditionEvent.violated(it, it.description)) }
        }
    }

private val ACCESSOR_PATTERN = Regex("^(get|is)[A-Z]")

private val DATA_CLASS_STRUCTURAL_METHODS = setOf("copy", "equals", "hashCode", "toString")

// data class라도 클래스 단위로 면제하지 않는다 — 이 프로젝트는 엔티티도 data class라서
// 클래스 단위 면제는 activate 같은 상태 전이 메서드 호출까지 통과시킨다
private fun Iterable<JavaMethodCall>.disallowedMutationCalls(): List<JavaMethodCall> =
    filterNot { it.targetOwner.isEnum }
        .filterNot { it.isReadOnlyCall() }
        .filterNot { call -> call.targetOwner.isKotlinDataClass() && call.target.name.isDataClassStructuralMethod() }

// 인자 없는 get/is 접근자와 ensure 계열만 조회로 본다. getOrCreate(x) 처럼 인자를 받는 get 메서드는 통과시키지 않는다.
// 이름 기반 판정의 한계로, 인자 없이 상태를 바꾸는 get 메서드까지는 막지 못한다
private fun JavaMethodCall.isReadOnlyCall(): Boolean =
    target.name.startsWith("ensure") ||
        (ACCESSOR_PATTERN.containsMatchIn(target.name) && target.rawParameterTypes.isEmpty())

// Java의 record 판정(ACC_RECORD 플래그)에 대응 — data class는 JVM record가 아니어서
// ArchUnit isRecord()로 판정할 수 없고, kotlin.Metadata를 읽는 kotlin-reflect로 판정한다
private fun JavaClass.isKotlinDataClass(): Boolean = runCatching { reflect().kotlin.isData }.getOrDefault(false)

private fun String.isDataClassStructuralMethod(): Boolean =
    this in DATA_CLASS_STRUCTURAL_METHODS || startsWith("component")
