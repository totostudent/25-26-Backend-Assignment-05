package com.gdg.oauthgooglelogin.dto.memo;

import com.gdg.oauthgooglelogin.domain.Memo;
import lombok.Builder;

@Builder
public record MemoInfoResponse (
    Long id,
    String content
) {
    public static MemoInfoResponse fromEntity(Memo memo) {
        return MemoInfoResponse.builder()
                .id(memo.getId())
                .content(memo.getContent())
                .build();
    }
}
