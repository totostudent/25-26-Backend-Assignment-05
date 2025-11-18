package com.gdg.oauthgooglelogin.repository;

import com.gdg.oauthgooglelogin.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {
}
