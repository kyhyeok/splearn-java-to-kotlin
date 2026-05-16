package kimspring.splearn.application.member.provided;

class MemberRegisterManualTest {
//    @Test
//    void registerTestStub() {
//        MemberRegister register = new MemberService(
//                new MemberRepositoryStub(),
//                new EmailSenderStub(),
//                MemberFixture.createPasswordEncoder()
//        );
//
//        Member member = register.register(MemberFixture.createMemberRegisterRequest());
//
//        assertThat(member).isNotNull();
//        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
//    }
//
//    @Test
//    void registerTestMock() {
//        EmailSenderMock emailSenderMock = new EmailSenderMock();
//
//        MemberRegister register = new MemberService(
//                new MemberRepositoryStub(),
//                emailSenderMock,
//                MemberFixture.createPasswordEncoder()
//        );
//
//        Member member = register.register(MemberFixture.createMemberRegisterRequest());
//
//        assertThat(member).isNotNull();
//        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
//
//        assertThat(emailSenderMock.getTos()).hasSize(1);
//        assertThat(emailSenderMock.getTos().getFirst()).isEqualTo(member.getEmail());
//    }
//
//    @Test
//    void registerTestMockito() {
//        EmailSender emailSenderMock = Mockito.mock(EmailSender.class);
//
//        MemberRegister register = new MemberService(
//                new MemberRepositoryStub(),
//                emailSenderMock,
//                MemberFixture.createPasswordEncoder()
//        );
//
//        Member member = register.register(MemberFixture.createMemberRegisterRequest());
//
//        assertThat(member).isNotNull();
//        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
//
//        Mockito.verify(emailSenderMock).send(eq(member.getEmail()), any(), any());
//    }
//
//
//    static class MemberRepositoryStub implements MemberRepository {
//
//        @Override
//        public Member save(Member member) {
//            ReflectionTestUtils.setField(member, "id", 1L);
//            return member;
//        }
//
//        @Override
//        public Optional<Member> findByEmail(Email email) {
//            return Optional.empty();
//        }
//
//        @Override
//        public Optional<Member> findById(Long memberId) {
//            return Optional.empty();
//        }
//    }
//
//    static class EmailSenderStub implements EmailSender {
//
//        @Override
//        public void send(Email email, String subject, String body) {
//
//        }
//    }
//
//    static class EmailSenderMock implements EmailSender {
//        List<Email> tos = new ArrayList<>();
//
//        public List<Email> getTos() {
//            return tos;
//        }
//
//        @Override
//        public void send(Email email, String subject, String body) {
//            tos.add(email);
//        }
//    }
}