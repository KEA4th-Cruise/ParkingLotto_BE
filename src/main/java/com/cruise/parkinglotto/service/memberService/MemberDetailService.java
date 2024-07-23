package com.cruise.parkinglotto.service.memberService;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.AccountType;
import com.cruise.parkinglotto.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        return memberRepository.findByAccountId(accountId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {

        if (member.getAccountType() == AccountType.ADMIN) {
            return User.builder()
                    .username(member.getAccountId())
                    .password(member.getPassword())
                    .roles("ADMIN")
                    .build();
        } else {
            return User.builder()
                    .username(member.getAccountId())
                    .password(member.getPassword())
                    .roles("USER")
                    .build();
        }
    }
}
