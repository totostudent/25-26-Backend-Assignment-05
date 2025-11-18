package com.gdg.oauthgooglelogin.controller;

import com.gdg.oauthgooglelogin.dto.memo.MemoCreateRequest;
import com.gdg.oauthgooglelogin.dto.memo.MemoInfoResponse;
import com.gdg.oauthgooglelogin.dto.memo.MemoUpdateRequest;
import com.gdg.oauthgooglelogin.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memo")
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    public ResponseEntity<MemoInfoResponse> createMemo(@RequestBody MemoCreateRequest memoCreateRequest) {
        return ResponseEntity.created(URI.create("/memo/")).body(memoService.createMemo(memoCreateRequest));
    }

    @GetMapping("/{memoId}")
    public ResponseEntity<MemoInfoResponse> getMemo(@PathVariable Long memoId) {
        return ResponseEntity.ok(memoService.getMemoInfo(memoId));
    }

    @PatchMapping("/{memoId}")
    public ResponseEntity<MemoInfoResponse> updateMemo(@RequestBody Long memoId, @RequestBody MemoUpdateRequest memoUpdateRequest) {
        return ResponseEntity.ok(memoService.updateMemo(memoId, memoUpdateRequest));
    }

    @DeleteMapping("/{memoId}")
    public ResponseEntity<MemoInfoResponse> deleteMemo(@PathVariable Long memoId) {
        memoService.deleteMemo(memoId);
        return ResponseEntity.noContent().build();
    }
}
