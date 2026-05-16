package kimspring.splearn.application.member.required

import kimspring.splearn.domain.member.Member
import kimspring.splearn.domain.member.Profile
import kimspring.splearn.domain.shared.Email
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface MemberRepository : CrudRepository<Member, Long> {
    @Query(
        """
        SELECT member.id                AS id,
               member.email_address     AS email_address,
               member.status            AS status,
               member.nickname          AS nickname,
               member.password_hash     AS password_hash,
               detail.id                AS detail_id,
               detail.profile_address   AS detail_profile_address,
               detail.activated_at      AS detail_activated_at,
               detail.registered_at     AS detail_registered_at,
               detail.introduction      AS detail_introduction,
               detail.deactivated_at    AS detail_deactivated_at
        FROM member
        LEFT OUTER JOIN member_detail detail ON detail.member = member.id
        WHERE member.email_address = :#{#email.address}
        """,
    )
    fun findByEmail(email: Email): Member?

    @Query(
        """
        SELECT m.id                AS id,
               m.email_address     AS email_address,
               m.status            AS status,
               m.nickname          AS nickname,
               m.password_hash     AS password_hash,
               md.id               AS detail_id,
               md.profile_address  AS detail_profile_address,
               md.activated_at     AS detail_activated_at,
               md.registered_at    AS detail_registered_at,
               md.introduction     AS detail_introduction,
               md.deactivated_at   AS detail_deactivated_at
        FROM member m
        JOIN member_detail md ON md.member = m.id
        WHERE md.profile_address = :#{#profile.address}
        """,
    )
    fun findByProfile(profile: Profile): Member?
}
