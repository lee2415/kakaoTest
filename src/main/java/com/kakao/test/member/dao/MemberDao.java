package com.kakao.test.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kakao.test.member.entity.Member;

public interface MemberDao extends JpaRepository<Member, String>{

}
