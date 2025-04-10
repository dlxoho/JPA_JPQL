package org.example;

import javax.persistence.*;
import java.util.List;

public class Main {
  public static void main(String[] args) {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    EntityManager em = emf.createEntityManager();

    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      Team team = new Team();
      team.setName("testTeam");
      em.persist(team);

      Member member = new Member();
      member.setUsername("test");
      member.setAge(10);
      member.setTeam(team);
      em.persist(member);

      // # Type Query : 반환 타입이 명확한 경우 사용
      // # Query : 반환 타입이 명확하지 않은 경우 사용

      // # 결과조회
      // - getResultList() : 결과가 하나 이상인 경우
      List<MemberDTO> result = em.createQuery("select new org.example.MemberDTO(m.username,m.age) from Member m",MemberDTO.class)
        .getResultList();

      MemberDTO memberDTO = result.get(0);
      System.out.println(memberDTO.getUsername());

      // - getSingleResult() : 결과가 하나
      // 결과가 없거나 2개 이상인 경우 예외가 발생
      TypedQuery<Member> query  = em.createQuery("select m from Member m where m.username = :username",Member.class);
      query.setParameter("username", "test");
      query.getSingleResult();

      // 페이징
      // - setFirstResult : 조회 시작 위치
      // - setMaxResult : 조회 마지막 위치
      List<Member> pageResult = em.createQuery("select m from Member m order by m.age desc", Member.class)
          .setFirstResult(0)
            .setMaxResults(10)
              .getResultList();
      for (Member member1 : pageResult) {
        System.out.println(member1);
      }

      // 조인
      String joinQuery = "select m from Member m join m.team t";
      List<Member> joinResult = em.createQuery(joinQuery, Member.class)
        .getResultList();

      // 경로표현식
      // 1. 상태필드 : 경로 탐색의 끝. 탐색 X
      // 2. 단일 값 연관 경로 : 묵시적 내부 조인발생. 탐색 O
      // 3. 컬렉션 값 연관 경로 : 묵시적 내부 조인발생. 탐색 X
      // 실무에서 쿼리튜닝이 어렵기 때문에 웬만하면 묵시적 조인이 발생하게는 하면 안된다. 직관적인게 좋음.

      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      throw new RuntimeException(e);
    } finally {
      em.close();
    }
    emf.close();
  }
}