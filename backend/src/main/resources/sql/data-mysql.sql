/**
  멤버 데이터 방식 1
  - 데이터가 있을 경우에는 수정 날짜 변경
  - 데이터가 없을 경우에는 데이터 저장
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
INSERT INTO book(`id`, `title`, `author`, `kind`, `discription`, `image`)
    SELECT 12345678, 'title', 'author', 'kind', 'discription', 'image' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 12345678) LIMIT 1;
INSERT INTO book(`id`, `title`, `author`, `kind`, `discription`, `image`)
    SELECT 11111111, 'title2', 'author2', 'kind2', 'discription', 'image2' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 11111111) LIMIT 1;
INSERT INTO book(`id`, `title`, `author`, `kind`, `discription`, `image`)
    SELECT 11111112, 'title3', 'author3', 'kind3', 'discription', 'image3' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 11111112) LIMIT 1;
INSERT INTO book(`id`, `title`, `author`, `kind`, `discription`, `image`)
    SELECT 11111311, 'title4', 'author4', 'kind4', 'discription', 'image4' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 11111311) LIMIT 1;
INSERT INTO book(`id`, `title`, `author`, `kind`, `discription`, `image`)
    SELECT 11141311, 'title5', 'author5', 'kind5', 'discription', 'image5' FROM DUAL
    WHERE NOT EXISTS (SELECT `id` FROM book WHERE id = 11141311) LIMIT 1;

