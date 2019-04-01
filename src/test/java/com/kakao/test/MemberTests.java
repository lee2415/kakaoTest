package com.kakao.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakao.test.member.dao.MemberDao;
import com.kakao.test.member.entity.Member;
import com.kakao.test.member.service.MemberService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberTests extends InitTest {

	@Autowired
	private MemberDao memberDao;
	
	@Test
	@Transactional
	public void signupTest() {
		Member member = new Member();
		
		memberDao.save(member);
		
		Member selMember = memberDao.getOne(userId);
		
		assert member.getUserId().equals(selMember.getUserId()) && member.getUserPass().equals(selMember.getUserPass());
	}
	
	@Test
	public void findall() {
		List<Member> list = memberDao.findAll();
		assertThat(list.size(), is(0));
	}
}
