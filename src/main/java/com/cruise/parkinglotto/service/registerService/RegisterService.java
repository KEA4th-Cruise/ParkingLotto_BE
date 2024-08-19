package com.cruise.parkinglotto.service.registerService;

import com.cruise.parkinglotto.domain.Member;
import com.cruise.parkinglotto.domain.enums.EnrollmentStatus;
import com.cruise.parkinglotto.web.dto.registerDTO.RegisterResponseDTO;
import org.springframework.data.domain.Page;

public interface RegisterService {

    /**
     * 사용자가 등록 요청 하는 메서드
     * 사용자가 등록 요청 버튼을 누르면 -> enrollmentStatus가 prepending -> pending 으로 바뀐다.
     * 관리자는 애초에 pending, enrollment 상태의 사용자 목록만 관리한다.
     */
    Object requestRegister(Member member);

    /**
     * 관리자가 사용자의 등록 요청을 거절하는 메서드
     * 관리자가 등록된 사용자를 삭제하는 메서드
     * 관리자가 사용자의 enrollmentStatus를 prepending 으로 바꾼다.
     * pending -> prepending or enrolled -> prepending
     * @param member
     * @return
     */
    Object rejectRegister(Member member);

    /**
     * 관리자가 사용자의 등록 요청을 승인하는 메서드
     * 관리자가 사용자의 enrollmentStatus를 pending -> enrolled 로 바꾼다.
     */
    Object approveRegister(Member member);

    /**
     * 관리자가 등록 관리 페이지에서 사용자의 세부 정보를 조회하는 메서드
     */
    RegisterResponseDTO.MemberInfoResponseDTO getMemberInfo(String accountId);

    /**
     * enrollmentStatus로 사용자 리스트를 가져오는 메서드
     * enrollmentStatus에 따라 리턴하는 사용자 리스트가 바뀐다.
     */
    Page<Member> getMembersByEnrollmentStatus(Integer page, EnrollmentStatus enrollmentStatus);

    /**
     * 검색 키워드로 사용자를 찾는 메서드
     * 검색 키워드는 accountId 또는 employeeNo 만 가능하다.
     */
    Page<Member> searchMemberByEnrollmentStatusAndKeyword(Integer page, String keyword, EnrollmentStatus enrollmentStatus);
}
