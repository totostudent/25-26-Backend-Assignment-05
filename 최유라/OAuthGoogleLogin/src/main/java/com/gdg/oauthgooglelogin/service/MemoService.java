package com.gdg.oauthgooglelogin.service;

import com.gdg.oauthgooglelogin.domain.Memo;
import com.gdg.oauthgooglelogin.dto.memo.MemoCreateRequest;
import com.gdg.oauthgooglelogin.dto.memo.MemoInfoResponse;
import com.gdg.oauthgooglelogin.dto.memo.MemoUpdateRequest;
import com.gdg.oauthgooglelogin.exception.CustomException;
import com.gdg.oauthgooglelogin.exception.ErrorCode;
import com.gdg.oauthgooglelogin.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserService userService;

    @Transactional
    public MemoInfoResponse createMemo(Principal principal, MemoCreateRequest memoCreateRequest) {
        Memo memo = memoRepository.save(Memo.builder()
                    .content(memoCreateRequest.content())
                    .user(userService.getUserEntity(Long.parseLong(principal.getName())))
                    .build());

        return MemoInfoResponse.fromEntity(memo);
    }

    @Transactional(readOnly = true)
    public MemoInfoResponse getMemoInfo(Long MemoId) { //memo 정보 가져오기
        return MemoInfoResponse.fromEntity(getMemo(MemoId));
    }

    @Transactional
    public MemoInfoResponse updateMemo(Principal principal, Long MemoId, MemoUpdateRequest memoUpdateRequest) {
        Memo memo = getMemo(MemoId);
        validateAuthor(principal, memo);

        memo.update(
                memoUpdateRequest.content() == null ? memo.getContent() : memoUpdateRequest.content()
        );
        return MemoInfoResponse.fromEntity(memo);
    }

    @Transactional
    public void deleteMemo(Principal principal, Long MemoId) {
        Memo memo = getMemo(MemoId);
        validateAuthor(principal, memo);

        memoRepository.delete(memo);
    }

    private Memo getMemo(Long MemoId) { //이건 무슨 용도지..MemoService 에서 쓰는 메소드 정의해 놓은 걸까..
        return memoRepository.findById(MemoId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND)); //존재하지 않는 메모
    }

    private void validateAuthor(Principal principal, Memo memo) {
        if (!memo.getUser().getId().equals(Long.parseLong(principal.getName()))) {
            throw new CustomException(ErrorCode.HAVE_NO_ROLE);
        }
    }
}
