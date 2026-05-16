package kimspring.splearn.adapter.out.persistence

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface SpringMemberRepository : CrudRepository<MemberJdbcEntity, Long> {
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
        WHERE member.email_address = :emailAddress
        """,
    )
    fun findByEmailAddress(emailAddress: String): MemberJdbcEntity?

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
        WHERE md.profile_address = :profileAddress
        """,
    )
    fun findByProfileAddress(profileAddress: String): MemberJdbcEntity?
}
