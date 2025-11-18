package com.gdg.oauthgooglelogin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //외부()에서 객체 함부로 생성 못하도록 함
@EntityListeners(AuditingEntityListener.class)
public class Memo { //DB에서 쓸 memo

    @Id
    @Column(name = "memo_Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "memo_content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private User user;

    @Builder
    public Memo(Long id, String content, User user) {
        this.id = id;
        this.content = content;
        this.user = user;
    }

    public void update(String content) {
        this.content = content;
    }
}
