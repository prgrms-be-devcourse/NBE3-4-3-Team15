/**
  멤버 데이터 방식 1
  - 데이터가 있을 경우에는 수정 날짜 변경
  - 데이터가 없을 경우에는 데이터 저장
  - 멤버의 비밀번호는 최소 8자리 이상이여야함
   */
insert into member(`username`, `created_at`, `modified_at`, `birth`, `email`, `gender`, `nickname`, `password`)
    VALUES ('admin', now(), null, null, 'admin@admin.com', 0, '관리자', 'admin'),
           ('user1', now(), null, null, 'user1@users.com', 0, '유저1', '1234'),
           ('user2', now(), null, null, 'user2@users.com', 1, '유저2', '1234'),
           ('user3', now(), null, null, 'user3@users.com', 1, '유저3', '1234')
    ON DUPLICATE KEY UPDATE modified_at = now();
/**
  멤버 데이터 방식 2
  - 데이터가 없는 경우에만 데이터 추가
 */
INSERT INTO member(`username`, `created_at`, `modified_at`, `birth`, `email`, `gender`, `nickname`, `password`)
    SELECT 'user4', now(), null, null, 'user4@users.com', 0, '유저4', '1234' FROM DUAL
    WHERE NOT EXISTS (SELECT `username` FROM member WHERE username = 'user4') LIMIT 1;

/**
  책 데이터
  - 책 데이터가 있을 때는 넣고 없을 때는 아무일도 일어나지 않음
  - 데이터를 하나씩 추가하는 방식
 */
INSERT INTO book(`isbn`, `title`, `author`, `description`, `image`, `favorite_count`)
    SELECT 12345678, 'title1', 'author1', 'description1', 'image1', '3' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 12345678) LIMIT 1;
INSERT INTO book(`isbn`, `title`, `author`, `description`, `image`, `favorite_count`)
    SELECT 11111111, 'title2', 'author2', 'description2', 'image2', '2' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 11111111) LIMIT 1;
INSERT INTO book(`isbn`, `title`, `author`, `description`, `image`, `favorite_count`)
    SELECT 11111112, 'title3', 'author3', 'description3', 'image3', '1' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 11111112) LIMIT 1;
INSERT INTO book(`isbn`, `title`, `author`, `description`, `image`, `favorite_count`)
    SELECT 11111311, 'title4', 'author4', 'description4', 'image4', '0' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 11111311) LIMIT 1;
INSERT INTO book(`isbn`, `title`, `author`, `description`, `image`, `favorite_count`)
    SELECT 11141311, 'title5', 'author5', 'description5', 'image5', '0' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 11141311) LIMIT 1;

/**
  찜 데이터
  - 특정 유저가 특정 책을 찜한 데이터
  - 중복 데이터가 없을 경우에만 추가됨
 */
INSERT INTO favorite(`book_id`, `member_username`)
    SELECT 1, 'user1' FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE book_id = 12345678 AND member_username = 'user1') LIMIT 1;
INSERT INTO favorite(`book_id`, `member_username`)
    SELECT 1, 'user2' FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE book_id = 12345678 AND member_username = 'user2') LIMIT 1;
INSERT INTO favorite(`book_id`, `member_username`)
    SELECT 1, 'user3' FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE book_id = 12345678 AND member_username = 'user3') LIMIT 1;
INSERT INTO favorite(`book_id`, `member_username`)
    SELECT 2, 'user1' FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE book_id = 11111111 AND member_username = 'user1') LIMIT 1;
INSERT INTO favorite(`book_id`, `member_username`)
    SELECT 2, 'user2' FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE book_id = 11111111 AND member_username = 'user2') LIMIT 1;
INSERT INTO favorite(`book_id`, `member_username`)
    SELECT 3, 'user1' FROM DUAL
    WHERE NOT EXISTS (SELECT 1 FROM favorite WHERE book_id = 11111112 AND member_username = 'user1') LIMIT 1;
