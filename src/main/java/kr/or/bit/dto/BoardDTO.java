package kr.or.bit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private int boardId;
    private String writer;
    private String password;
    private String title;
    private String content;
    private int readCount;
    private int ref;
    private int reStep;
    private int reLevel;
    private String isDeleted;
    private String createdAt;
    private int commentCount;
}
