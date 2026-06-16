-- Board table for 4Minute project
-- Oracle / MVC2 / Servlet + JSP + JDBC

DROP SEQUENCE seq_reply_id;
DROP SEQUENCE seq_jspboard_id;
DROP TABLE reply;
DROP TABLE jspboard ;

CREATE TABLE jspboard (
    board_id NUMBER PRIMARY KEY,
    writer VARCHAR2(50) NOT NULL,
    password VARCHAR2(50) NOT NULL,
    title VARCHAR2(200) NOT NULL,
    content VARCHAR2(4000) NOT NULL,
    read_count NUMBER DEFAULT 0 NOT NULL,
    ref NUMBER DEFAULT 0 NOT NULL,
    re_step NUMBER DEFAULT 0 NOT NULL,
    re_level NUMBER DEFAULT 0 NOT NULL,
    is_deleted CHAR(1) DEFAULT 'N' NOT NULL,
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT chk_jspboard_deleted CHECK (is_deleted IN ('N', 'Y'))
);

CREATE TABLE reply (
    comment_id NUMBER PRIMARY KEY,
    board_id NUMBER NOT NULL,
    writer VARCHAR2(50) NOT NULL,
    password VARCHAR2(50) NOT NULL,
    content VARCHAR2(4000) NOT NULL,
    created_at DATE DEFAULT SYSDATE NOT NULL,
    updated_at DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_reply_board FOREIGN KEY (board_id) REFERENCES jspboard(board_id)
);

CREATE SEQUENCE seq_jspboard_id START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_reply_id START WITH 1 INCREMENT BY 1;

CREATE INDEX idx_jspboard_post_list ON jspboard (re_level, is_deleted, board_id);
CREATE INDEX idx_reply_board_id ON reply (board_id, comment_id);