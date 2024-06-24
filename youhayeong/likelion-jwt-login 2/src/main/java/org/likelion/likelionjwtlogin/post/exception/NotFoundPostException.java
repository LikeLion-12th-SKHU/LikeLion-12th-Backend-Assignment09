package org.likelion.likelionjwtlogin.post.exception;

public class NotFoundPostException extends RuntimeException{
    public NotFoundPostException(final String message) {
        super(message);
    }

    public NotFoundPostException() {
        this("게시물을 찾을 수 없습니다.");
    }
}
