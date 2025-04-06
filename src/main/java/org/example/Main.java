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
      Member member = new Member();
      member.setUsername("test");
      member.setAge(10);
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